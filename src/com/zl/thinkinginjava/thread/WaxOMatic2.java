package com.zl.thinkinginjava.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Car2{
	private boolean waxOn = false;
	private Lock lock = new ReentrantLock();
	private Condition con = lock.newCondition();
	public  void waxed() {
		lock.lock();
		try {
			waxOn = true;
			con.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public void buffed() {
		lock.lock();
		try {
			waxOn = false;
			con.signalAll();;
		} finally {
			lock.unlock();
		}
		
	}
	
	public void waitForBuffing() throws InterruptedException {
		lock.lock();
		try {
			while (waxOn == true) {
				con.await();
			} 
		} finally {
			lock.unlock();
		}
	}
	
	public void waitForWaxing() throws InterruptedException {
		lock.lock();
		try {
			while (waxOn == false) {
				con.await();
			} 
		} finally {
			lock.unlock();
		}
	}
}

class WaxOn2 implements Runnable{
	private Car2 car;
	public WaxOn2(Car2 c) {
		car = c;
	}
	
	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				System.out.println("Wax on£¡");
				TimeUnit.MILLISECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
		}catch(InterruptedException e){
			System.out.println("Exit via interrupt");
		}
		System.out.println("Ending wax on task");		
	}
}

class WaxOff2 implements Runnable{
	private Car2 car;
	public WaxOff2(Car2 c) {
		car = c;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				car.waitForWaxing();
				System.out.println("Wax off!");
				TimeUnit.MILLISECONDS.sleep(200);
				car.buffed();
			}
		} catch (InterruptedException e) {
			System.out.println("Exit via interrupt");
		}
		System.out.println("Ending wax off task");
	}
}



public class WaxOMatic2 {

	public static void main(String[] args) throws Exception {
		Car2 car = new Car2();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new WaxOff2(car));
		exec.execute(new WaxOn2(car));
		TimeUnit.SECONDS.sleep(5);
		exec.shutdownNow();
	}

}
