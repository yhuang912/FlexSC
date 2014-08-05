package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	static int bufferSize = 655360;
	private ServerSocket sock;
	
	public InputStream is;
	public OutputStream os;
	
	public void listen(int port) throws Exception {
		Socket clientSock;
        sock = new ServerSocket(port);            // create socket and bind to port
        clientSock = sock.accept();                   // wait for client to connect
        os = new BufferedOutputStream(clientSock.getOutputStream(), bufferSize);  
        is = new BufferedInputStream(clientSock.getInputStream(), bufferSize);
	}

	public void disconnect() throws Exception { 
		is.read();
		os.write(0);
		os.flush();
		sock.close(); 
	}
}
