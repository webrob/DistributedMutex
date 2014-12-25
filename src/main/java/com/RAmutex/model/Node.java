package com.RAmutex.model;

import com.RAmutex.utils.GlobalParameters;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Robert on 2014-12-12.
 */
public class Node
{
    private StringProperty IP;
    private IntegerProperty port;

    private static int defaultPort = GlobalParameters.DEFAULT_PORT;

    public Node()
    {
        defaultPort++;

        IP = new SimpleStringProperty(GlobalParameters.LOCALHOST);
        port = new SimpleIntegerProperty(defaultPort);
    }

    public Node(String IP, Integer port)
    {
        this.IP = new SimpleStringProperty(IP);
        this.port = new SimpleIntegerProperty(port);
    }

    public String getIP()
    {
        return IP.get();
    }

    public void setIP(String IP)
    {
        this.IP.set(IP);
    }

    public Integer getPort()
    {
        return port.get();
    }

    public void setPort(Integer port)
    {
        this.port.set(port);
    }

    @Override
    public String toString()
    {
        return "address: " + getIP() + " port: " + getPort();
    }
}
