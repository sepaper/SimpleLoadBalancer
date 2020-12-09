package WebServer;

import java.sql.Timestamp;

public class WorkerManager extends Thread {
	private WorkerPool workers;
	private RequestPool requests;
	//남아있는 요청 수, scale out 기준
	private int maxLimit; // 이름 모호, maxSize, thread max count
	private int minLimit; // minSize, thread min count
	private boolean isActive;
	
	WorkerManager(WorkerPool workers, RequestPool requests){
		this.workers = workers;
		this.requests = requests;
		this.isActive = true;
	
		this.maxLimit = 10;
		this.minLimit = 2;
	}
	
	public void setMax(int maxLimit) {
		this.maxLimit = maxLimit;
	}
	public void setMin(int minLimit) {
		this.minLimit = minLimit;
	}
	private boolean isBusy() {
		//ReqeustPool is full, then return false;
		return (requests.size() > 500);
	}
	private boolean isIdle() {
		return (requests.size() < 100);
	}
	private void scaleOut() {
		if(workers.getSize() < maxLimit) {
			int newNumber = workers.getSize()+1;
			System.out.println("Worker " + newNumber + " is created!!" );
			workers.add(newNumber );
		}
	}
	private void scaleIn() {
		if(workers.getSize() > minLimit) {
			workers.remove();
		}
	}
	
	public void run() {
		while(isActive) {
			try {
				if(isBusy()) {
					System.out.println("Busy! => " + workers.getSize());
					scaleOut();
				}
				else if (isIdle()) {
					System.out.println("Idle! => " + workers.getSize());
					scaleIn();
				}
				else {
					System.out.println("Stable! => " + workers.getSize());
				}
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				isActive = false;
			}
		}
	}
	public void terminate() {
		while(workers.getSize() > 0)
			workers.remove();
	}
}
