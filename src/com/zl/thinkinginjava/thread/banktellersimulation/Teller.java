package com.zl.thinkinginjava.thread.banktellersimulation;

import java.util.concurrent.TimeUnit;

public class Teller implements Runnable, Comparable<Teller>{
	private static int counter = 0;
	private final int id = counter++;
	private int customersServed = 0;
	private CustomerLine customers;
	private boolean servingCustomerLine = true;
	public Teller(CustomerLine cl) {
		customers = cl;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				Customer customer = customers.take();
				TimeUnit.MILLISECONDS.sleep(customer.getServiceTime());
				synchronized (this) {
					customersServed++;
					while(!servingCustomerLine)
						wait();
				}
			}
		} catch (InterruptedException e) {
			System.out.println(this + "Interrupted");
		}
		System.out.println(this + "terminating");
	}
	
	public synchronized void doSomethingElse() {
		customersServed = 0;
		servingCustomerLine = false;
	}
	
	public synchronized void serveCustomerLine() {
		assert !servingCustomerLine;
		servingCustomerLine = true;
		notifyAll();
	}
	
	@Override
	public String toString() {
		return String.format("Teller %d ", id);
	}
	
	public String shortString() {
		return "T" + id;
	}
	
	public synchronized int compareTo(Teller other) {
		return customersServed < other.customersServed ? -1 : 
			(customersServed == other.customersServed ? 0 : 1);
	}
}
