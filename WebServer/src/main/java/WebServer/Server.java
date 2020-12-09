package WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
// RequestParser, ResponseParser, Routing, Thread, 예외처리(서버다운안되게), persistent http, 백엔드에서 request param 받을수있게
// Final objective => Lambda clone coding or Tomcat
public class Server {
	RequestPool requests;
	WorkerPool workers;
	WorkerManager manager;
	ServerSocket socket;
	int port;
	
	static final int TIMEOUT = 5000;
	static final int MAXCONNECTION = 100;
	
	Server(int port) throws IOException{
		//requests = new RequestPool();
		//workers = new WorkerPool(requests, 5);
		//manager = new WorkerManager(workers,requests);
		this.port = port;

		socket = new ServerSocket(port);
		System.out.println("Listening "+port+"...");
		
		//manager.start();
	}
	public ServerSocket getServerSocket() {
		return socket;
	}
	public void addRequest(InputStream request) {
		//requests.add(new Request());
	}
	public static void main(String args[]) {
		try {
			boolean flag = true;
			
			Server server = new Server(8080);
			RequestParser reqParser = new RequestParser();
			
			Socket sock = server.getServerSocket().accept();
			System.out.println("Accessed by " + sock.getInetAddress());
			InputStream sockIS = sock.getInputStream();
			
			Timer m_timer = new Timer();
			TimerTask m_task = new TimerTask() {
				@Override
				public void run() {
					try {
						sock.close();
						System.out.println("Connection closed");
					} catch(IOException e) {
						System.out.println(e);
					}
				}
			};
			m_timer.schedule(m_task, TIMEOUT);

			while(!sock.isClosed()) {
				if(sockIS.available() > 0) {
					
					m_timer.cancel();
					System.out.println("New Request!! " + sockIS);
					Request req = reqParser.getRequest(sockIS);
					
					System.out.println("Requested Path: " + req.requestURI);
					System.out.println("Connection: " + req.header.get("Connection"));
					System.out.println("Cookie: " + req.header.get("Cookie"));

					OutputStream os = sock.getOutputStream();
					DataOutputStream dos = new DataOutputStream(os); 
					
		            String requestURI = route(req.requestURI);
					sendResponse(dos, requestURI);
		            System.out.println("Response sent!!");
		            
		            m_timer = new Timer();
		            m_task = new TimerTask() {
						@Override
						public void run() {
							try {
								sock.close();
								System.out.println("Connection closed");
							} catch(IOException e) {
								System.out.println(e);
							}
						}
					};
					m_timer.schedule(m_task, TIMEOUT);
				}
			}
			
			} catch(IOException e) {
				System.out.println(e);
			}
		
	}

	public static void sendResponse(DataOutputStream dos, String requestURI) throws IOException{
		String extention;
		String[] splitedURI = requestURI.split("[.]");
		if(splitedURI.length== 1) { // if this indicates directory
			extention = "html";
			requestURI = requestURI + "/index.html";
		}
		else {
			extention = splitedURI[1];
		}
		
		byte[] content = readFile(dos, requestURI);
		
		System.out.println("Extention: " +  extention);
		
		Response res = new Response();
		res.statusCode = 200;
		res.reasonPhrase = "OK";

		if(extention.equals("html"))
			res.addHeader("Content-Type", "text/html;charset=utf-8");
		else if(extention.equals("png"))
			res.addHeader("Content-Type", "image/png");
		else if(extention.equals("jpeg"))
			res.addHeader("Content-Type", "image/jpeg");
		else if(extention.equals("ico"))
			res.addHeader("Content-Type", "image/x-icon");
		else
			res.addHeader("Content-Type", "text/plain");
		
		res.addHeader("Content-Length", Integer.toString(content.length)); 		
		res.addHeader("Connection", "Keep-Alive");
		//set cookie
		res.addCookie("greeting", "hello");
		res.addCookie("greeting2", "hi");
		
		res.body = content;
		
        dos.write(res.toBytes());
        dos.write(content);
        dos.writeBytes("\r\n");
        
		dos.flush();
		
	}
	public static byte[] readFile(DataOutputStream dos, String path) throws IOException {
		//base64 encoding, decoding
		//https://dev-syhy.tistory.com/15
		byte[] content = Files.readAllBytes(Paths.get(path));
		
		return content;
	}

	public static String route(String uri) {
		String base = "/home/sejongkim/eclipse-workspace/WebServer/src/main/resources";
		String requestedPath = base + uri;
		
		//check whether there is the file in the requestedPath
		if(!new File(requestedPath).exists()) {
			System.out.println(">> Wrong Path!, Redirect to main page");
			requestedPath = base + "/index.html";
		}
		
		System.out.println(requestedPath);
		
		return requestedPath;
		
	}
	
}
