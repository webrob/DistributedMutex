package com.RAmutex.utils;

/**
 * Created by Robert on 2014-12-12.
 */
public class GlobalParameters
{
    public static final String LOCALHOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 20001;
    public static final String DEFAULT_ID = "1";

    public static int reconnectionPeriod = 4000;
    public static final String RECONNECTION_PERIOD_DESCRIPTION = "reconnection period";

    public static int maxSectionOccupationTime = 5000;
    public static final String MAX_SECTION_OCCUPATION_TIME_DESCRIPTION = "max time of section occupation";

    public static int getMaxSectionOccupationTimeWithDelay()
    {
	return maxSectionOccupationTime + 1000;
    }

    public static long getTimeout(int clientsAmount)
    {
	return clientsAmount * (getMaxSectionOccupationTimeWithDelay());
    }

}
