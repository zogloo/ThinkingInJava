package com.zl.thinkinginjava.thread;


import java.util.concurrent.*;

class Meal{
	private final int orderNum;
	
	public  Meal(int num) {
		orderNum = num;
	}
	
	public String toString() {
		return "Meal " + orderNum;
	}
}

class Waiter implements Runnable{
	private Restaurant restaurant;
	public Waiter(Restaurant restaurant){
		this.restaurant = restaurant;
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				synchronized (this) {
					while (restaurant.meal == null) 
						wait();
				}
				System.out.println("Waiter got " + restaurant.meal);
				synchronized (restaurant.chef) {
					restaurant.meal = null;
					restaurant.chef.notifyAll();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Waiter interrupted");
		}
	}
}

class Chef implements Runnable{
	private Restaurant restaurant;
	private int count = 0;
	public Chef(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				synchronized (this) {
					while(restaurant.meal != null)
						wait();
				}
				if(++count == 10){
					System.out.println("Out of food, closing");
					restaurant.exec.shutdownNow();
				}
				System.out.println("Order up");
				synchronized (restaurant.waiter) {
					restaurant.meal = new Meal(count);
					restaurant.waiter.notifyAll();
				}
			//	TimeUnit.MILLISECONDS.sleep(100);
			}
		} catch (InterruptedException e) {
			System.out.println("Chef interrupted");
		}
		
		
	}
}

public class Restaurant {
	Meal meal;
	Waiter waiter =  new Waiter(this);
	Chef chef = new Chef(this);
	ExecutorService exec = Executors.newCachedThreadPool();
	public Restaurant() {
		exec.execute(chef);
		exec.execute(waiter);
	}
	public static void main(String[] args) {
		new Restaurant();
	}

}
