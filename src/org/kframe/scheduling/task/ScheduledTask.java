package org.kframe.scheduling.task;

import java.util.concurrent.ScheduledFuture;

import org.kframe.scheduling.CronTask;
import org.kframe.scheduling.annotations.Nullable;

public class ScheduledTask {
	
	private final Task task;

	@Nullable
	volatile ScheduledFuture<?> future;


	ScheduledTask(Task task) {
		this.task = task;
	}


	/**
	 * Return the underlying task (typically a {@link CronTask},
	 * {@link FixedRateTask} or {@link FixedDelayTask}).
	 * @since 5.0.2
	 */
	public Task getTask() {
		return this.task;
	}

	/**
	 * Trigger cancellation of this scheduled task.
	 */
	public void cancel() {
		ScheduledFuture<?> future = this.future;
		if (future != null) {
			future.cancel(true);
		}
	}

	@Override
	public String toString() {
		return this.task.toString();
	}
}
