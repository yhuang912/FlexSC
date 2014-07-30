package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Machine {
	public InputStream[] peerIsServer;
	public OutputStream[] peerOsServer;
	public InputStream[] peerIsClient;
	public OutputStream[] peerOsClient;
	private Socket[] peerSocket;
	private ServerSocket[] peerServerSocket;
	protected int machineId;
	protected int numberOfIncomingConnections;
	protected int numberOfOutgoingConnections;
	ExecutorService executorService;
	int START_PORT;

	public Machine() {
		peerIsServer = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsServer = new BufferedOutputStream[Master.LOG_MACHINES];
		peerIsClient = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsClient = new BufferedOutputStream[Master.LOG_MACHINES];
		peerSocket = new Socket[Master.LOG_MACHINES];
		peerServerSocket = new ServerSocket[Master.LOG_MACHINES];
		executorService = Executors.newFixedThreadPool(Master.LOG_MACHINES);
	}

	public void setMachineId(int machineId) {
		this.machineId = machineId;
		this.numberOfIncomingConnections = getNumberOfIncomingConnections(machineId);
		this.numberOfOutgoingConnections = getNumberOfIncomingConnections(Master.MACHINES - machineId - 1);
	}

	public Future<Socket> listenFromPeer(int port, int index) throws Exception {
		try {
			Socket clientSock;
			peerServerSocket[index] = new ServerSocket(port);            // create socket and bind to port
	        // clientSock = peerServerSocket[index].accept();                   // wait for client to connect
	        Future<Socket> futureSocket = executorService.submit(new WorkerThread(peerServerSocket[index]));
	        return futureSocket;
	
	        /* while (true) {
	        	int read = peerIsServer[index].read();
	        	if (read != -1) {
	        		System.out.println("Read = " + read);
	        		break;
	        	}
	        } */
		} catch (Exception e) {
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			System.out.println("Server not connected properly");
			return null;
		}
	}

	void setStreams(Future<Socket> futureSocket, int index) throws Exception {
		Socket clientSock = futureSocket.get();
		System.out.println("hello " + machineId);
		peerOsServer[index] = new BufferedOutputStream(clientSock.getOutputStream(), Master.BUFFER_SIZE);
        peerIsServer[index] = new BufferedInputStream(clientSock.getInputStream(), Master.BUFFER_SIZE);
	}

	public void connectToPeer(String server, int port, int index) throws Exception {
		while(true){
			try{
				System.out.println("Trying to connect");
				peerSocket[index] = new Socket(server, port);          // create socket and connect
				if(peerSocket[index] != null)
					break;
			}
			catch(IOException e){
				Thread.sleep(100);
				System.out.println("Waiting client ...");
			}
		}

		peerOsClient[index] = new BufferedOutputStream(peerSocket[index].getOutputStream(), Master.BUFFER_SIZE);  
		peerIsClient[index] = new BufferedInputStream(peerSocket[index].getInputStream(), Master.BUFFER_SIZE);
		// peerOsClient[index].write(1);
		// peerOsClient[index].flush();
	}

	public void connectMachines() throws Exception {
		int startPort = START_PORT + machineId;
		int noOfIncomingConnections = numberOfIncomingConnections;
		int noOfOutgoingConnections = numberOfOutgoingConnections;

		System.out.println("reached here " + noOfIncomingConnections + " " + noOfOutgoingConnections);
		ArrayList<Future<Socket>> futureList = new ArrayList<Future<Socket>>();
		for (int i = 0; i < Master.LOG_MACHINES; i++) {
			if (noOfIncomingConnections > 0) {
				futureList.add(listenFromPeer(startPort + i * Master.MACHINES, i));
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				connectToPeer("localhost", startPort - (1 << i) + i * Master.MACHINES, i);
				noOfOutgoingConnections--;
			}
		}
		for (int i = 0; i < numberOfIncomingConnections; i++) {
			setStreams(futureList.get(i), i);
		}
		System.out.println("connected " + machineId);
	}

	private int getNumberOfIncomingConnections(int machineId) {
		int k = 0;
		while (true) {
			if (machineId >= Master.MACHINES - (1 << k)) {
				return k;
			}
			k++;
		}
	}

	public void disconnectFromPeers() throws Exception {
		System.out.println("hanging up");
		for (int i = 0; i < Master.LOG_MACHINES; i++) {
			if (peerOsClient[i] != null) {
				peerOsClient[i].write(0);
				peerOsClient[i].flush(); // dummy I/O to prevent dropping connection earlier than
				// protocol payloads are received.
			}
			if (peerIsClient[i] != null) {
				peerIsClient[i].read();
			}

			if (peerIsServer[i] != null) {
				peerIsServer[i].read();
			}
			if (peerOsServer[i] != null) {
				peerOsServer[i].write(0);
				peerOsServer[i].flush();
			}
			
			Thread.sleep(100000);
			if (peerServerSocket[i] != null) {
				peerServerSocket[i].close();
			}
			if (peerSocket[i] != null) {
				peerSocket[i].close();
			}
		}
	}

	/* public static void main(String args[]) throws Exception {
		int machineId = Integer.parseInt(args[0]);
		Machine newMachine = new Machine();
		newMachine.connectToMaster("localhost", Master.START_PORT + machineId);
		System.out.println("connected");
		newMachine.prefixSum(Integer.parseInt(args[1]));
		newMachine.disconnect();
	}

	public void prefixSum(int sum) throws Exception {
		for (int k = 0; k < Master.LOG_MACHINES; k++) {
			masterOs.write(sum);
			masterOs.flush();
			sum += masterIs.read();
		}
		System.out.println("Sum = " + sum);
	} */

	public void setStartPort(int startPort) {
		START_PORT = startPort;
	}
}
