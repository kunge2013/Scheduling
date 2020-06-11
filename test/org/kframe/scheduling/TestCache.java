package org.kframe.scheduling;

import static org.kframe.scheduling.Cache.Kind.SOFT;

import java.lang.reflect.Constructor;


public class TestCache {
	
	 private static final Cache<TestCache, Constructor<?>[] > CACHE = new Cache<TestCache, Constructor<?>[] >(SOFT, SOFT) {
	        @Override
	        public Constructor<?>[]  create(TestCache cache) {
	            try {
	                return cache.getClass().getConstructors();
	            }
	            catch (Exception exception) {
	            	
	            }
				return null;
	        }
	    };
	    
	    public static void main(String[] args) {
	    	Constructor<?>[] objs = CACHE.get(new TestCache());
	    	System.out.println(objs);
	    }
}
