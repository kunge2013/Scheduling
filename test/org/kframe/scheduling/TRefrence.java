package org.kframe.scheduling;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TRefrence {

	byte[] d = new byte[1024 * 1024 * 100];
	
	public static void main(String[] args) {
		Car car = new Car(22000,"silver");
		WeakReference<Car> weakCar = new WeakReference<Car>(car);
		SoftReference<List<SoftReference<TRefrence>>> list = new SoftReference<List<SoftReference<TRefrence>>>(new ArrayList<SoftReference<TRefrence>>());
		int i=0;
		while(true) {
			list.get().add(new SoftReference<TRefrence>(new TRefrence()));
			System.gc();
			if(weakCar.get()!=null) {
				i++;
				System.out.println("Object is alive for "+i+" loops - "+weakCar);
			}else{
				System.out.println("Object has been collected.");
				break;
			}
		}
	}
	
	public static class Car {
		private double price;
		private String colour;
		
		public Car(double price, String colour){
			this.price = price;
			this.colour = colour;
		}
		
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public String getColour() {
			return colour;
		}
		public void setColour(String colour) {
			this.colour = colour;
		}
		
		public String toString(){
			return colour +"car costs $"+price;
		}

	}
	
	public void test() {
		byte[] b = new byte [100 * 1024 *1024];
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
