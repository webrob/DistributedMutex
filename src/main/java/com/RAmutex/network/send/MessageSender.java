package com.RAmutex.network.send;

import com.RAmutex.model.*;
import com.RAmutex.ui.TextAreaControllerSingleton;
import com.RAmutex.utils.GlobalParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class MessageSender extends Thread
{
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private Node node;
    private PrintWriter printWriter;
    private boolean isRunning = true;
    private Socket outputSocket;
    private final BlockingQueue<Message> queue;

    public MessageSender(Node node, BlockingQueue<Message> queue)
    {
	this.node = node;
	this.queue = queue;
    }

    @Override
    public void run()
    {
	establishConnection(node);
	serveQueue();
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
	writeMessageToClient(message);
    }

    private void establishConnection(Node node)
    {
	while (isRunning)
	{
	    try
	    {
		tryToConnect(node);
		singleton.showApplicationStateMessage("connected to  " + node);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(outputSocket.getInputStream()));
		String initLine = bufferedReader.readLine();
		Message message = JSONtoMessageConverter.convert(initLine);
		singleton.showReceivedDataMessage(message + " received from " + node.getIP());
		if (message.getType() == MessageType.INIT)
		{
		    CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();
		    criticalSection.updateClock(message.getClock());
		}
		else
		{
		    throw new IOException();
		}
		break;
	    }
	    catch (IOException e)
	    {
		showMessageAndSleep();
	    }
	}
    }

    public void stopRunning()
    {
	isRunning = false;
	try
	{
	    if (outputSocket != null)
	    {
		outputSocket.close();
		outputSocket = null;
	    }
	    if (printWriter != null)
	    {
		printWriter.close();
		outputSocket = null;
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    private void tryToConnect(Node node) throws IOException
    {
	singleton.showApplicationStateMessage("Trying to connect to " + node);
	outputSocket = new Socket(node.getIP(), node.getPort());
	printWriter = new PrintWriter(outputSocket.getOutputStream(), true);
    }

    private void showMessageAndSleep()
    {
	try
	{
	    singleton.showApplicationStateMessage("Can't connect to " + node);
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
	    writeMessageToClient(message);
	}
	else
	{
	    singleton.showSentDataMessage(message + " sent to " + node);
	}
    }
}
