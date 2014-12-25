package com.RAmutex.network;

import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver extends Thread
{
    private BufferedReader bufferedReader;
    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private boolean isWorking = true;
    private Socket clientSocket;

    public MessageReceiver(Socket clientSocket)
    {
	this.clientSocket = clientSocket;
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
	catch (IOException e)
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

    private void listenForClientMessages() throws IOException
    {
	String hostAddress = clientSocket.getInetAddress().getHostAddress();
	if (bufferedReader != null)
	{
	    String line = bufferedReader.readLine();
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
