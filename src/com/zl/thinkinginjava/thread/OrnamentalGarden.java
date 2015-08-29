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
	private static Count count = new Count(); //��¼����
	private static List<Entrance> entrances = new ArrayList<Entrance>();
	private int number = 0;//��¼ÿ���Ŵ򿪵Ĵ���
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
			synchronized (this) {  //˫�ر���count
				++number;
			}
			System.out.println(this + "Total:" + count.increment());
			try {
				TimeUnit.MILLISECONDS.sleep(100); //ʹ��runһ����ʱ100ms
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
			TimeUnit.MILLISECONDS.sleep(1000);//main�߳�����2�룬��5�����̶߳����л���shutdown
			Entrance.cancel();
			exec.shutdown();
			if(!exec.awaitTermination(250, TimeUnit.MILLISECONDS))//�ȴ�250ms
				System.out.println("Some task were not terminates");
			System.out.println("Total: " + Entrance.getTotalCount());
			System.out.println("Sum of entrance  " + Entrance.sumEntrances());
	}

}
