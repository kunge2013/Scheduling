package org.kframe.scheduling;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ServiceLoaderSpi {
	public static void main(String[] args) {
		ServiceLoader<IService> loader = ServiceLoader.load(IService.class);
		Iterator<IService> it = loader.iterator();
		while(it.hasNext()) {
			IService s = it.next();
			s.hello();
			System.out.println(s.getClass().getName());
		}
	}
}
