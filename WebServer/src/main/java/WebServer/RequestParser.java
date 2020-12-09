package WebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class RequestParser {
	
	RequestParser(){

	}
	// need to be moved to class Request for integration
	public Request getRequest(InputStream socketInputStream) throws IOException {
		Request req = new Request();
		HashMap<String, String> reqheader = req.header;
		
		BufferedReader socketInputReader = new BufferedReader(new InputStreamReader(socketInputStream));
		
		String requestLine = socketInputReader.readLine();
		req.method = requestLine.split(" ")[0];
		req.requestURI = requestLine.split(" ")[1];
		req.httpVersion = requestLine.split(" ")[2];
		
		String line = socketInputReader.readLine();
		//header parsing
		while(!line.isEmpty()) {
			reqheader.put(line.split(":")[0], line.split(":")[1].substring(1));
			line = socketInputReader.readLine();
		}
		//body parsing
		if(req.method.equals("POST") && reqheader.containsKey("Content-Length")) {
			int bodyLen = Integer.parseInt(reqheader.get("Content-Length"));
			char[] bodyCharArr = new char[bodyLen+1];
			socketInputReader.read(bodyCharArr, 0, bodyLen);
			req.body = new String(bodyCharArr);
		}
	
		return req;
	}
	
	
	
}
