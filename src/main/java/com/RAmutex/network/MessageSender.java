package com.RAmutex.network;

import com.RAmutex.model.Node;
import com.RAmutex.utils.GlobalParameters;
import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessageSender implements Runnable
{
    TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private Node node;
    private DataOutputStream outToServer;

    public MessageSender(Node node)
    {
        this.node = node;
    }

    @Override
    public void run() {
        establishConnection(node);
        singleton.showApplicationStateMessage("connected to  " + node);
    }

    private void establishConnection(Node node) {
        while (true)
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

    private void tryToConnect(Node node) throws IOException
    {
        singleton.showApplicationStateMessage("Trying to connect to " + node);
        Socket outputSocket = new Socket(node.getIP(), node.getPort());
        outputSocket.setTcpNoDelay(true);
        outToServer = new DataOutputStream(outputSocket.getOutputStream());
    }

    private void showMessageAndSleep()
    {
        try
        {
            singleton.showApplicationStateMessage("Can't connect to " + node);
            Thread.sleep(GlobalParameters.reconnectionPeriod);
        }
        catch (InterruptedException ignored)
        {
        }
    }

    public synchronized void writeMessageToClient(String message) {
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
    }
}
