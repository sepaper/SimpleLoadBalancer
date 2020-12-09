package WebServer;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Worker extends Thread{
	private RequestPool requests;
	private boolean isActive;
	public int number;
	
	Worker(RequestPool requests, int number){
		this.requests = requests;
		this.isActive = true;
		this.number = number;
	}
	
	public void run() {
		while(isActive) {
			try {
				Request request = requests.remove();
				if(request != null) {
					System.out.println(this.number + " => " + "request.getMsg()");
				}
				else {
					System.out.println( number + " => forever sleep..." + " => " + requests.size());
				}
				Thread.sleep(100);
			} catch(InterruptedException e) {
				//
			} catch(NoSuchElementException e) {
				System.out.println("##################### Collision Occurred!! => "+ number + " #####################");
			} finally {
			}
		}
		System.out.println("Worker "+ number + " is terminated!");
	}
	
	public void setActive() {
		isActive = true;
	}
	public void setInActive() {
		isActive = false;
	}
}
