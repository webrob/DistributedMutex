package com.webrob.distributedmutex.network;

import com.webrob.distributedmutex.model.Message;

/**
 * Created by Robert on 2014-12-24.
 */
public class AllConnectionsManagerEmpty implements AllConnectionsManager
{
    @Override public String getMyNodeId()
    {
        return "";
    }

    @Override public void startConnections()
    {

    }

    @Override public void wantEnterToSection()
    {

    }

    @Override public void leaveSection()
    {

    }

    @Override public void sendBroadcastEnterMessage(Message message)
    {

    }

    @Override public int getClientsAmount()
    {
        return 0;
    }

    @Override public void sendMessageToNode(String id, Message message)
    {

    }

}
