package org.kframe.scheduling.event;

import java.util.concurrent.ScheduledExecutorService;

import org.kframe.scheduling.ConcurrentTaskScheduler;

public class LoadEvent extends Event {
	
	 ScheduledExecutorService scheduler;
	
	 ConcurrentTaskScheduler taskScheduler;

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public void setScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}

	public ConcurrentTaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	public void setTaskScheduler(ConcurrentTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}
	
	 
}
