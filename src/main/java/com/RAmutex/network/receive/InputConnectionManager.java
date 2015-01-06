package com.RAmutex.network.receive;

import com.RAmutex.model.JSONtoMessageConverter;
import com.RAmutex.model.Message;
import com.RAmutex.model.MessageType;
import com.RAmutex.ui.TextAreaControllerSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	    InetAddress hostAddress = clientSocket.getInetAddress();

	    try
	    {
		receiveInitMessage(clientSocket, hostAddress.toString());
		MessageReceiver reader = new MessageReceiver(clientSocket, criticalSectionQueue);
		reader.setDaemon(true);
		reader.start();

		singleton.showApplicationStateMessage("accepted connection from: " + hostAddress);
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }

	}
    }

    private void receiveInitMessage(Socket clientSocket, String hostAddress) throws IOException
    {
	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	String initLine = bufferedReader.readLine();
	Message message = JSONtoMessageConverter.convert(initLine);
	while (message.getType() != MessageType.INIT)
	{
	    initLine = bufferedReader.readLine();
	    message = JSONtoMessageConverter.convert(initLine);
	    singleton.showReceivedDataMessage(message + " received from " + hostAddress);
	}
	singleton.showReceivedDataMessage(message + " received from " + hostAddress);
	try
	{
	    criticalSectionQueue.put(message);
	}
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
	//CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();
	//criticalSection.updateClock(message.getClock());

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
