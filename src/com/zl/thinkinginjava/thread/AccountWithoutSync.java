package com.zl.thinkinginjava.thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountWithoutSync {
	private static Account account = new Account();
	public static void main(String[] args) {
		ExecutorService execuor = Executors.newCachedThreadPool();
		for(int i = 0; i != 100; i++){
			execuor.execute(new AddPennyTask());
		}
		execuor.shutdown();		
		while(!execuor.isTerminated()){
		}	
		System.out.println(account.getBalance());
	}
	
	private static class AddPennyTask implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			account.deposit(1);
		}
	}
	
	private static class Account{
		private int balance;

		public int getBalance() {
			return balance;
		}	
		public synchronized void deposit(int amount){
			int newBalance = amount + balance;
//			try{
				Thread.yield();
//			}catch(InterruptedException e){			
//			}			
			balance = newBalance;
		}
	}

}
