package com.RAmutex.model;

import com.RAmutex.network.AllConnectionsManager;
import com.RAmutex.ui.TextAreaControllerSingleton;
import com.RAmutex.utils.GlobalParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2015-01-04.
 */
public class CriticalSectionSingleton implements TimeoutListener
{
    private volatile long currentClock = 1;
    private long requestClock = 0;
    private Timer timer;

    public String getId()
    {
	return id;
    }

    private String id;
    private SectionState state = SectionState.FREE;

    private final Object lock = new Object();

    private int clientsAmount;
    private List<Message> messagesToSend = new ArrayList<>();
    private int receivedOkMessagesAmount = 0;

    private static volatile CriticalSectionSingleton instance = null;
    private AllConnectionsManager allConnectionManager;
    private TimeoutManager timeoutManager = new TimeoutMangerEmptyImpl();

    private CriticalSectionSingleton()
    {
    }

    public static CriticalSectionSingleton getInstance()
    {
	if (instance == null)
	{
	    synchronized (CriticalSectionSingleton.class)
	    {
		if (instance == null)
		{
		    instance = new CriticalSectionSingleton();
		}
	    }
	}
	return instance;
    }

    public long getCurrentClock()
    {
	synchronized (lock)
	{
	    return currentClock;
	}
    }

    public void updateClock(long clock)
    {
	synchronized (lock)
	{
	    this.currentClock = Math.max(this.currentClock, clock) + 1;
	}
    }

    public void serveRequestMessage(Message message)
    {
	updateClock(message.getClock());
	if (shouldLetEnterToSection(message.getId(), message.getClock()))
	{
	    allConnectionManager.sendOkMessageToNode(message.getId(), message.getClock());
	}
	else
	{
	    messagesToSend.add(message);
	}

    }

    public void setId(String id)
    {
	this.id = id;
    }

    private boolean shouldLetEnterToSection(String id, Long clock)
    {
	return (state != SectionState.IN_SECTION) && (state != SectionState.WAIT_FOR_SECTION ||
			((requestClock > clock) || ((requestClock == clock) && (
					this.id.compareTo(id) > 0))));
    }

    public void setAllConnectionManager(AllConnectionsManager allConnectionManager)
    {
	this.allConnectionManager = allConnectionManager;
	clientsAmount = allConnectionManager.getClientsAmount();
	id = allConnectionManager.getMyNodeId();
    }

    public void serveOkMessage(Message message)
    {
	updateClock(message.getClock());
	if (message.getClock() == requestClock)
	{
	    receivedOkMessagesAmount++;
	    timeoutManager.decreaseTimer();
	    if (receivedOkMessagesAmount == clientsAmount)
	    {
		timeoutManager.cancelTimeout();
		receivedOkMessagesAmount = 0;
		occupantSection();
	    }
	}
    }

    private void occupantSection()
    {
	requestClock = 0;
	receivedOkMessagesAmount = 0;
	if (timer != null)
	{
	    timer.cancel();
	}
	timeoutManager.cancelTimeout();
	state = SectionState.IN_SECTION;
	TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
	singleton.showApplicationStateMessage("-------------enter section---------------");

	LeaveSectionTimerTask leaveSectionTimerTask = new LeaveSectionTimerTask();

	//new Timer(true);
	timer = new Timer(true);
	timer.schedule(leaveSectionTimerTask, GlobalParameters.maxSectionOccupationTime);
    }

    public void initMessage(Message message)
    {
	updateClock(message.getClock());

	String nodeID = message.getId();

	message.setId(id);
	message.setClock(getCurrentClock());
	allConnectionManager.sendMessageToNode(nodeID, message);
    }

    private class LeaveSectionTimerTask extends TimerTask
    {
	@Override public void run()
	{
	    leaveSection();
	}
    }

    public void leaveSection()
    {
	timer.cancel();
	timeoutManager.cancelTimeout();
	state = SectionState.FREE;
	sendOkMessagesToQueuedNodes();
	messagesToSend.clear();
	TextAreaControllerSingleton singleton = TextAreaControllerSingleton.getInstance();
	singleton.showApplicationStateMessage("-------------leave section--------------");
    }

    private void sendOkMessagesToQueuedNodes()
    {
	for (Message message : messagesToSend)
	{
	    allConnectionManager.sendOkMessageToNode(message.getId(), message.getClock());
	}
    }

    public void requestEnterToSection()
    {
	if ((state != SectionState.IN_SECTION) && (state != SectionState.WAIT_FOR_SECTION))
	{
	    timeoutManager = new TimeoutManagerImpl(this);
	    timeoutManager.startWaitingForSection(clientsAmount);
	    state = SectionState.WAIT_FOR_SECTION;
	    requestClock = getCurrentClock();
	    Message requestMessage = MessageManager.getRequestMessage(id, requestClock);
	    allConnectionManager.sendBroadcastEnterMessage(requestMessage);
	}
    }

    @Override
    public void timeout()
    {
	TextAreaControllerSingleton textAreaController = TextAreaControllerSingleton.getInstance();
	textAreaController.showApplicationStateMessage("-------------timeout = can enter");
	occupantSection();
    }
}
