package network;

import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static int TOTAL_SIGNALS = 400;
	public static GCSignal SIGNALS[] = new GCSignal[TOTAL_SIGNALS];
	public static int GC_INDEX = 0;
	private ServerSocket sock;
	
	public InputStream is;
	public OutputStream os;

	public Server() {
		for (int i = 0; i < TOTAL_SIGNALS; i++) {
			SIGNALS[i] = new GCSignal(new byte[10]);
		}
	}

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
