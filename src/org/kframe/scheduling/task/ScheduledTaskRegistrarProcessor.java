package org.kframe.scheduling.task;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.kframe.scheduling.CronTask;
import org.kframe.scheduling.CronTrigger;
import org.kframe.scheduling.LiferCycle;
import org.kframe.scheduling.MethodIntrospector;
import org.kframe.scheduling.ScheduledMethodRunnable;
import org.kframe.scheduling.annotations.Nullable;
import org.kframe.scheduling.annotations.Scheduled;
import org.kframe.scheduling.event.Event;
import org.kframe.scheduling.event.Listener;
import org.kframe.scheduling.event.LoadEvent;
import org.kframe.scheduling.functions.StringValueResolver;
import org.kframe.scheduling.util.AopUtils;
import org.kframe.scheduling.util.Proxy;
import org.kframe.scheduling.util.StringUtils;


public class ScheduledTaskRegistrarProcessor implements Listener , LiferCycle {


	private final ScheduledTaskRegistrar registrar;
	
	@Nullable
	private StringValueResolver embeddedValueResolver;
	
	
	private final Map<Object, Set<ScheduledTask>> scheduledTasks = new IdentityHashMap<>(16);

	
	public ScheduledTaskRegistrarProcessor() {
		this.registrar = new ScheduledTaskRegistrar();
	}
	@Nullable
	private Object scheduler;

	private void finishRegistration(LoadEvent event) {
		this.registrar.setTaskScheduler(event.getTaskScheduler());
		this.registrar.afterPropertiesSet();//初始化
	}


	@Override
	public void onEvent(Event event) {
		if (event instanceof LoadEvent) {
			LoadEvent loadEvent = (LoadEvent) event;
			finishRegistration(loadEvent);
		}
	}
	
	protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
		try {
			Runnable runnable = createRunnable(bean, method);
			boolean processedSchedule = false;
			String errorMessage =
					"Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";

			Set<ScheduledTask> tasks = new LinkedHashSet<>(4);

			// Check cron expression
			String cron = scheduled.cron();
			if (StringUtils.hasText(cron)) {
				String zone = scheduled.zone();
				if (this.embeddedValueResolver != null) {
					cron = this.embeddedValueResolver.resolveStringValue(cron);
					zone = this.embeddedValueResolver.resolveStringValue(zone);
				}
				if (StringUtils.hasLength(cron)) {
					processedSchedule = true;
					if (!Scheduled.CRON_DISABLED.equals(cron)) {
						TimeZone timeZone;
						if (StringUtils.hasText(zone)) {
							timeZone = StringUtils.parseTimeZoneString(zone);
						}
						else {
							timeZone = TimeZone.getDefault();
						}
						/**
						 * @describle 将当前的任务丢到定时线程池中，按照时间进行执行当前的任务
						 */
						tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone))));
					}
				}
			}
		
			// Finally register the scheduled tasks
			synchronized (this.scheduledTasks) {
				Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> new LinkedHashSet<>(4));
				regTasks.addAll(tasks);
			}
		}
		catch (IllegalArgumentException ex) {
			throw new IllegalStateException(
					"Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
		}
	}
	
	/**
	 * Create a {@link Runnable} for the given bean instance,
	 * calling the specified scheduled method.
	 * <p>The default implementation creates a {@link ScheduledMethodRunnable}.
	 * @param target the target bean instance
	 * @param method the scheduled method to call
	 * @since 5.1
	 * @see ScheduledMethodRunnable#ScheduledMethodRunnable(Object, Method)
	 */
	protected Runnable createRunnable(Object target, Method method) {
		Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
		return new ScheduledMethodRunnable(target, invocableMethod);
	}
	
	public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
		if (targetType == null) {
			return method;
		}
		Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
		if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) &&
				Proxy.class.isAssignableFrom(targetType)) {
			throw new IllegalStateException(String.format(
					"Need to invoke method '%s' found on proxy for target class '%s' but cannot " +
					"be delegated to target bean. Switch its visibility to package or protected.",
					method.getName(), method.getDeclaringClass().getSimpleName()));
		}
		return methodToUse;
	}
	
	
	public void postProcessAfterInitialization(Object bean) {
		Method[]  methods = bean.getClass().getMethods();
		Map<Method, Scheduled> annotatedMethods = new HashMap<Method, Scheduled>();
		if (methods != null && methods.length > 0) {
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				Scheduled scheduled = method.getAnnotation(Scheduled.class);
				if (scheduled != null) {
					annotatedMethods.put(method, scheduled);
				}
				
			}
		}
		
		if (!annotatedMethods.isEmpty()) {
			annotatedMethods.forEach((method, scheduled) ->   processScheduled(scheduled, method, bean));
		}
	}
	
	@Override
	public void destroy() {
		registrar.destroy();
	}
}
