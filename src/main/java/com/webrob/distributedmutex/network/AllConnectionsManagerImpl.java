package com.webrob.distributedmutex.network;

import com.webrob.distributedmutex.model.CriticalSectionManager;
import com.webrob.distributedmutex.model.Message;
import com.webrob.distributedmutex.model.MessageType;
import com.webrob.distributedmutex.model.Node;
import com.webrob.distributedmutex.network.receive.InputConnectionManager;
import com.webrob.distributedmutex.network.send.OutputConnectionManager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 2014-12-12.
 */
public class AllConnectionsManagerImpl implements AllConnectionsManager
{
    private final BlockingQueue<Message> criticalSectionQueue = new LinkedBlockingQueue<>();
    private List<Node> nodes;
    private Node myNode;
    private OutputConnectionManager outputConnectionManager;

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
    public int getClientsAmount()
    {
        return nodes.size();
    }

    @Override public void sendMessageToNode(String id, Message message)
    {
        outputConnectionManager.sendMessageToNode(id, message);
    }

}
