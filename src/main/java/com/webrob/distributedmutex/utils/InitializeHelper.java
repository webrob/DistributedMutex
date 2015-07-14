package com.webrob.distributedmutex.utils;

import java.util.prefs.Preferences;

/**
 * Created by Robert on 2015-01-05.
 */
public class InitializeHelper
{
    private static final Preferences prefs = Preferences.userNodeForPackage(InitializeHelper.class);
    private static final String NAME = "init";
    private static final int DEFAULT_VALUE = 0;
    private static int value = prefs.getInt(NAME, DEFAULT_VALUE);

    public static int getValue()
    {
	return value;
    }

    public static void resetValue()
    {
        setValue(DEFAULT_VALUE);
    }

    public static void incrementValue()
    {
        setValue(++value);
    }

    public static void setValue(int value)
    {
	InitializeHelper.value = value;
	savePath();
    }

    private static void savePath()
    {
	prefs.putInt(NAME, value);
    }
}
