package com.RAmutex.network.receive;

import com.RAmutex.model.CriticalSectionSingleton;
import com.RAmutex.model.JSONtoMessageConverter;
import com.RAmutex.model.Message;
import com.RAmutex.model.MessageManager;
import com.RAmutex.ui.TextAreaControllerSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class MessageReceiver extends Thread
{
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private boolean isWorking = true;
    private Socket clientSocket;
    private final BlockingQueue<Message> criticalSectionQueue;
    private String hostAddress;

    public MessageReceiver(Socket clientSocket, BlockingQueue<Message> criticalSectionQueue)
    {
	this.clientSocket = clientSocket;
	this.criticalSectionQueue = criticalSectionQueue;
	this.hostAddress = clientSocket.getInetAddress().getHostAddress();
	try
	{
	    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void run()
    {
	try
	{
	    sendInitMessage();

	    while (isWorking)
	    {
		listenForClientMessages();
	    }
	}
	catch (IOException | InterruptedException e)
	{
	    e.printStackTrace();
	    singleton.showApplicationStateMessage("connection lost with: " + hostAddress);
	}
    }

    private void sendInitMessage() throws IOException
    {
	CriticalSectionSingleton criticalSection = CriticalSectionSingleton.getInstance();
	Message initMessage = MessageManager
			.getInitMessage(criticalSection.getId(), criticalSection.getCurrentClock());
	printWriter.println(initMessage);
	if (printWriter.checkError())
	{
	    throw new IOException();
	}
	else
	{
	    singleton.showSentDataMessage(initMessage + " sent to " + hostAddress);
	}
    }

    public void stopWorking()
    {
	isWorking = false;
	try
	{
	    if (clientSocket != null)
	    {
		clientSocket.close();
		clientSocket = null;
	    }
	    if (bufferedReader != null)
	    {
		bufferedReader.close();
		bufferedReader = null;
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }

    private void listenForClientMessages() throws IOException, InterruptedException
    {
	if (bufferedReader != null)
	{
	    String line = bufferedReader.readLine();
	    Message message = JSONtoMessageConverter.convert(line);
	    criticalSectionQueue.put(message);

	    if (line != null)
	    {
		singleton.showReceivedDataMessage(line + " received from " + hostAddress);
	    }
	    else
	    {
		isWorking = false;
	    }
	}
	else
	{
	    isWorking = false;
	}
    }

}
