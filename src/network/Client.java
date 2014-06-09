package network;

import java.io.*;
import java.net.Socket;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

public class Client {
	private Socket sock;
	public InputStream is;
	public OutputStream os;
	CountingOutputStream cos;
	CountingInputStream cis;

	public void connect(String server, int port) throws Exception {
        sock = new java.net.Socket(server, port);          // create socket and connect
                
        cos = new CountingOutputStream(sock.getOutputStream());
        cis = new CountingInputStream(sock.getInputStream());
        os = new BufferedOutputStream(cos, Server.bufferSize);  
        is = new BufferedInputStream( cis, Server.bufferSize);
    }

	public void disconnect() throws Exception {
		os.write(0);
		os.flush();
		is.read(); // dummy write to prevent dropping connection earlier than
				   // protocol payloads are received.
		sock.close(); 
	}
	
	public void printStatistic() {
		System.out.println("\n********************************\n"+
		"Data Sent from Client :"+cos.getByteCount()/1024.0/1024.0+"MB\n"+
		"Data Sent to Client :"+cis.getByteCount()/1024.0/1024.0+"MB"+
		"\n********************************");
	}
}
