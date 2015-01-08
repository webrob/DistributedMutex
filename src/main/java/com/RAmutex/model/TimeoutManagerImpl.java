package com.RAmutex.model;

import com.RAmutex.utils.GlobalParameters;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2015-01-05.
 */
public class TimeoutManagerImpl implements TimeoutManager
{
    long startTime;
    private TimeoutListener listener;
    private Timer timer;
    private long timeout;
    private TimeoutTask timeoutTask;
    private int clientsAmount;

    public TimeoutManagerImpl(TimeoutListener listener)
    {
	this.listener = listener;
    }

    private int okCounter = 0;
    private boolean canDecrease;

    @Override
    public void decreaseTimer()
    {

	CriticalSectionSingleton singleton = CriticalSectionSingleton.getInstance();
	String id = singleton.getId();
	//timeout -= GlobalParameters.getMaxSectionOccupationTimeWithDelay();
	//System.out.println("timeout: " + timeout);
	//timeoutTask.cancel();
	System.out.println("id: " + id);
	if (canDecrease)
	{

	    System.out.println("id: " + id + " timeout: " + timeout);
	    long timeLast = (System.nanoTime() - startTime) / 1000000;

	    long v = timeLast / GlobalParameters.getMaxSectionOccupationTimeWithDelay();
	    v++;

	    timeout -= v * GlobalParameters.getMaxSectionOccupationTimeWithDelay();
	    startTime = System.nanoTime();
	    timeoutTask.cancel();

	    System.out.println("id: " + id + " timeout: " + timeout + " v " + v);

	    if (timeout >= 0)
	    {
		setTimeoutTask(timeout);
	    }
	}
	//okCounter++;

    }

    @Override
    public void cancelTimeout()
    {
	canDecrease = false;
	okCounter = 0;
	timeoutTask.cancel();
    }

    @Override
    public void startWaitingForSection(int clientsAmount)
    {
	this.clientsAmount = clientsAmount;
	canDecrease = true;
	timeout = GlobalParameters.getTimeout(clientsAmount);
	timer = new Timer(true);
	startTime = System.nanoTime();
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
	    okCounter = 0;
	    listener.timeout();
	}
    }
}
