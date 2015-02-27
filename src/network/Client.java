package network;

import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	private Socket sock = null;
	public InputStream is;
	public OutputStream os;
//	CountingOutputStream cos;
//	CountingInputStream cis;

	public Client() {
		for (int i = 0; i < Server.TOTAL_SIGNALS; i++) {
			Server.SIGNALS[i] = new GCSignal(new byte[10]);
		}
	}

	public void connect(String server, int port) throws InterruptedException, IOException {
		sock = NetworkUtil.connect(server, port);

//		if (Flag.countIO) {
//			cos = new CountingOutputStream(sock.getOutputStream());
//			cis = new CountingInputStream(sock.getInputStream());
//			os = new BufferedOutputStream(cos, Constants.BUFFER_SIZE);  
//			is = new BufferedInputStream( cis, Constants.BUFFER_SIZE);
//		}
//		else{
			os = new BufferedOutputStream(sock.getOutputStream());  
			is = new BufferedInputStream(sock.getInputStream());
//		} 
	}

	public void disconnect() throws IOException {
		sock.close(); 
	}

//	public void printStatistic() {
//		if(Flag.countIO){
//			System.out.println("\n********************************\n"+
//					"Data Sent from Client :"+cos.getByteCount()/1024.0/1024.0+"MB\n"+
//					"Data Sent to Client :"+cis.getByteCount()/1024.0/1024.0+"MB"+
//					"\n********************************");
//		}
//	}
}
