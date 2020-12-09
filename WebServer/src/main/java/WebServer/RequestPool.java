package WebServer;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class RequestPool {
	private LinkedList<Request> requests;
	
	public RequestPool() {
		requests = new LinkedList<Request>();
	}
	
	public synchronized void add(Request request) {
		requests.add(request);
	}
	public synchronized Request remove() throws NoSuchElementException {
		if(requests.size() > 0)
			return requests.remove();
		else
			return null;
	}
	public synchronized int size() {
		return requests.size();
	}
	
}
