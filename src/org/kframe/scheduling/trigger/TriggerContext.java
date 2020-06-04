package org.kframe.scheduling.trigger;

import java.util.Date;

import org.kframe.scheduling.annotations.Nullable;

public interface TriggerContext {
	/**
	 * Return the last <i>scheduled</i> execution time of the task,
	 * or {@code null} if not scheduled before.
	 */
	@Nullable
	Date lastScheduledExecutionTime();

	/**
	 * Return the last <i>actual</i> execution time of the task,
	 * or {@code null} if not scheduled before.
	 */
	@Nullable
	Date lastActualExecutionTime();

	/**
	 * Return the last completion time of the task,
	 * or {@code null} if not scheduled before.
	 */
	@Nullable
	Date lastCompletionTime();
}
