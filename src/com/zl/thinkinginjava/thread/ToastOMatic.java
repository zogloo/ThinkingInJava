package com.zl.thinkinginjava.thread;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class Toast{
	public enum Status{DRY, BUTTERED, JAMMED};
	private Status status = Status.DRY;
	private final int id;
	
	public Toast(int id){
		this.id = id;
	}
	
	public void butter(){
		status = Status.BUTTERED;
	}
	
	public void jam(){
		status = Status.JAMMED;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() {
		return "Toast " + id + ": " + status;
	}
}

class Toaster implements Runnable{
	private BlockingQueue<Toast> toastQueue;
	private int count = 0;
	private Random rand = new Random(47);
	
	public Toaster(BlockingQueue<Toast> toastQueue) {
		this.toastQueue = toastQueue;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(500));
				Toast t = new Toast(count++);
				System.out.println(t);
				toastQueue.put(t);
			}
		} catch (InterruptedException e) {
			System.out.println("Toaster interrupt");
		}
		System.out.println("Toaster off");
	}
}

class Butterer implements Runnable{
	private BlockingQueue<Toast> dryQueue, butteredQueue;
	public Butterer(BlockingQueue<Toast> dryQueue, BlockingQueue<Toast> butteredQueue) {
		this.dryQueue = dryQueue;
		this.butteredQueue = butteredQueue;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Toast t = dryQueue.take();
				t.butter();
				System.out.println(t);
				butteredQueue.put(t);
			}
		} catch (InterruptedException e) {
			System.out.println("Butterer interrupt");
		}
		System.out.println("Butterer off");
	}
}

class Jammer implements Runnable{
	private BlockingQueue<Toast>  butteredQueue, finishedQueue;
	public Jammer(BlockingQueue<Toast>  butteredQueue, BlockingQueue<Toast> finishedQueue) {
		this.butteredQueue = butteredQueue;
		this.finishedQueue = finishedQueue;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Toast t = butteredQueue.take();
				t.jam();
				System.out.println(t);
				finishedQueue.put(t);
			}
		} catch (InterruptedException e) {
			System.out.println("Jammer interrupt");
		}
		System.out.println("Jammer off");
		
	}
}

class Eater implements Runnable{
	private BlockingQueue<Toast> finishedQueue;
	private int counter = 0;
	public Eater(BlockingQueue<Toast> finishedQueue ){
		this.finishedQueue = finishedQueue;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Toast t = finishedQueue.take();
				if(t.getId() != counter++ || t.getStatus() != Toast.Status.JAMMED){
					System.out.println(">>>>>Error:  " + t);
					System.exit(1);
				}else {
					System.out.println("Eat " + t);
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Eatter interrupt");
		}
		System.out.println("Eatter off");
		
	}
}



public class ToastOMatic {

	public static void main(String[] args) throws Exception {
		BlockingQueue<Toast> dryQueue = new LinkedBlockingQueue<>(),
												butteredQueue = new LinkedBlockingQueue<>(),
												finishedQueue = new LinkedBlockingQueue<>();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Toaster(dryQueue));
		exec.execute(new Butterer(dryQueue, butteredQueue));
		exec.execute(new Jammer(butteredQueue, finishedQueue));
		exec.execute(new Eater(finishedQueue));
		TimeUnit.SECONDS.sleep(1);
		exec.shutdownNow();

	}

}


















