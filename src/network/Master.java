package network;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import circuits.IntegerLib;

import com.sun.corba.se.spi.extension.ZeroPortPolicy;

public class Master {
	static int BUFFER_SIZE = 655360;
	public static int MACHINES = 4;
	public static int LOG_MACHINES = (int) Math.ceil(Math.log(MACHINES));

	private ServerSocket[] serverSocket;
	public InputStream[] is;
	public OutputStream[] os;

	public Master() {
		serverSocket = new ServerSocket[MACHINES];
		is = new InputStream[MACHINES];
		os = new OutputStream[MACHINES];
	}

	public void listen(int port, int index) throws Exception {
		Socket clientSock;
        serverSocket[index] = new ServerSocket(port);            // create socket and bind to port
        clientSock = serverSocket[index].accept();                   // wait for client to connect
        
        os[index] = new BufferedOutputStream(clientSock.getOutputStream(), BUFFER_SIZE);  
        is[index] = new BufferedInputStream(clientSock.getInputStream(), BUFFER_SIZE);
	}

	public void disconnect() throws Exception {
		for (int i = 0; i < MACHINES; i++) {
			os[i].write(0);
			os[i].flush(); // dummy I/O to prevent dropping connection earlier than
			// protocol payloads are received.
			serverSocket[i].close();
		}
	}

	public void func() throws Exception {
		//CompEnv.getEnv(Mode.REAL, Party.Alice, null, null);
		//IntegerLib<GCSignal> lib = new IntegerLib<>(e);
		int length = 32;
		GCSignal[][] a = new GCSignal[MACHINES][length];
		for (int k = 0; k < LOG_MACHINES; k++) {
			for (int j = 0; j < MACHINES; j++) {
				// TODO(kartiknayak): remove hardcoded length
				for (int i = 0; i < length; i++) {
					a[j][i] = GCSignal.receive(is[j]);
				}
			}
			for (int j = 0; j < MACHINES; j++) {
				int to = j - (1 << k);
				GCSignal[] data = new GCSignal[length];
				for (int i = 0; i < length; i++) {
					data[i] = (GCSignal) GCSignal.ZERO;
					if (to >= 0) {
						data[i] = a[j][i];
					}
				}
				to = (to + MACHINES) % MACHINES;
				for (int i = 0; i < length; i++) {
					data[i].send(os[to]);
				}
				os[to].flush();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		Master master = new Master();
		for (int i = 0; i < MACHINES; i++) {
			master.listen(63254 + i, i);
		}
		System.out.println("connected");
		master.func();
		master.disconnect();
	}
}
