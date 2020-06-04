package org.kframe.scheduling;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.kframe.scheduling.annotations.Nullable;
import org.kframe.scheduling.task.TaskScheduler;
import org.kframe.scheduling.trigger.Trigger;



public class ConcurrentTaskScheduler implements TaskScheduler {


	private ScheduledExecutorService scheduledExecutor;

	private boolean enterpriseConcurrentScheduler = false;

	@Nullable
	private ErrorHandler errorHandler = (e) -> { e.printStackTrace(); };


	/**
	 * Create a new ConcurrentTaskScheduler,
	 * using a single thread executor as default.
	 * @see java.util.concurrent.Executors#newSingleThreadScheduledExecutor()
	 */
	public ConcurrentTaskScheduler() {
		super();
		this.scheduledExecutor = initScheduledExecutor(null);
	}

	/**
	 * Create a new ConcurrentTaskScheduler, using the given
	 * {@link java.util.concurrent.ScheduledExecutorService} as shared delegate.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedScheduledExecutorService}
	 * in order to use it for trigger-based scheduling if possible,
	 * instead of Spring's local trigger management.
	 * @param scheduledExecutor the {@link java.util.concurrent.ScheduledExecutorService}
	 * to delegate to for {@link org.springframework.scheduling.SchedulingTaskExecutor}
	 * as well as {@link TaskScheduler} invocations
	 */
	public ConcurrentTaskScheduler(ScheduledExecutorService scheduledExecutor) {
		this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
	}

	/**
	 * Create a new ConcurrentTaskScheduler, using the given {@link java.util.concurrent.Executor}
	 * and {@link java.util.concurrent.ScheduledExecutorService} as delegates.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedScheduledExecutorService}
	 * in order to use it for trigger-based scheduling if possible,
	 * instead of Spring's local trigger management.
	 * @param concurrentExecutor the {@link java.util.concurrent.Executor} to delegate to
	 * for {@link org.springframework.scheduling.SchedulingTaskExecutor} invocations
	 * @param scheduledExecutor the {@link java.util.concurrent.ScheduledExecutorService}
	 * to delegate to for {@link TaskScheduler} invocations
	 */
	public ConcurrentTaskScheduler(Executor concurrentExecutor, ScheduledExecutorService scheduledExecutor) {
		this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
	}


	private ScheduledExecutorService initScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		this.enterpriseConcurrentScheduler = false;
		return this.scheduledExecutor;
	}

	/**
	 * Specify the {@link java.util.concurrent.ScheduledExecutorService} to delegate to.
	 * <p>Autodetects a JSR-236 {@link javax.enterprise.concurrent.ManagedScheduledExecutorService}
	 * in order to use it for trigger-based scheduling if possible,
	 * instead of Spring's local trigger management.
	 * <p>Note: This will only apply to {@link TaskScheduler} invocations.
	 * If you want the given executor to apply to
	 * {@link org.springframework.scheduling.SchedulingTaskExecutor} invocations
	 * as well, pass the same executor reference to {@link #setConcurrentExecutor}.
	 * @see #setConcurrentExecutor
	 */
	public void setScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
		initScheduledExecutor(scheduledExecutor);
	}

	/**
	 * Provide an {@link ErrorHandler} strategy.
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}


	@Override
	@Nullable
	public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
		return new ReschedulingRunnable(task, trigger, this.scheduledExecutor, errorHandler).schedule();
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
		long initialDelay = startTime.getTime() - System.currentTimeMillis();
		return this.scheduledExecutor.schedule(task, initialDelay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void shoutDownNow() {
		if(scheduledExecutor != null) scheduledExecutor.shutdownNow();
	}

	
	
}
