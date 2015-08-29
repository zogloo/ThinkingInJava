package com.zl.thinkinginjava.thread;

import java.util.concurrent.*;
import java.io.*;

class LiftOffRunner implements Runnable{
	private BlockingQueue<LiftOff> rockets;
	public LiftOffRunner(BlockingQueue<LiftOff> queue){
		this.rockets = queue;
	}
	public void add(LiftOff lo) {
		try {
			rockets.put(lo);
		} catch (InterruptedException e) {
			System.out.println("Interruptted in add");
		}
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				LiftOff rocket = rockets.take();
				rocket.run();
				TimeUnit.SECONDS.sleep(1);
				
			}
		} catch (InterruptedException e) {
			System.out.println("Waking from take()");
		}
		System.out.println("Exit LiftOffRunner");
		
	}
	
}

class LiftOff implements Runnable{
	protected int countDown = 10;
	private static int taskCount = 0;
	private final int id = taskCount++;
	public LiftOff(){}
	public LiftOff(int countDown){
		this.countDown = countDown;
	}
	public String status(){
		return "#" + id + "(" + (countDown > 0 ? countDown : "LiftOff!" ) + " ), ";
	}
	
	@Override
	public void run() {
		while(countDown-- > 0){
			
			System.out.println(status());
			Thread.yield();
		}
		
	}
	
}

public class TestBlockingQueues {
	static void getKey(){
		try{
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	static void getKey(String message){
		System.out.println(message);
		getKey();
	}
	
	static void  test(String msg, BlockingQueue<LiftOff> queue){
		System.out.println(msg);
		LiftOffRunner runner = new LiftOffRunner(queue);
		Thread t = new Thread(runner);
		t.start();
		for(int i = 0; i < 15; i++){
			runner.add(new LiftOff(5));
		}
		getKey("Press Enter   " + msg);
		t.interrupt();
		System.out.println("Finish " + msg + "   test");
	}
	
	public static void main(String[] args) {
		//test("LinkedBlockingQueue", new  LinkedBlockingQueue<LiftOff>());
		test("ArrayBlockingQueue", new  ArrayBlockingQueue<LiftOff>(3));
		test("SynchronousQueue", new SynchronousQueue<LiftOff>());

	}

}















