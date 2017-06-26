package default_package;
import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket socket;
	
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}
	
	// run method of Runnable interface
	public void run() 
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private static String contentType(String fileName)
	{
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")){
			return "text/html";
		}
		// checks for .jpg and .jpeg
		if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")){
			return "image/jpeg";
		}
		if(fileName.endsWith(".gif")){
			return "image/gif";
		}
		// else
		return "application/octet-stream";
	}
	
	private void processRequest() throws Exception 
	{
		// get reference to socket's input and output streams
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		// Set up input stream filters
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		// Get request line of HTTP request message
		String requestLine = br.readLine();
		
		//Display the request line
		System.out.println();
		System.out.println(requestLine);
		
		// Get and display the header lines
		/*String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) 
		{
			System.out.println(headerLine);
		}
		*/
		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();
		
		// Prepend a "." to request from current directory
		fileName = "." + fileName;
		
		// Open requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		
		// Construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " +
						contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-type: text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
						"<BODY>Not Found</BODY></HTML>";
		}
		
		os.writeBytes(statusLine); // send status
		os.writeBytes(contentTypeLine); // send content type line
		os.writeBytes(CRLF); // end with CRLF per usual
		
		// Send the entity body
		if (fileExists){
			sendBytes(fis, os); // input, output stream
			fis.close();
		} else {
			os.writeBytes(entityBody);
		}
		
		os.close();
		br.close();
		socket.close();
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws IOException {
		// Construct a 1k buffer to hold bytes
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copy requested file from input to output
		while((bytes = fis.read(buffer)) != -1){
			os.write(buffer, 0, bytes);
		}
	}
	
}
