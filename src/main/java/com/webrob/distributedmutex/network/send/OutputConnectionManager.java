package com.webrob.distributedmutex.network.send;

import com.webrob.distributedmutex.model.Message;
import com.webrob.distributedmutex.model.MessageManager;
import com.webrob.distributedmutex.model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void sendMessagesToAllNodes(Message message)
    {
        for(String id : nodesQueue.keySet())
        {
            BlockingQueue<Message> queue = nodesQueue.get(id);
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

    public void sendOkMessageToNode(String id, Long clock)
    {
        BlockingQueue<Message> messages = nodesQueue.get(id);
        Message message = MessageManager.getOkMessage(id, clock);
        try
        {
            messages.put(message);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessageToNode(String id, Message message)
    {
	BlockingQueue<Message> messages = nodesQueue.get(id);
	if (messages!= null)
	{
	    try
	    {
		messages.put(message);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	}
    }
}

