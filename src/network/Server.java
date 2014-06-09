package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static int bufferSize = 655360;
	private ServerSocket sock;
	
	protected InputStream is;
	protected OutputStream os;
	
	public void listen(int port) throws Exception {
		Socket clientSock;
        sock = new ServerSocket(port);            // create socket and bind to port
        clientSock = sock.accept();                   // wait for client to connect
        
        os = new BufferedOutputStream(clientSock.getOutputStream(), bufferSize);  
        is = new BufferedInputStream( clientSock.getInputStream(), bufferSize);

	}

	public void disconnect() throws Exception { 
		is.read(); 
		os.write(0);
		os.flush(); // dummy I/O to prevent dropping connection earlier than
					// protocol payloads are received.

		sock.close(); 
	}
	
	static public byte[] readBytes(InputStream is, int len) throws IOException
	{
		byte[] temp = new byte[len];
		int remain = len;
		remain -= is.read(temp);
		while(0 != remain)
		{
			remain -= is.read(temp, 10-remain, remain);
		}
		return temp;
	}
}
