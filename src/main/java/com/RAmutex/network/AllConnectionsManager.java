package com.RAmutex.network;

import com.RAmutex.model.Message;

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
    void closeAllSockets();
    int getClientsAmount();


    void sendMessageToNode(String id,Message message);

    void sendOkMessageToNode(String id, Long clock);
}
