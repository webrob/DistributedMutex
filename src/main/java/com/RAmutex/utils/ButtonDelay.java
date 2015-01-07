package com.RAmutex.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2015-01-06.
 */
public class ButtonDelay
{
    private final ButtonDelayListener listener;
    private long startTime;
    private boolean alwaysCanPress;

    public ButtonDelay(ButtonDelayListener listener)
    {
	this.listener = listener;
    }

    public void startTimeMeasure()
    {
	startTime = System.nanoTime();
    }


    public boolean canPress()
    {
	boolean canPress = false;
	if (alwaysCanPress)
	{
	   canPress = true;
	}
	else
	{
	    long timeDelay = (System.nanoTime() - startTime) / 1000000;
	    long timeout = GlobalParameters.getReconnectionPeriodTimeWithDelay();
	    if (timeDelay > timeout)
	    {
		canPress = true;
		alwaysCanPress = true;
	    }
	    else
	    {
		long delayLast = timeout - timeDelay;
		Timer timer = new Timer(true);
		timer.schedule(new TimeoutTask(), delayLast);
	    }
	}
	return canPress;
    }

    private class TimeoutTask extends TimerTask
    {
	@Override public void run()
	{
	    listener.delayTimeout();
	}
    }

}
