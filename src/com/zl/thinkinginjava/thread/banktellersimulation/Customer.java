package com.zl.thinkinginjava.thread.banktellersimulation;

public class Customer{
	private final int serviceTime;
	
	public Customer(int serviceTime) {
		this.serviceTime = serviceTime;
	}
	public int getServiceTime() {
		return serviceTime;
	}
	@Override
	public String toString() {
		return String.format("[ %d ]", serviceTime);
	}
}
