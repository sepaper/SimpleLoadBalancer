package WebServer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;

public class Request {
	String method;
	String requestURI;
	String httpVersion;
	HashMap<String, String> header;
	String body;
	
	
	Request() {
		header = new HashMap<String, String>();
	}
	
	public String getMsg() {
		return "";
	}
}
