package com.RAmutex.model;

import com.google.common.base.Enums;
import com.google.gson.Gson;

public class Message
{
    public void setClock(Long clock)
    {
        this.clock = clock;
    }

    public void setType(String type)
    {
        this.type = type;
    }



    private String id;
    private Long clock;
    private String type;

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


    public Message() {}

    public Message(String id, Long clock, MessageType type)
    {
	this.id = id;
	this.clock = clock;
	this.type = type.toString();
    }

    public String toString()
    {
	Gson gson = new Gson();
	return gson.toJson(this);
    }

}
