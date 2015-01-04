package com.RAmutex.model;

import com.google.common.base.Enums;
import com.google.gson.Gson;

public class Message
{
    private String id;
    private Long clockValue;
    private String type;

    public String getId()
    {
	return id;
    }

    public Long getClockValue()
    {
	return clockValue;
    }

    public MessageType getType()
    {
	return Enums.getIfPresent(MessageType.class, type).or(MessageType.UNKNOWN);
    }

    public Message(String id, Long clockValue, MessageType type)
    {
	this.id = id;
	this.clockValue = clockValue;
	this.type = type.toString();
    }

    public String toString()
    {
	Gson gson = new Gson();
	return gson.toJson(this);
    }

}
