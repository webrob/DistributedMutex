package com.RAmutex.network;

import com.RAmutex.model.Node;
import com.RAmutex.utils.GlobalParameters;
import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MessageSender extends Thread
{
    TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private Node node;
    private PrintWriter printWriter;
    private boolean isRunning = true;
    private Socket outputSocket;

    public MessageSender(Node node)
    {
	this.node = node;
    }

    @Override
    public void run()
    {
	establishConnection(node);
	singleton.showApplicationStateMessage("connected to  " + node);
    }

    private void establishConnection(Node node)
    {
	while (isRunning)
	{
	    try
	    {
		tryToConnect(node);
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
	outputSocket.setTcpNoDelay(true);
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

    public synchronized void writeMessageToClient(String message)
    {
	if (printWriter != null)
	{
	    printWriter.println(message);
	    singleton.showSentDataMessage(message+ " sent to " + node);
	}
	else
	{
	    singleton.showSentDataMessage("No output connection");
	}

        /*
	if (outToServer != null) {
            try {
                outToServer.writeBytes(message + "\n");
                singleton.showSentDataMessage(message+ " sent to " + node);
            } catch (IOException e) {
                singleton.showSentDataMessage("Connection has been broken! Reconnecting...");
                establishConnection(node);
                singleton.showSentDataMessage("Repeating message");
                writeMessageToClient(message);
                //writeMessageToClient(message);  //TODO: To make sure that it went.
            }
        } else {
            singleton.showSentDataMessage("No output connection");
        }
        */
    }
}
