package org.kframe.scheduling;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kframe.scheduling.annotations.Scheduled;
import org.kframe.scheduling.event.LoadEvent;
import org.kframe.scheduling.task.ScheduledTaskRegistrarProcessor;

public class ScheduledTaskRegistrarProcessorTest {

	public static void main(String[] args) throws InterruptedException {

		ScheduledTaskRegistrarProcessor processor = new ScheduledTaskRegistrarProcessor();
		LoadEvent event = new LoadEvent();
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
		ConcurrentTaskScheduler concurrentTaskScheduler = new ConcurrentTaskScheduler();
		event.setScheduler(executor);
		event.setTaskScheduler(concurrentTaskScheduler);
		processor.onEvent(event);
		processor.postProcessAfterInitialization(new TimeTaskTest());
//		processor.destroy();
	}

	public static class TimeTaskTest {

		long prefix = 0l;
		long prefix2 = 0l;
//		@Scheduled(cron = "*/10 * * * * *")
//		public void doTask() {
//			long now = System.currentTimeMillis();
//			Logger.getGlobal().setLevel(Level.INFO);
//			Logger.getGlobal().info("测试日志输出11111");
//			Logger logger = Logger.getLogger("JUL");
//			logger.setLevel(Level.INFO);
//			logger.info("dotask1 ....." + (prefix - (now)) / 1000);
//			prefix = now;
//
//		}
//		
//		
		@Scheduled(cron = "*/30 * * * * *")
		public void doTask2() {
			long now = System.currentTimeMillis();
			Logger.getGlobal().setLevel(Level.INFO);
			Logger.getGlobal().info("22222222");
			Logger logger = Logger.getLogger("JUL");
			logger.setLevel(Level.INFO);
			logger.info("doTask2 ....." + (prefix2 - (now)) / 1000);
			prefix2 = now;
		}
		
//		@Scheduled(cron = "0 58 11 ? * *")
//		public void time() {
//			long now = System.currentTimeMillis();
//			Logger.getGlobal().setLevel(Level.INFO);
//			Logger.getGlobal().info("测试日志输出222222");
//			Logger logger = Logger.getLogger("JUL");
//			logger.setLevel(Level.INFO);
//			logger.info("doTask2 ....." + (prefix2 - (now)) / 1000);
//			prefix2 = now;
//		}
	}
}
