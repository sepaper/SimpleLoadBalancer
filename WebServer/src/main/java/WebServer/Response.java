package WebServer;

import java.util.HashMap;

public class Response {
	String httpVersion;
	int statusCode;
	String reasonPhrase;
	HashMap<String, String> header;
	
	//res.body.toString != new String(res.body)
	 byte[] body;
	
	Response() {
		httpVersion = "HTTP/1.1";
		header = new HashMap<String, String>();
	}

	
	public void addHeader(String name, String value) {
		header.put(name, value);
	}
	public void addCookie(String name, String value) {
		header.put("Set-Cookie", String.format("%s=%s", name, value));
	}
	
	public byte[] toBytes() {
		String statusLine;
		StringBuffer ret = new StringBuffer();
		
		statusLine = String.format("%s %d %s\r\n", httpVersion, statusCode, reasonPhrase);
		
		ret.append(statusLine);
		header.forEach((name, value)->{
			String headerLine = String.format("%s: %s\r\n", name, value);
			ret.append(headerLine);
		});
		ret.append("\r\n");		 // need to be refactored
		//ret.append(body);
		//ret.append("\r\n");	
		
		return ret.toString().getBytes();
	}
	
	
}
