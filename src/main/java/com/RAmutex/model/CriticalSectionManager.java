package com.RAmutex.model;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Robert on 2015-01-04.
 */
public class CriticalSectionManager
{
    private final BlockingQueue<Message> messages;

    public CriticalSectionManager(BlockingQueue<Message> messages)
    {
	this.messages = messages;
    }
}
