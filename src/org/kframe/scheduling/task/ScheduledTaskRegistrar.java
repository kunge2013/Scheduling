package org.kframe.scheduling.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.kframe.scheduling.ConcurrentTaskScheduler;
import org.kframe.scheduling.CronTask;
import org.kframe.scheduling.LiferCycle;
import org.kframe.scheduling.annotations.Nullable;

public class ScheduledTaskRegistrar implements ScheduledTaskHolder, LiferCycle {

	/**
	 * A special cron expression value that indicates a disabled trigger: {@value}.
	 * <p>
	 * This is primarily meant for use with {@link #addCronTask(Runnable, String)}
	 * when the value for the supplied {@code expression} is retrieved from an
	 * external source &mdash; for example, from a property in the
	 * {@link org.springframework.core.env.Environment Environment}.
	 * 
	 * @since 5.2
	 * @see org.springframework.scheduling.annotation.Scheduled#CRON_DISABLED
	 */
	public static final String CRON_DISABLED = "-";

	@Nullable
	private ConcurrentTaskScheduler taskScheduler;

	@Nullable
	private ScheduledExecutorService localExecutor;

	@Nullable
	private List<TriggerTask> triggerTasks;

	@Nullable
	private List<CronTask> cronTasks;

	private final Set<ScheduledTask> scheduledTasks = new LinkedHashSet<>(16);

	private final Map<Task, ScheduledTask> unresolvedTasks = new HashMap<>(16);

	public ConcurrentTaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	public void setTaskScheduler(ConcurrentTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	public ScheduledExecutorService getLocalExecutor() {
		return localExecutor;
	}

	public void setLocalExecutor(ScheduledExecutorService localExecutor) {
		this.localExecutor = localExecutor;
	}

	public List<TriggerTask> getTriggerTasks() {
		return triggerTasks;
	}

	public void setTriggerTasks(List<TriggerTask> triggerTasks) {
		this.triggerTasks = triggerTasks;
	}

	public List<CronTask> getCronTasks() {
		return cronTasks;
	}

	public void setCronTasks(List<CronTask> cronTasks) {
		this.cronTasks = cronTasks;
	}

	@Override
	public Set<ScheduledTask> getScheduledTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void afterPropertiesSet() {
		// TODO Auto-generated method stub
		scheduleTasks();
	}

	/**
	 * Schedule all registered tasks against the underlying
	 * {@linkplain #setTaskScheduler(TaskScheduler) task scheduler}.
	 *
	 * @Describle 任务调度线程池初始化,添加任务到线程池中
	 * 
	 */
	protected void scheduleTasks() {
		if (this.taskScheduler == null) {
			this.localExecutor = Executors.newSingleThreadScheduledExecutor();
			this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
		}
		if (this.triggerTasks != null) {
			for (TriggerTask task : this.triggerTasks) {
				addScheduledTask(scheduleTriggerTask(task));
			}
		}
		if (this.cronTasks != null) {
			for (CronTask task : this.cronTasks) {
				addScheduledTask(scheduleCronTask(task));
			}
		}
	}

	private void addScheduledTask(@Nullable ScheduledTask task) {
		if (task != null) {
			this.scheduledTasks.add(task);
		}
	}

	@Nullable
	public ScheduledTask scheduleCronTask(CronTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask(task);
			newTask = true;
		}
		if (this.taskScheduler != null) {
			scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
		} else {
			addCronTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}

	/**
	 * Add a {@link Runnable} task to be triggered per the given cron
	 * {@code expression}.
	 * <p>
	 * As of Spring Framework 5.2, this method will not register the task if the
	 * {@code expression} is equal to {@link #CRON_DISABLED}.
	 */
	public void addCronTask(Runnable task, String expression) {
		if (!CRON_DISABLED.equals(expression)) {
			addCronTask(new CronTask(task, expression));
		}
	}

	/**
	 * Add a {@link CronTask}.
	 * 
	 * @since 3.2
	 */
	public void addCronTask(CronTask task) {
		if (this.cronTasks == null) {
			this.cronTasks = new ArrayList<>();
		}
		this.cronTasks.add(task);
	}

	/**
	 * Schedule the specified trigger task, either right away if possible or on
	 * initialization of the scheduler.
	 * 
	 * @return a handle to the scheduled task, allowing to cancel it
	 * @since 4.3
	 */
	@Nullable
	public ScheduledTask scheduleTriggerTask(TriggerTask task) {
		ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
		boolean newTask = false;
		if (scheduledTask == null) {
			scheduledTask = new ScheduledTask(task);
			newTask = true;
		}
		/**
		 * @describle 添加任务到线程池
		 */
		if (this.taskScheduler != null) {
			scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
		} else {
			addTriggerTask(task);
			this.unresolvedTasks.put(task, scheduledTask);
		}
		return (newTask ? scheduledTask : null);
	}

	/**
	 * Add a {@code TriggerTask}.
	 * 
	 * @since 3.2
	 * @see TaskScheduler#scheduleAtFixedRate(Runnable, long)
	 */
	public void addTriggerTask(TriggerTask task) {
		if (this.triggerTasks == null) {
			this.triggerTasks = new ArrayList<>();
		}
		this.triggerTasks.add(task);
	}
	
	@Override
	public void destroy() {
		if (taskScheduler != null) taskScheduler.shoutDownNow();
		if (localExecutor != null) localExecutor.shutdownNow();
	}
}
