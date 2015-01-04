package com.RAmutex.network.send;

import com.RAmutex.model.Message;
import com.RAmutex.model.Node;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Robert on 2014-12-12.
 */
public class OutputConnectionManager
{
    private List<Node> nodes;
    private Map<String, BlockingQueue<Message>> nodesQueue = new HashMap<>();

    public OutputConnectionManager(List<Node> nodes)
    {
	this.nodes = nodes;
    }

    public void connectToAllServers()
    {
	nodes.forEach(this::connectToServer);
    }

    private void connectToServer(Node node)
    {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        String id = node.getId();
        nodesQueue.put(id, queue);

        MessageSender sender = new MessageSender(node, queue);
        sender.setDaemon(true);
        sender.start();
    }

    public void sendMessagesToAllNodes(final Message message)
    {
        for(String string : nodesQueue.keySet())
        {
            BlockingQueue<Message> queue = nodesQueue.get(string);
            try
            {
                queue.put(message);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void closeSockets()
    {

    }
}
