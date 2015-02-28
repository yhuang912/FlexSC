package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket sock;
	
	public InputStream is;
	public OutputStream os;

	public void listen(int port) throws IOException {
		Socket clientSock;
        sock = new ServerSocket(port);            // create socket and bind to port
        clientSock = sock.accept();                   // wait for client to connect
        os = new BufferedOutputStream(clientSock.getOutputStream());  
        is = new BufferedInputStream(clientSock.getInputStream());
	}

	public void disconnect() throws IOException { 
		sock.close(); 
	}
}
