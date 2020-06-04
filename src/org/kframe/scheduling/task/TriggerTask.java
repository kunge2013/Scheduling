package org.kframe.scheduling.task;

import org.kframe.scheduling.trigger.Trigger;

public class TriggerTask extends Task {


	private final Trigger trigger;


	/**
	 * Create a new {@link TriggerTask}.
	 * @param runnable the underlying task to execute
	 * @param trigger specifies when the task should be executed
	 */
	public TriggerTask(Runnable runnable, Trigger trigger) {
		super(runnable);
		this.trigger = trigger;
	}


	/**
	 * Return the associated trigger.
	 */
	public Trigger getTrigger() {
		return this.trigger;
	}
}
