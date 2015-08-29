package com.zl.thinkinginjava.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

class PrioritizedTask implements Runnable, Comparable<PrioritizedTask>{
	private static int counter = 0;
	private final int id = counter++;
	private Random rand = new Random(47);
	private final int priority;
	protected static List<PrioritizedTask> sequence = new ArrayList<PrioritizedTask>();
	public PrioritizedTask(int priority) {
		this.priority = priority;
		sequence.add(this);
	}
	@Override
	public int compareTo(PrioritizedTask o) {
		return priority < o.priority ? 1 : 
			(priority > o.priority ? -1 : 0);
	}
	
	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(rand.nextInt(250));
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("[%1$-3d]", priority) + " Task " + id;
	}
	public String summary() {
		return "(" + id + ":" + priority + ")";
	}
	
	public  static class EndSentinel extends PrioritizedTask{
		private ExecutorService exec;
		public EndSentinel(ExecutorService exec) {
			super(-1);
			this.exec = exec;
		}
		
		@Override
		public void run() {
			int count = 0;
			System.out.println();
			for (PrioritizedTask prioritizedTask : PrioritizedTask.sequence) {
				
				System.out.println(prioritizedTask.summary());
//				if(++count % 5 == 0)
//					System.out.println();
			}
			System.out.println();
			System.out.println(this + " Calling shutdownNow()");
			exec.shutdownNow();
		}
	}	
}

class PrioritizedTaskProducer implements Runnable{
	private Random rand = new Random(47);
	private Queue<Runnable> queue;
	private ExecutorService exec;
	public PrioritizedTaskProducer(Queue<Runnable> queue, ExecutorService exec) {
		this.exec = exec;
		this.queue = queue;
	}
	
	@Override
	public void run() {
		for(int i = 0; i != 20; i++){
			queue.add(new PrioritizedTask(rand.nextInt(10)));
			//Thread.yield();
		}
		
		try {
			for(int i = 0; i != 10; i++){
				TimeUnit.MILLISECONDS.sleep(250);
				queue.add(new PrioritizedTask(10));
			}
			for(int i = 0; i != 10; i++)
				queue.add(new PrioritizedTask(i));
			queue.add(new PrioritizedTask.EndSentinel(exec));
		} catch (Exception e) {
			
		}
		System.out.println("Finish priortizedTaskProdecer");
	}
}

class PrioritizedTaskConsumer implements Runnable{
	private PriorityBlockingQueue<Runnable> q;
	public PrioritizedTaskConsumer(PriorityBlockingQueue<Runnable> q) {
		this.q = q;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				//TimeUnit.MILLISECONDS.sleep(1000);
				q.take().run();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Finish Consumer");
	}
}

public class PriorityBlockingQueueDemo {

	public static void main(String[] args) throws Exception{
	
		ExecutorService exec = Executors.newCachedThreadPool();
		PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
		exec.execute(new PrioritizedTaskProducer(queue, exec));
		exec.execute(new PrioritizedTaskConsumer(queue));
	}

}
