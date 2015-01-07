package com.RAmutex.network.send;

import com.RAmutex.model.*;
import com.RAmutex.ui.TextAreaControllerSingleton;
import com.RAmutex.utils.GlobalParameters;

import java.io.IOException;
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
    private InitState initState = InitState.notSent;
    CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();


    private enum InitState {
	notSent , sentBySender, sentByCriticalSection
    }

    public MessageSender(Node node, BlockingQueue<Message> queue)
    {
	this.node = node;
	this.queue = queue;
    }

    @Override
    public void run()
    {
	establishConnection(node);
	sendInitMessage();
	serveQueue();
    }

    private void sendInitMessage()
    {
	Message initMessage = MessageManager
			.getInitMessage(criticalSection.getId(), criticalSection.getCurrentClock());

	writeMessageToClient(initMessage);
    }

    private void serveQueue()
    {
	while (isRunning)
	{
	    try
	    {
		System.out.println("przed take");
		Message message = queue.take();
		System.out.println("po take");
		serveInternalMessage(message);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    private boolean canSentByCritical = false;

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
	    if (initState == InitState.sentBySender)
	    {
		canSentByCritical = true;
	    }
	}
    }

    private void establishConnection(Node node)
    {
	initState = InitState.notSent;
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

	initState = InitState.sentBySender;
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
