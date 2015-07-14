package com.webrob.distributedmutex.model;

import com.google.common.base.Enums;
import com.google.gson.Gson;

public class Message
{
    private String id;
    private Long clock;
    private String type;

    public Message() {}

    public Message(String id, Long clock, MessageType type)
    {
        this.id = id;
        this.clock = clock;
        this.type = type.toString();
    }

    public void setClock(Long clock)
    {
        this.clock = clock;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
	return id;
    }

    public Long getClock()
    {
	return clock;
    }

    public MessageType getType()
    {
	return Enums.getIfPresent(MessageType.class, type).or(MessageType.UNKNOWN);
    }

    @Override
    public String toString()
    {
	Gson gson = new Gson();
	return gson.toJson(this);
    }
}
