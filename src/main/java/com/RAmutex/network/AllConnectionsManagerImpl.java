package com.RAmutex.network;

import com.RAmutex.model.Message;
import com.RAmutex.model.MessageType;
import com.RAmutex.model.Node;

import java.util.List;

/**
 * Created by Robert on 2014-12-12.
 */
public class AllConnectionsManagerImpl implements AllConnectionsManager
{
    private List<Node> nodes;
    private Node myNode;
    private OutputConnectionManager outputConnectionManager;
    private InputConnectionManager inputConnectionManager;

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
        inputConnectionManager = new InputConnectionManager(myNode.getPort());
        new Thread(inputConnectionManager).start();
    }

    public void sendBroadcastEnterMessage()
    {
        Message message = new Message(1l, MessageType.order);
        outputConnectionManager.sendMessagesToAllNodes(message);
    }

    public void closeAllSockets()
    {
        closeServerSocket();
        outputConnectionManager.closeSockets();
    }

    protected void closeServerSocket()
    {
        inputConnectionManager.closeServerSocket();
    }

}
