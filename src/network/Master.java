package network;

import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {
	static int BUFFER_SIZE = 655360;
	public static int MACHINES = 32;
	public static int LOG_MACHINES = Machine.log2(MACHINES);
	static int START_PORT;

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
			NetworkUtil.writeInt(os[i], 0);
			os[i].flush(); // dummy I/O to prevent dropping connection earlier than
			// protocol payloads are received.
			serverSocket[i].close();
		}
	}

	public void func() throws Exception {
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

	public void connect(int peerPort) throws Exception {
		// set machineId for each of the machines
		for (int i = 0; i < MACHINES; i++) {
			NetworkUtil.writeInt(os[i], Command.SET_MACHINE_ID.getValue());
			NetworkUtil.writeInt(os[i], i);
			NetworkUtil.writeInt(os[i], peerPort);
			os[i].flush();
		}

		// Ask all machines except for the last to listen
		for (int i = 0; i < MACHINES - 1; i++) {
			NetworkUtil.writeInt(os[i], Command.LISTEN.getValue());
			os[i].flush();
		}

		// Wait for machines' to start listening
		for (int i = 0; i < MACHINES - 1; i++) {
			readResponse(i);
		}

		// Ask all machines to connect, one at a time
		System.out.println("Everyone started listening!");
		for (int i = MACHINES - 1; i > 0; i--) {
			NetworkUtil.writeInt(os[i], Command.CONNECT.getValue());
			os[i].flush();

			readResponse(i);
			System.out.println("Reached " + i);
		}

		// let all machines start computing
		for (int i = 0; i < MACHINES; i++) {
			NetworkUtil.writeInt(os[i], Command.COMPUTE.getValue());
			os[i].flush();
		}
	}

	private void readResponse(int machineId) throws IOException, Exception {
		int ret = NetworkUtil.readInt(is[machineId]);
		if (ret == -1) {
			throw new Exception("Why is ret -1?");
		}
		if (ret != Response.SUCCESS.getValue()) {
			throw new Exception("Slave listen failed");
		}
	}

	public static void main(String args[]) throws Exception {
		Master master = new Master();
		Master.START_PORT = Integer.parseInt(args[0]);
		int peerPort = Integer.parseInt(args[1]);
		for (int i = 0; i < MACHINES; i++) {
			master.listen(Master.START_PORT + i, i);
		}
		System.out.println("connected master");
		master.connect(peerPort);
		System.out.println("master says connections successful");
	}
}
