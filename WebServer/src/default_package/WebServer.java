package default_package;
import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer 
{	
	public static void main(String argv[]) throws Exception
	{
		// set port number
		int port = 6789;
		
		// Establish listen socket
		ServerSocket serverSocket = new ServerSocket(port);
		
		// Process HTTP service requests in inf loop
		while (true){
			// Listen for TCP connection request and accepts it
			Socket requestSocket = serverSocket.accept();
		
			// Construct an object to process HTTP request message
			HttpRequest request = new HttpRequest(requestSocket);
			
			// Create a new thread to process the request
			Thread thread = new Thread(request);
			
			// Start thread
			thread.start();
			
		}
	}
}
