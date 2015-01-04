package com.RAmutex.network;

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
    private BlockingQueue<Message> criticalSectionQueue = new LinkedBlockingQueue<>();

    public AllConnectionsManagerImpl(List<Node> nodes, Node myNode)
    {
	this.nodes = nodes;
	this.myNode = myNode;
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

    public void sendBroadcastEnterMessage()
    {
        Message message = new Message("1",1l, MessageType.REQUEST);
        outputConnectionManager.sendMessagesToAllNodes(message);
    }

    public void closeAllSockets()
    {
        //inputConnectionManager.closeServerSocket();
        //outputConnectionManager.closeSockets();
    }



}
