package emailclient;

import java.util.LinkedList;

public class MyBlockingQueue {
	private int maxQueueSize;
	private LinkedList<Email> data;
	
	public MyBlockingQueue(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
		data = new LinkedList<Email>();
	}
	
	public synchronized void enqueue(Email email) {
		while (data.size() > maxQueueSize) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		data.add(email);
		notifyAll();
	}
	
	public synchronized Email dequeue() {
		Email email = null;
		while (data.size() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		email = data.remove();
		notifyAll();
		return email;
	}
	
}
