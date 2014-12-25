package com.RAmutex.network;

import com.RAmutex.model.Message;
import com.RAmutex.model.Node;
import com.RAmutex.utils.GlobalParameters;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Robert on 2014-12-12.
 */
public class OutputConnectionManager
{
    private ExecutorService connectionsService;
    private List<Node> nodes;
    private List<MessageSender> messageSenders = new ArrayList<>();

    public OutputConnectionManager(List<Node> nodes)
    {
	this.nodes = nodes;
	connectionsService = Executors.newFixedThreadPool(30);
    }

    public void connectToAllServers()
    {
	nodes.forEach(this::connectToServer);
    }

    private void connectToServer(Node node)
    {
        MessageSender sender = new MessageSender(node);
        connectionsService.submit(sender);
        //sender.start();
        messageSenders.add(sender);
    }

    public void sendMessagesToAllNodes(final Message message)
    {
	for (MessageSender sender : messageSenders)
	{
	    sender.writeMessageToClient(message.toString());
	}
    }

    public void closeSockets()
    {
        messageSenders.forEach(MessageSender::stopRunning);
        connectionsService.shutdownNow();
    }
}
