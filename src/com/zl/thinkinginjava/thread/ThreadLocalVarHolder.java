package com.zl.thinkinginjava.thread;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Accessor implements Runnable{
	private final int id;
	public Accessor(int id) {
		this.id = id;
		
	}
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			ThreadLocalVarHolder.increment();
			System.out.println(this);
			Thread.yield();
		}
	}
	
	public String toString() {
		return "#" + id + ":" + ThreadLocalVarHolder.get();
	}
}


public class ThreadLocalVarHolder {
	private static ThreadLocal<Integer> value = 
			new ThreadLocal<Integer>(){
																private Random rand = new Random(47);
																protected synchronized Integer initialValue(){
																	return rand.nextInt(100000);
																}
															};

	public static int get() {
		// TODO Auto-generated method stub
		return value.get();
	}

	public static void increment() {
		value.set(value.get() + 1);
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i = 0; i != 5; i++){
			exec.execute(new Accessor(i));
		}
		TimeUnit.SECONDS.sleep(3);
		exec.shutdownNow();

	}

}
