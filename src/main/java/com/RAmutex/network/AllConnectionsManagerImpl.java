package com.RAmutex.network;

import com.RAmutex.model.CriticalSectionManager;
import com.RAmutex.model.Message;
import com.RAmutex.model.MessageType;
import com.RAmutex.model.Node;
import com.RAmutex.network.receive.InputConnectionManager;
import com.RAmutex.network.send.OutputConnectionManager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 2014-12-12.
 */
public class AllConnectionsManagerImpl implements AllConnectionsManager
{
    private List<Node> nodes;
    private Node myNode;
    private OutputConnectionManager outputConnectionManager;
    private final BlockingQueue<Message> criticalSectionQueue = new LinkedBlockingQueue<>();

    public AllConnectionsManagerImpl(List<Node> nodes, Node myNode)
    {
	this.nodes = nodes;
	this.myNode = myNode;
        CriticalSectionManager criticalSectionManager = new CriticalSectionManager(criticalSectionQueue, this);
        Thread thread = new Thread(criticalSectionManager);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public String getMyNodeId()
    {
        return myNode.getId();
    }


    public void startConnections()
    {
	startOutputConnections();
        startInputConnections();
    }

    private void startOutputConnections()
    {
	outputConnectionManager = new OutputConnectionManager(nodes);
	outputConnectionManager.connectToAllServers();
    }

    private void startInputConnections()
    {
        InputConnectionManager inputConnectionManager = new InputConnectionManager(myNode.getPort(),
                        criticalSectionQueue);
        Thread thread = new Thread(inputConnectionManager);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void wantEnterToSection()
    {
        Message message = new Message();
        message.setType(MessageType.INTERNAL_REQUEST.toString());
        try
        {
            criticalSectionQueue.put(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveSection()
    {
        Message message = new Message();
        message.setType(MessageType.INTERNAL_LEAVE.toString());
        try
        {
            criticalSectionQueue.put(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBroadcastEnterMessage(Message message)
    {
        outputConnectionManager.sendMessagesToAllNodes(message);
    }

    @Override
    public void closeAllSockets()
    {
        //inputConnectionManager.closeServerSocket();
        //outputConnectionManager.closeSockets();
    }

    @Override
    public int getClientsAmount()
    {
        return nodes.size();
    }

    @Override
    public void sendOkMessageToNode(String id, Long clock)
    {
        outputConnectionManager.sendOkMessageToNode(id, clock);
    }

}
