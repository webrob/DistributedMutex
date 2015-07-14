package com.webrob.distributedmutex.model;

import com.webrob.distributedmutex.network.AllConnectionsManager;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Robert on 2015-01-04.
 */
public class CriticalSectionManager implements Runnable
{
    private final BlockingQueue<Message> messages;
    private boolean isRunning = true;
    private CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();

    public CriticalSectionManager(BlockingQueue<Message> messages, AllConnectionsManager allConnectionsManager)
    {
	this.messages = messages;
        criticalSection.setAllConnectionManager(allConnectionsManager);
    }


    private void serveMessage(Message message)
    {
        switch (message.getType())
        {
            case REQUEST:
            {
                criticalSection.serveRequestMessage(message);
                break;
            }
            case OK:
            {
                criticalSection.serveOkMessage(message);
                break;
            }
            case INTERNAL_REQUEST:
            {
                criticalSection.requestEnterToSection();
                break;
            }
            case INTERNAL_LEAVE:
            {
                criticalSection.leaveSection();
                break;
            }
            case INIT:
            {
                criticalSection.initMessage(message);
            }
        }
    }


    @Override public void run()
    {
        while (isRunning)
        {
            try
            {
                Message message = messages.take();
                serveMessage(message);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
