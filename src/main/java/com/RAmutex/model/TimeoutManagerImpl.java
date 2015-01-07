package com.RAmutex.model;

import com.RAmutex.utils.GlobalParameters;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2015-01-05.
 */
public class TimeoutManagerImpl implements TimeoutManager
{
    long start_time;
    private TimeoutListener listener;
    private Timer timer;
    private long timeout;
    private TimeoutTask timeoutTask;
    private int clientsAmount;

    public TimeoutManagerImpl(TimeoutListener listener)
    {
	this.listener = listener;
    }

    @Override
    public void decreaseTimer()
    {
	long timeLast = (System.nanoTime() - start_time) / 1000000;

	long v = timeLast / GlobalParameters.getMaxSectionOccupationTimeWithDelay();
	long rest = timeLast % GlobalParameters.getMaxSectionOccupationTimeWithDelay();
	if (rest > 0)
	{
	    v++;
	}

	timeoutTask.cancel();

	long newTimeout = timeout - v * GlobalParameters.getMaxSectionOccupationTimeWithDelay();

        System.out.println(newTimeout);
	setTimeoutTask(newTimeout);
    }

    @Override
    public void cancelTimeout()
    {
	timeoutTask.cancel();
    }

    @Override
    public void startWaitingForSection(int clientsAmount)
    {
	this.clientsAmount = clientsAmount;
	timeout = GlobalParameters.getTimeout(clientsAmount);
	timer = new Timer(true);
	start_time = System.nanoTime();
	setTimeoutTask(timeout);
    }

    private void setTimeoutTask(long timeout)
    {
	timeoutTask = new TimeoutTask();
	timer.schedule(timeoutTask, timeout);
    }

    private class TimeoutTask extends TimerTask
    {
	@Override public void run()
	{
	    listener.timeout();
	}
    }
}
