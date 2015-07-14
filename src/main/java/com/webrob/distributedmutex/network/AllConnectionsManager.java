package com.webrob.distributedmutex.network;

import com.webrob.distributedmutex.model.Message;

/**
 * Created by Robert on 2014-12-24.
 */
public interface AllConnectionsManager
{
    String getMyNodeId();

    void startConnections();

    void wantEnterToSection();

    void leaveSection();

    void sendBroadcastEnterMessage(Message message);

    int getClientsAmount();

    void sendMessageToNode(String id,Message message);
}
