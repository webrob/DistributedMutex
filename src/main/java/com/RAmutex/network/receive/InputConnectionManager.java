package com.RAmutex.network.receive;

import com.RAmutex.model.Message;
import com.RAmutex.ui.TextAreaControllerSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Robert on 2014-12-12.
 */
public class InputConnectionManager implements Runnable
{
    private int myPortToListen;
    private ServerSocket serverSocket;
    private boolean isWorking;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private final BlockingQueue<Message> criticalSectionQueue;

    public InputConnectionManager(int myPortToListen, BlockingQueue<Message> criticalSectionQueue)
    {
	this.myPortToListen = myPortToListen;
	this.criticalSectionQueue = criticalSectionQueue;
    }

    @Override
    public void run()
    {
	try
	{
	    serverSocket = new ServerSocket(myPortToListen);
	    isWorking = true;
	    listenForClientsToConnect();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    try
	    {
		serverSocket.close();
		serverSocket = null;
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    private void listenForClientsToConnect() throws IOException
    {
	while (isWorking)
	{
	    Socket clientSocket = serverSocket.accept();
	    MessageReceiver reader = new MessageReceiver(clientSocket, criticalSectionQueue);
	    reader.setDaemon(true);
	    reader.start();
	    InetAddress hostAddress = clientSocket.getInetAddress();
	    singleton.showApplicationStateMessage("accepted connection from: " + hostAddress);
	}
    }

    protected void closeServerSocket()
    {
	/*
	try
	{
	    isWorking = false;
	    if (serverSocket!=null)
	    {
		serverSocket.close();
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	*/
    }


}
