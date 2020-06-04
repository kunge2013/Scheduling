package org.kframe.scheduling.trigger;

import java.util.Date;

public interface Trigger {
	Date nextExecutionTime(TriggerContext triggerContext);
}
