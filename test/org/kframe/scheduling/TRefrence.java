package org.kframe.scheduling;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class TRefrence {
	public static void main(String[] args) throws InterruptedException {
		byte[] datavel1 = new byte[402800000];
		byte[] datavel2 = new byte[40280000];
		byte[] datavel3 = new byte[4028000];
		Data d = new Data(datavel1);
		SoftData sd = new SoftData(datavel2);
		PhantomReferenceData pd = new PhantomReferenceData(datavel3, new ReferenceQueue<byte[]>());
		
		datavel1 = null;
		datavel2 = null;
		datavel3 = null;
		
		byte[] b = new byte[1024 * 10000];
		System.out.println(b);
		System.out.println("weak =====" + d.get() +", soft==== " + sd +", PhantomReferenceData === " + pd);
		System.gc();
		System.out.println("weak =====" + d.get() +", soft==== " + sd +", PhantomReferenceData === " + pd);
		
	}
	
	static class Data extends WeakReference<byte[]> {
	
		public Data(byte[] referent) {
			super(referent);
		}
		
	}
	
	static class SoftData extends SoftReference<byte[]> {
		
		public SoftData(byte[] referent) {
			super(referent);
		}
		
	}
	
	static class PhantomReferenceData extends PhantomReference<byte[]> {

		public PhantomReferenceData(byte[] referent, ReferenceQueue<? super byte[]> q) {
			super(referent, q);
		}
		
		
		
	}
	
	// PhantomReference
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
}
