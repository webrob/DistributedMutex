package com.webrob.distributedmutex.model;

/**
 * Created by Robert on 2015-01-04.
 */
public class MessageManager
{
    public static Message getOkMessage(String id, Long clock)
    {
	return new Message(id, clock, MessageType.OK);
    }

    public static Message getRequestMessage(String id, Long clock)
    {
	return new Message(id, clock, MessageType.REQUEST);
    }

    public static Message getInitMessage(String id, Long clock)
    {
        return new Message(id, clock, MessageType.INIT);
    }
}
