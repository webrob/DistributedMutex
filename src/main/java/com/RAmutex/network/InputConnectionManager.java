package com.RAmutex.network;

import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Robert on 2014-12-12.
 */
public class InputConnectionManager implements Runnable
{
    private int myPortToListen;
    private ExecutorService connectionsService;
    private List<MessageReceiver> messageReceivers = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean isWorking;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();

    public InputConnectionManager(int myPortToListen)
    {
	this.myPortToListen = myPortToListen;
	connectionsService = Executors.newFixedThreadPool(30);
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
	    MessageReceiver reader = new MessageReceiver(clientSocket);
	    connectionsService.submit(reader);
	    //reader.start();
	    messageReceivers.add(reader);
	    InetAddress hostAddress = clientSocket.getInetAddress();
	    singleton.showApplicationStateMessage("accepted connection from: " + hostAddress);
	}
    }

    protected void closeServerSocket()
    {
	try
	{
	    isWorking = false;
	    if (serverSocket!=null)
	    {
		serverSocket.close();
	    }
	    System.out.println("close server socket");
	    messageReceivers.forEach(MessageReceiver::stopWorking);
	    connectionsService.shutdownNow();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }


}
