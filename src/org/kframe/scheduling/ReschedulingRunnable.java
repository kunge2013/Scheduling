package org.kframe.scheduling;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.kframe.scheduling.annotations.Nullable;
import org.kframe.scheduling.trigger.SimpleTriggerContext;
import org.kframe.scheduling.trigger.Trigger;

public class ReschedulingRunnable implements Runnable , ScheduledFuture<Object>{

	private  Trigger trigger;

	private final SimpleTriggerContext triggerContext = new SimpleTriggerContext();

	private  ScheduledExecutorService executor;

	@Nullable
	private ScheduledFuture<?> currentFuture;

	@Nullable
	private Date scheduledExecutionTime;

	private final Object triggerContextMonitor = new Object();

	private  Runnable delegate;

	private  ErrorHandler errorHandler;
	
	
	public ReschedulingRunnable(Runnable delegate, Trigger trigger, ScheduledExecutorService scheduledExecutor,
			ErrorHandler errorHandler) {
		this.delegate = delegate;
		this.trigger = trigger;
		this.executor = scheduledExecutor;
		this.errorHandler = errorHandler;
	}

	
	/**
	 * @descriple 将任务丢到定时执行任务的线程池中，计算下次执行任务的时间, 将任务丢到线程池中,此处一直通过run方法循环调用，达到把任务丢进定时线程池的效果
	 * 
	 * @return
	 */
	@Nullable
	public ScheduledFuture<?> schedule() {
		synchronized (this.triggerContextMonitor) {
			this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
			if (this.scheduledExecutionTime == null) {
				return null;
			}
			long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
			// 将任务丢到线程池中,此处一直通过run方法循环调用，达到把任务丢进定时线程池的效果
			this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			return this;
		}
	}
	
	@Override
	public void run() {
		Date actualExecutionTime = new Date();
		delegate.run();
		Date completionTime = new Date();
		synchronized (this.triggerContextMonitor) {
			this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
			if (!obtainCurrentFuture().isCancelled()) {
				//TODO  添加下次执行的时间任务 
				schedule();
			}
		}
	}
	
	private ScheduledFuture<?> obtainCurrentFuture() {
		return this.currentFuture;
	}
	
	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Delayed o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}
}
