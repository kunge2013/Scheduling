package org.kframe.scheduling.trigger;

import java.util.Date;

import org.kframe.scheduling.annotations.Nullable;

public class SimpleTriggerContext implements TriggerContext {

	@Nullable
	private volatile Date lastScheduledExecutionTime;

	@Nullable
	private volatile Date lastActualExecutionTime;

	@Nullable
	private volatile Date lastCompletionTime;
	
	@Override
	public Date lastScheduledExecutionTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date lastActualExecutionTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date lastCompletionTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(Date scheduledExecutionTime, Date actualExecutionTime, Date completionTime) {
		this.lastScheduledExecutionTime = lastScheduledExecutionTime;
		this.lastActualExecutionTime = lastActualExecutionTime;
		this.lastCompletionTime = lastCompletionTime;
	}

}
