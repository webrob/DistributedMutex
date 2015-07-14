package com.webrob.distributedmutex.model;

/**
 * Created by Robert on 2015-01-05.
 */
public interface TimeoutListener
{
    void timeout();
    void sendOkToAllQueuedNodes();
}
