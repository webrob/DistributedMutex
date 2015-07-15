package com.webrob.distributedmutex.network.send;

import com.webrob.distributedmutex.model.*;
import com.webrob.distributedmutex.ui.TextAreaControllerSingleton;
import com.webrob.distributedmutex.utils.GlobalParameters;
import com.webrob.distributedmutex.utils.TimeHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class MessageSender extends Thread
{
    private final BlockingQueue<Message> queue;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private Node node;
    private PrintWriter printWriter;
    private boolean isRunning = true;
    private InitState initState = InitState.NOT_SENT;
    private CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();
    private boolean canSentByCritical = false;

    public MessageSender(Node node, BlockingQueue<Message> queue)
    {
	this.node = node;
	this.queue = queue;
    }

    @Override public void run()
    {
	establishConnection(node);
	sendInitMessage();
	serveQueue();
    }

    private void sendInitMessage()
    {
	Message initMessage = MessageManager.getInitMessage(criticalSection.getId(), criticalSection.getCurrentClock());

	writeMessageToClient(initMessage);
    }

    private void serveQueue()
    {
	while (isRunning)
	{
	    try
	    {
		Message message = queue.take();
		serveInternalMessage(message);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    private void serveInternalMessage(Message message)
    {
	if (message.getType() == MessageType.INIT && canSentByCritical)
	{
	    writeMessageToClient(message);
	}
	else if (message.getType() != MessageType.INIT)
	{
	    writeMessageToClient(message);
	}

	if (message.getType() == MessageType.INIT)
	{
	    if (initState == InitState.SENT)
	    {
		canSentByCritical = true;
	    }
	}
    }

    private void establishConnection(Node node)
    {
	initState = InitState.NOT_SENT;
	while (isRunning)
	{
	    try
	    {
		tryToConnect(node);
		singleton.showApplicationStateMessage("connected to  " + node);

		break;
	    }
	    catch (IOException e)
	    {
		showMessageAndSleep();
	    }
	}

	initState = InitState.SENT;
    }

    private void tryToConnect(Node node) throws IOException
    {
	Socket outputSocket = new Socket(node.getIP(), node.getPort());
	printWriter = new PrintWriter(outputSocket.getOutputStream(), true);
    }

    private void showMessageAndSleep()
    {
	try
	{
	    Thread.sleep(GlobalParameters.reconnectionPeriod);
	}
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	}

    }

    private void writeMessageToClient(Message message)
    {
	printWriter.println(message);
	if (printWriter.checkError())
	{
	    singleton.showApplicationStateMessage("No output connection -- reconnecting");
	    establishConnection(node);
	    if (message.getType() == MessageType.INIT)
	    {
		writeMessageToClient(message);
	    }
	}
	else
	{
	    String now = TimeHelper.getCurrentHourWithMiliSec();
	    singleton.showSentDataMessage(now + " " + message + " sent to " + node);
	}
    }
}
