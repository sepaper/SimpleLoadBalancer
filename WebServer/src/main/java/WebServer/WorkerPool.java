package WebServer;

import java.util.Queue;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class WorkerPool {
	private int size;
	private RequestPool requests;
	private LinkedList<Worker> workers;

	
	WorkerPool(RequestPool requests, int initSize){
		this.size = 0;
		this.requests = requests;
		this.workers = new LinkedList<Worker>();
		
		for(int i=1;i<=initSize;i++) {
			this.add(i);
		}
	}
	
	public synchronized int getSize() {
		return this.size;
	}
	
	public synchronized void add(int number) {
		size++;
		Worker worker = new Worker(requests, number);
		worker.start();
		workers.add(worker);
	}
	
	public synchronized Worker remove() {
		if(size == 0)
			return null;
		size--;
		Worker worker = workers.remove();
		worker.setInActive();
		//worker.interrupt(); // need to change
		return worker;
	}
	
	
	
}
