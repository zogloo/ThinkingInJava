package com.zl.thinkinginjava.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Egg{
	private enum Status{PUTTED, TOKEN};
	private Status status = Status.TOKEN;
	private  int id = 0;
	public Egg(int id) {
		this.id = id;
	}
	
	public void in(){
		status = Status.PUTTED;
	}
	
	public void out() {
		status = Status.TOKEN;
	}
	
	@Override
	public String toString() {
	return "Egg: " + id + " " + status;
	}
}

class PutEgg implements Runnable{
	ArrayBlockingQueue<Egg> basket;
	int count = 0;
	public PutEgg(ArrayBlockingQueue<Egg> basket) {
		this.basket = basket;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Egg egg = new Egg(++count);
				basket.put(egg);
				egg.in();
				System.out.println(egg);
				TimeUnit.MILLISECONDS.sleep(100);
			}
		} catch (InterruptedException e) {
			System.out.println("Put interrupted");
		}
		
	}
}

class TakeEgg implements Runnable{
	ArrayBlockingQueue<Egg> basket;
	public TakeEgg(ArrayBlockingQueue<Egg> basket) {
		this.basket = basket;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Egg egg = basket.take();
				egg.out();
				System.out.println(egg);
			}
		} catch (InterruptedException e) {
			System.out.println("Take interrupt");
		}
		
	}
}
	
public class TreadLearning{
	 
	public static void main(String[] args) throws Exception {	
		ArrayBlockingQueue<Egg> basket = new ArrayBlockingQueue<Egg>(1);
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new PutEgg(basket));
		exec.execute(new TakeEgg(basket));
		TimeUnit.SECONDS.sleep(1);
		exec.shutdownNow();
	}

}
