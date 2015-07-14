package com.webrob.distributedmutex.network.receive;

import com.webrob.distributedmutex.model.JsonToMessageConverter;
import com.webrob.distributedmutex.model.Message;
import com.webrob.distributedmutex.model.MessageType;
import com.webrob.distributedmutex.ui.TextAreaControllerSingleton;
import com.webrob.distributedmutex.utils.TimeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class MessageReceiver extends Thread
{
    private BufferedReader bufferedReader;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private boolean isWorking = true;
    private final BlockingQueue<Message> criticalSectionQueue;
    private String hostAddress;

    public MessageReceiver(Socket clientSocket, BlockingQueue<Message> criticalSectionQueue)
    {
	this.criticalSectionQueue = criticalSectionQueue;
	this.hostAddress = clientSocket.getInetAddress().getHostAddress();
	try
	{
	    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	    PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
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
	    singleton.showApplicationStateMessage("connection lost with: " + hostAddress);
	}
    }

    private void listenForClientMessages() throws IOException, InterruptedException
    {
	if (bufferedReader != null)
	{
	    String line = bufferedReader.readLine();
	    Message message = JsonToMessageConverter.convert(line);

	    if (message.getType() != MessageType.INIT)
	    {
		criticalSectionQueue.put(message);
	    }

	    if (line != null)
	    {

		String now = TimeHelper.getCurrentHourWithMiliSec();
		singleton.showReceivedDataMessage(now + " " + line + " received from " + hostAddress);
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
