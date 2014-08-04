package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Machine {
	public static String LOCALHOST = "localhost";
	public InputStream masterIs;
	public OutputStream masterOs;
	private Socket masterSocket;
	private ServerSocket serverSocket;
	public InputStream[] peerIsUp;
	public OutputStream[] peerOsUp;
	public InputStream[] peerIsDown;
	public OutputStream[] peerOsDown;
	public int machineId;
	protected int numberOfIncomingConnections;
	protected int numberOfOutgoingConnections;
	int peerPort;

	public Machine() {
		peerIsUp = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsUp = new BufferedOutputStream[Master.LOG_MACHINES];
		peerIsDown = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsDown = new BufferedOutputStream[Master.LOG_MACHINES];
	}

	protected void connect(int masterPort) throws Exception {
		connectToMaster(LOCALHOST, masterPort);
		while(true) {
			Command command = Command.valueOf(Master.readInt(masterIs));
			switch(command) {
				case LISTEN: System.out.println(machineId + ": listen triggered");
					         listenFromPeer(peerPort + machineId);
							 break;
				case SET_MACHINE_ID: int id = Master.readInt(masterIs);
									 int peerPort = Master.readInt(masterIs);
									 setMachineId(id, peerPort);
									 break;
				case CONNECT: connectToPeers();
							  break;
				case COMPUTE: return;
				default:
					System.out.println("Default switch case");
					break;
									 
			}
		}
	}

	public void setMachineId(int machineId, int peerPort) {
		this.machineId = machineId;
		this.numberOfIncomingConnections = getNumberOfIncomingConnections(machineId);
		this.numberOfOutgoingConnections = getNumberOfIncomingConnections(Master.MACHINES - machineId - 1);
		this.peerPort = peerPort;
	}

	public void listenFromPeer(int port) {
		Socket clientSock = null;
        try {
        	System.out.println(machineId + ": Port listening from " + port);
			serverSocket = new ServerSocket(port);
			// Send Ack to Master before accept
			Master.writeInt(masterOs, 1);
			masterOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}            // create socket and bind to port

        for (int i = 0; i < numberOfIncomingConnections; i++) {
        	try {
				clientSock = serverSocket.accept();
				InputStream is;
				OutputStream os;
				os = new BufferedOutputStream(clientSock.getOutputStream(), Master.BUFFER_SIZE);
				is = new BufferedInputStream(clientSock.getInputStream(), Master.BUFFER_SIZE);
				int id = Master.readInt(is);
				int index = log2(id - machineId);
				System.out.println(machineId + ": Accepted a connection from " + id + ". Stored at index " + index);
				peerIsDown[index] = is;
				peerOsDown[index] = os;
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
	}

	public void connectToPeers() throws InterruptedException {
		for (int i = 0; i < numberOfOutgoingConnections; i++) {
			System.out.println(machineId + ": I'm trying to connect to " + (machineId - (1 << i)) + " at " + (peerPort + machineId - (1 << i)) + ". Storing connection at " + i);
			// System.out.println(machineId + ": I have " + (numberOfOutgoingConnections - i) + " remaining");
			Socket peerSocket = connect(LOCALHOST, peerPort + machineId - (1 << i));
			try {
				peerOsUp[i] = new BufferedOutputStream(peerSocket.getOutputStream(), Master.BUFFER_SIZE);
				peerIsUp[i] = new BufferedInputStream(peerSocket.getInputStream(), Master.BUFFER_SIZE);
				Master.writeInt(peerOsUp[i], machineId);
				peerOsUp[i].flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(machineId + ": I'm done connecting ");
		try {
			Master.writeInt(masterOs, 1 /* SUCCESS */);
			masterOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connectToMaster(String server, int port) throws InterruptedException {
		masterSocket = connect(server, port);

		try {
			masterOs = new BufferedOutputStream(masterSocket.getOutputStream(), Master.BUFFER_SIZE);
			masterIs = new BufferedInputStream(masterSocket.getInputStream(), Master.BUFFER_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	private Socket connect(String server, int port) throws InterruptedException {
		Socket socket;
		while(true){
			try{
				socket = new Socket(server, port);          // create socket and connect
				if(socket != null)
					break;
			}
			catch(IOException e){
				Thread.sleep(100);
			}
		}
		return socket;
	}

	public void disconnectFromMaster() throws Exception {
		masterOs.write(0);
		masterOs.flush(); // dummy I/O to prevent dropping connection earlier than
		// protocol payloads are received.
		masterSocket.close();
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

	private int getNumberOfIncomingConnections(int machineId) {
		int k = 0;
		while (true) {
			if (machineId >= Master.MACHINES - (1 << k)) {
				return k;
			}
			k++;
		}
	}

	public static int log2(int n){
	    if(n <= 0) {
	    	throw new IllegalArgumentException();
	    }
	    return 31 - Integer.numberOfLeadingZeros(n);
	}

	/*public byte[] readBytes(InputStream is, int len) throws IOException {
		byte[] temp = new byte[len];
		int remain = len;
		remain -= is.read(temp);
		while(0 != remain) {
			remain -= is.read(temp, len-remain, remain);
		}
		return temp;
	}

	public int readInt(InputStream is) throws IOException
	{
		byte[] lenBytes = readBytes(is, 4);
		return ByteBuffer.wrap(lenBytes).getInt();
	}

	public void writeInt(OutputStream os, int data) throws IOException {
		os.write(ByteBuffer.allocate(4).putInt(data).array());
	}*/
}
