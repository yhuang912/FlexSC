package network;

import java.io.*;
import java.net.Socket;

public class Client {
	private Socket sock;
	public InputStream is;
	public OutputStream os;

	public void connect(String server, int port) throws Exception {
        sock = new java.net.Socket(server, port);          // create socket and connect
        os  = sock.getOutputStream();  
        is  = sock.getInputStream();
    }

	public void disconnect() throws Exception {
		os.write(0);
		os.flush();  
		is.read(); // dummy write to prevent dropping connection earlier than
				   // protocol payloads are received.
		
		sock.close(); }
}
