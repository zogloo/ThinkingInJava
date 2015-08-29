package com.zl.thinkinginjava.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


class SleepBlocked implements Runnable{
	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("InterruptExption");
		}
		System.out.println("Exit SleepBlocked run()");	
	}
}

class IOBlocked implements Runnable{
	private InputStream in;
	public IOBlocked(InputStream is) {
		in = is;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Wait for read()");
			in.read();
		} catch (IOException e) {
			if (Thread.currentThread().isInterrupted()) {
				System.out.println("Interrupt from blocked IO");
			} else {
				throw new RuntimeException(e);
			}
		}
		System.out.println("Exit IOBlocked run()");
	}
}

class SynchronizedBlocked implements Runnable{
	public synchronized void f() {
		while(true)//不释放锁
			Thread.yield();
	}
	
	public SynchronizedBlocked() {
		new Thread(){
			@Override
			public void run() {
				f();
			}
		}.start();
	}
	
	@Override
	public void run() {
		System.out.println("Try to call f()");
		f();
		System.out.println("Exit SynBlocked run()");
	}
}


public class Interrupting {
	private static ExecutorService exec = Executors.newCachedThreadPool();
	static void test(Runnable r) throws InterruptedException{
		Future<?> f = exec.submit(r);
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("Interrupting " + r.getClass().getName());
		f.cancel(true);//即当前线程f的interrput()
		System.out.println("Interrupt sent to  " + r.getClass().getName());
	}
	
	public static void main(String[] args) throws Exception{
		test(new SleepBlocked());
		test(new IOBlocked(System.in));
		test(new SynchronizedBlocked());
		TimeUnit.SECONDS.sleep(3);
		System.out.println("Aborting with System.exit(0)");
		System.exit(0);
	}

}
