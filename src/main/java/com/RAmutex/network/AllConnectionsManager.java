package com.RAmutex.network;

/**
 * Created by Robert on 2014-12-24.
 */
public interface AllConnectionsManager
{
    void startConnections();
    void sendBroadcastEnterMessage();
    void closeAllSockets();
}
