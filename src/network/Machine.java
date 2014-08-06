package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Machine {
	public static boolean DEBUG = true;

	private InputStream masterIs;
	private OutputStream masterOs;
	private Socket masterSocket;
	private ServerSocket serverSocket;
	private Socket[] upSocket;
	private Socket[] downSocket;
	private int machineId;
	int peerPort;
	int totalMachines;

	protected InputStream[] peerIsUp;
	protected OutputStream[] peerOsUp;
	protected InputStream[] peerIsDown;
	protected OutputStream[] peerOsDown;
	protected int numberOfIncomingConnections;
	protected int numberOfOutgoingConnections;

	public Machine() {
		peerIsUp = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsUp = new BufferedOutputStream[Master.LOG_MACHINES];
		peerIsDown = new BufferedInputStream[Master.LOG_MACHINES];
		peerOsDown = new BufferedOutputStream[Master.LOG_MACHINES];
		upSocket = new Socket[Master.LOG_MACHINES];
		downSocket = new Socket[Master.LOG_MACHINES];
	}

	protected void connect(int masterPort) throws InterruptedException, IOException, BadCommandException {
		connectToMaster(Constants.LOCALHOST, masterPort);
		while(true) {
			Command command = Command.valueOf(NetworkUtil.readInt(masterIs));
			switch(command) {
				case LISTEN: debug("listen triggered");
					         listenFromPeer(peerPort + machineId);
							 break;
				case SET_MACHINE_ID: int id = NetworkUtil.readInt(masterIs);
									 int peerPort = NetworkUtil.readInt(masterIs);
									 int machines = NetworkUtil.readInt(masterIs);
									 setMachineId(id, peerPort, machines);
									 break;
				case CONNECT: connectToPeers();
							  break;
				case COMPUTE: return;
				default:
					throw new BadCommandException("Unknown command. Default switch case");
			}
		}
	}

	public void setMachineId(int machineId, int peerPort, int totalMachines) {
		this.totalMachines = totalMachines;
		this.machineId = machineId;
		this.numberOfIncomingConnections = getNumberOfIncomingConnections(machineId);
		this.numberOfOutgoingConnections = getNumberOfIncomingConnections(totalMachines - machineId - 1);
		this.peerPort = peerPort;
	}

	public void listenFromPeer(int port) {
		Socket clientSock = null;
        try {
        	debug("Port listening from " + port);
			serverSocket = new ServerSocket(port);
			// Send Ack to Master before accept
			NetworkUtil.writeInt(masterOs, 1);
			masterOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}            // create socket and bind to port

        for (int i = 0; i < numberOfIncomingConnections; i++) {
        	try {
				clientSock = serverSocket.accept();
				InputStream is;
				OutputStream os;
				os = new BufferedOutputStream(clientSock.getOutputStream(), Constants.BUFFER_SIZE);
				is = new BufferedInputStream(clientSock.getInputStream(), Constants.BUFFER_SIZE);
				int id = NetworkUtil.readInt(is);
				int index = log2(id - machineId);
				debug("Accepted a connection from " + id + ". Stored at index " + index);
				downSocket[index] = clientSock;
				peerIsDown[index] = is;
				peerOsDown[index] = os;
				debug(id + " peerIsDown " + peerIsDown[index].hashCode());
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
	}

	public void connectToPeers() throws InterruptedException {
		for (int i = 0; i < numberOfOutgoingConnections; i++) {
			debug("I'm trying to connect to " + (machineId - (1 << i)) + " at " + (peerPort + machineId - (1 << i)) + ". Storing connection at " + i);
			// System.out.println(machineId + ": I have " + (numberOfOutgoingConnections - i) + " remaining");
			Socket peerSocket = NetworkUtil.connect(Constants.LOCALHOST, peerPort + machineId - (1 << i));
			try {
				upSocket[i] = peerSocket;
				peerOsUp[i] = new BufferedOutputStream(peerSocket.getOutputStream(), Constants.BUFFER_SIZE);
				peerIsUp[i] = new BufferedInputStream(peerSocket.getInputStream(), Constants.BUFFER_SIZE);
				NetworkUtil.writeInt(peerOsUp[i], machineId);
				peerOsUp[i].flush();
				debug((machineId - (1 << i)) + "peerOsUp " + peerOsUp[i].hashCode());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		debug("I'm done connecting ");
		try {
			NetworkUtil.writeInt(masterOs, Response.SUCCESS.getValue());
			masterOs.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connectToMaster(String server, int port) throws InterruptedException {
		masterSocket = NetworkUtil.connect(server, port);

		try {
			masterOs = new BufferedOutputStream(masterSocket.getOutputStream(), Constants.BUFFER_SIZE);
			masterIs = new BufferedInputStream(masterSocket.getInputStream(), Constants.BUFFER_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	public void disconnect() throws IOException {
		disconnectFromMaster();
		disconnectFromPeers();
	}

	private void disconnectFromMaster() throws IOException {
		masterSocket.close();
	}

	private void disconnectFromPeers() throws IOException {
		for (int i = 0; i < numberOfIncomingConnections; i++) {
			downSocket[i].close();
		}
		for (int i = 0; i < numberOfOutgoingConnections; i++) {
			upSocket[i].close();
		}
	}

	private int getNumberOfIncomingConnections(int machineId) {
		int k = 0;
		while (true) {
			if (machineId >= totalMachines - (1 << k)) {
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

	protected void debug(String debug) {
		if (DEBUG) {
			System.out.println(machineId + ": " + debug);
		}
	}
}
