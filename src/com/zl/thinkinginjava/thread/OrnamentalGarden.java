package com.zl.thinkinginjava.thread;

import java.util.*;
import java.util.concurrent.*;



class Count{
	private int count = 0;
	private Random rand = new Random(47);
	public synchronized int increment() {
		int temp = count;
		if(rand.nextBoolean())
			Thread.yield();
		return count = ++temp;
	}
	
	public synchronized int value(){
		return count;
	}
}

class Entrance implements Runnable{
	private static Count count = new Count(); //记录总数
	private static List<Entrance> entrances = new ArrayList<Entrance>();
	private int number = 0;//记录每个门打开的次数
	private final int id;
	private static volatile boolean canceled = false;
	
	public static void cancel() {
		canceled = true;
	}
	
	public Entrance(int id) {
		this.id = id;
		entrances.add(this);
	}
	
	@Override
	public void run() {
		while(!canceled){
			synchronized (this) {  //双重保护count
				++number;
			}
			System.out.println(this + "Total:" + count.increment());
			try {
				TimeUnit.MILLISECONDS.sleep(100); //使得run一次用时100ms
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Stop " + this);
	}
	
	public synchronized int getValue() {
		return number;
	}
	
	@Override
	public String toString() {
		return "Entrance " + id + ": " + getValue();
	}
	
	public static int getTotalCount() {
		return count.value();
	}
	
	public static int sumEntrances() {
		int sum = 0;
		for (Entrance entrance : entrances) {
			sum += entrance.getValue();
		}
		return sum;
	}
	
}

public class OrnamentalGarden {

	public static void main(String[] args) throws InterruptedException {
			ExecutorService exec = Executors.newCachedThreadPool();
			for(int i = 0; i != 5; i++){
				exec.execute(new Entrance(i));
			}
			TimeUnit.MILLISECONDS.sleep(1000);//main线程休眠2秒，让5条子线程多运行会再shutdown
			Entrance.cancel();
			exec.shutdown();
			if(!exec.awaitTermination(250, TimeUnit.MILLISECONDS))//等待250ms
				System.out.println("Some task were not terminates");
			System.out.println("Total: " + Entrance.getTotalCount());
			System.out.println("Sum of entrance  " + Entrance.sumEntrances());
	}

}
