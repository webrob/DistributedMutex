package com.webrob.distributedmutex.model;

import com.webrob.distributedmutex.utils.GlobalParameters;
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
    private StringProperty id;

    private static int defaultPort = GlobalParameters.DEFAULT_PORT;
    private static int defaultID = 1;

    public Node()
    {
        defaultPort++;
        defaultID++;

        IP = new SimpleStringProperty(GlobalParameters.LOCALHOST);
        port = new SimpleIntegerProperty(defaultPort);
        id = new SimpleStringProperty(Integer.toString(defaultID));
    }

    public Node(String IP, Integer port, String id)
    {
        this.IP = new SimpleStringProperty(IP);
        this.port = new SimpleIntegerProperty(port);
        this.id = new SimpleStringProperty(id);
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
        return "address: " + getIP() + " port: " + getPort() + " id: " + getId();
    }

    public String getId()
    {
        return id.get();
    }

    public void setId(String id)
    {
        this.id.set(id);
    }
}
