package com.RAmutex.network;

import com.RAmutex.utils.TextAreaControllerSingleton;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class MessageReceiver implements Runnable
{

    private TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
    private Scanner messageScanner;
    private boolean isWorking = true;
    private Socket clientSocket;

    public MessageReceiver(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            messageScanner = new Scanner(new InputStreamReader(clientSocket.getInputStream())).useDelimiter("\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isWorking) {
           listenForClientMessages();
        }
    }

    private void listenForClientMessages()
    {
        String hostAddress = clientSocket.getInetAddress().getHostAddress();
        while (messageScanner.hasNext())
        {
            String line = messageScanner.next();
            singleton.showReceivedDataMessage(line + " received from " +hostAddress);
        }
    }
}
