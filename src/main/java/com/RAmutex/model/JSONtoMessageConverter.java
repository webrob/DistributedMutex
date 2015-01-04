package com.RAmutex.model;

import com.google.gson.Gson;

/**
 * Created by Robert on 2015-01-04.
 */
public class JSONtoMessageConverter
{
    public static Message convert(String json)
    {
	Gson gson = new Gson();
	return gson.fromJson(json, Message.class);
    }
}
