package org.kframe.scheduling;

public interface LiferCycle {
	
	default void init() {};

	default void destroy() {};
}
