package com.RAmutex.network;

import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Robert on 2014-12-12.
 */
public class InputConnectionManager implements Runnable
{
    private int myPortToListen;
    private ExecutorService connectionsService;
    private ServerSocket socket;
    private boolean isWorking;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();

    public InputConnectionManager(int myPortToListen)
    {
	this.myPortToListen = myPortToListen;
	connectionsService = Executors.newFixedThreadPool(30);
    }

    @Override public void run()
    {
	try
	{
	    socket = new ServerSocket(myPortToListen);
	    isWorking = true;
	    listenForClientsToConnect();
	}
	catch (IOException ignored)
	{
	}
    }

    private void listenForClientsToConnect() throws IOException
    {
	while (isWorking)
	{
	    Socket clientSocket = socket.accept();
	    MessageReceiver reader = new MessageReceiver(clientSocket);
	    connectionsService.submit(reader);
	    InetAddress hostAddress = clientSocket.getInetAddress();
	    singleton.showApplicationStateMessage("accepted connection from: " + hostAddress);
	}
    }
}
