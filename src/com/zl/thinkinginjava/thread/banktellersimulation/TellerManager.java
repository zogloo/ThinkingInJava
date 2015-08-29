package com.zl.thinkinginjava.thread.banktellersimulation;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TellerManager implements Runnable {
	private ExecutorService exec;
	private CustomerLine customers;
	private PriorityBlockingQueue<Teller> workingTellers = new PriorityBlockingQueue<Teller>();
	private Queue<Teller> tellersDoingOtherThings = new LinkedList<Teller>();
	private int adjustmentPeriod;

	public TellerManager(ExecutorService exec, CustomerLine customers,  int adjustmentPeriod) {
		this.exec = exec;
		this. customers = customers;
		this.adjustmentPeriod =adjustmentPeriod;
		Teller teller = new Teller(customers);
		exec.execute(teller);
		workingTellers.add(teller);
	}
	public void adjustTellerNumber() {
		if(customers.size()/workingTellers.size() > 2){//����Ŷӵ��˺ܶ�
			if(tellersDoingOtherThings.size() > 0){//����������õķ���Ա
				Teller teller = tellersDoingOtherThings.remove();
				teller.serveCustomerLine();//ȥ����
				workingTellers.offer(teller);
				return;
			}//else
			Teller teller = new Teller(customers);//����һ������Ա
			exec.execute(teller);
			workingTellers.add(teller);
			return;
		}
		if(workingTellers.size() > 1 && customers.size()/workingTellers.size() < 2)//����˿�������
			reassignOneTell();
		if(customers.size() == 0)
			while (workingTellers.size() > 1) {
				reassignOneTell();
			}
	}
	private void reassignOneTell() {
		Teller teller = workingTellers.poll();
		teller.doSomethingElse();
		tellersDoingOtherThings.offer(teller);
	}
	
	@Override
	public void run() {
		try {
			while(!Thread.interrupted()){
				TimeUnit.MILLISECONDS.sleep(adjustmentPeriod);
				adjustTellerNumber();
				System.out.print(customers + " { ");
				for (Teller teller : workingTellers) {
					System.out.print(teller.shortString() + " " );
				}
				System.out.println(" } ");
			}
		} catch (Exception e) {
			System.out.println(this + "interrupted");
		}
		System.out.println(this  + "terminating");
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "TellerManager";
	}
}
