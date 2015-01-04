package com.RAmutex.network.receive;

import com.RAmutex.model.JSONtoMessageConverter;
import com.RAmutex.model.Message;
import com.RAmutex.ui.TextAreaControllerSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageReceiver extends Thread
{
    private BufferedReader bufferedReader;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private boolean isWorking = true;
    private Socket clientSocket;
    private final BlockingQueue<Message> criticalSectionQueue;


    public MessageReceiver(Socket clientSocket, BlockingQueue<Message> criticalSectionQueue)
    {
	this.clientSocket = clientSocket;
	this.criticalSectionQueue = criticalSectionQueue;
	try
	{
	    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
	    while (isWorking)
	    {
		listenForClientMessages();
	    }
	}
	catch (IOException | InterruptedException e)
	{
	    e.printStackTrace();
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
	String hostAddress = clientSocket.getInetAddress().getHostAddress();
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
