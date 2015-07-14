package com.webrob.distributedmutex.model;

import com.webrob.distributedmutex.network.AllConnectionsManager;
import com.webrob.distributedmutex.ui.TextAreaControllerSingleton;
import com.webrob.distributedmutex.utils.GlobalParameters;
import com.webrob.distributedmutex.utils.TimeHelper;

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
    private int clientsAmount;
    private int receivedOkMessagesAmount = 0;
    private Timer timer;
    private String id;
    private SectionState state = SectionState.FREE;
    private List<Message> messagesToSend = new ArrayList<>();
    private final Object lock = new Object();
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

    public String getId()
    {
	return id;
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
	    Message messageToSend = MessageManager.getOkMessage(id, message.getClock());
	    allConnectionManager.sendMessageToNode(message.getId(), messageToSend);
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
	    if (receivedOkMessagesAmount == clientsAmount)
	    {
		timeoutManager.cancelTimeout();
		receivedOkMessagesAmount = 0;
		occupantSection();
	    }
	    timeoutManager.decreaseTimer();
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
	String now = TimeHelper.getCurrentHourWithMiliSec();
	singleton.showApplicationStateMessage(now + "-------------enter section---------------");

	LeaveSectionTimerTask leaveSectionTimerTask = new LeaveSectionTimerTask();

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
	String now = TimeHelper.getCurrentHourWithMiliSec();
	singleton.showApplicationStateMessage(now + "-------------leave section--------------");
    }

    private void sendOkMessagesToQueuedNodes()
    {
	for (Message message : messagesToSend)
	{
	    Message messageToSend = MessageManager.getOkMessage(id, message.getClock());
	    allConnectionManager.sendMessageToNode(message.getId(), messageToSend);
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

    @Override
    public void sendOkToAllQueuedNodes()
    {
	requestClock = 0;
	receivedOkMessagesAmount = 0;
	timer.cancel();
	timeoutManager.cancelTimeout();
	state = SectionState.FREE;
	sendOkMessagesToQueuedNodes();
	messagesToSend.clear();
    }
}
