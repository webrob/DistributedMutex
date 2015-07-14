package com.webrob.distributedmutex.model;

import com.webrob.distributedmutex.utils.GlobalParameters;

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
    private boolean canDecrease;

    public TimeoutManagerImpl(TimeoutListener listener)
    {
	this.listener = listener;
    }

    @Override
    public void decreaseTimer()
    {

	CriticalSectionSingleton singleton = CriticalSectionSingleton.getInstance();
	String id = singleton.getId();
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
    }

    @Override
    public void cancelTimeout()
    {
	canDecrease = false;
	timeoutTask.cancel();
    }

    @Override
    public void startWaitingForSection(int clientsAmount)
    {
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
	    long timeDelay = (System.nanoTime() - startTime) / 1000000;
	    if (timeDelay > timeout + 500)
	    {
		listener.sendOkToAllQueuedNodes();
	    }
	    else
	    {
		listener.timeout();
	    }
	}
    }
}
