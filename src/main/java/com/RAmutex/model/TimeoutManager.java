package com.RAmutex.model;

/**
 * Created by Robert on 2015-01-05.
 */
public interface TimeoutManager
{

    void decreaseTimer();

    void cancelTimeout();

    void startWaitingForSection(int clientsAmount);
}
