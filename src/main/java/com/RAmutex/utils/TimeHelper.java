package com.RAmutex.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Robert on 2015-01-08.
 */
public class TimeHelper
{
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");

    public static String getCurrentHourWithMiliSec()
    {
	Date now = new Date();
	return format.format(now);
    }

}
