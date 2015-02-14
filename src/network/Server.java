package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import flexsc.Flag;
import flexsc.Mode;

public class Server extends Network{

	private ServerSocket sock;

	public void listen(int port) throws Exception {
		Socket clientSock;
		sock = new ServerSocket(port); // create socket and bind to port
		clientSock = sock.accept(); // wait for client to connect

		os = new BufferedOutputStream(clientSock.getOutputStream(), bufferSize);
		is = new BufferedInputStream(clientSock.getInputStream(), bufferSize);
		setUpThread();
	}
	


	public void disconnect() throws Exception {
		is.read();
		os.write(0);
		os.flush(); // dummy I/O to prevent dropping connection earlier than
					// protocol payloads are received.
		sock.close();
		if(Flag.mode == Mode.OFFLINE && !Flag.offline) {
			try {
				gc.offline.GCGen.fout.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
