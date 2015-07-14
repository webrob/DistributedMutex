package com.webrob.distributedmutex.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Created by Robert on 2014-12-13.
 */
public class TextAreaControllerSingleton
{
    private TextArea applicationStateTextArea = new TextArea();
    private TextArea receivedDataTextArea = new TextArea();
    private TextArea sentDataTextArea = new TextArea();


    private static volatile TextAreaControllerSingleton instance = null;


    private TextAreaControllerSingleton()
    {
    }

    public static TextAreaControllerSingleton getInstance()
    {
	if (instance == null)
	{
	    synchronized (TextAreaControllerSingleton.class)
	    {
		if (instance == null)
		{
		    instance = new TextAreaControllerSingleton();
		}
	    }
	}
	return instance;
    }


    public void showApplicationStateMessage(String message)
    {
	Platform.runLater(() -> applicationStateTextArea.appendText(getMessageWithNewLine(message)));
    }

    public void showReceivedDataMessage(String message)
    {
	Platform.runLater(() -> receivedDataTextArea.appendText(getMessageWithNewLine(message)));
    }

    public void showSentDataMessage(String message)
    {
	Platform.runLater(() -> sentDataTextArea.appendText(getMessageWithNewLine(message)));
    }

    private String getMessageWithNewLine(String message)
    {
	return message + "\n";
    }

    public void setApplicationStateTextArea(TextArea applicationStateTextArea)
    {
	this.applicationStateTextArea = applicationStateTextArea;
    }

    public void setReceivedDataTextArea(TextArea receivedDataTextArea)
    {
	this.receivedDataTextArea = receivedDataTextArea;
    }

    public void setSentDataTextArea(TextArea sentDataTextArea)
    {
	this.sentDataTextArea = sentDataTextArea;
    }
}
