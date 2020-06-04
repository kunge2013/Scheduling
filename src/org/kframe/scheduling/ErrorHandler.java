package org.kframe.scheduling;
@FunctionalInterface
public interface ErrorHandler {
	void accept(Exception e);
}
