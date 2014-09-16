package network;

import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Mode;
import flexsc.Party;
import gc.BadLabelException;
import gc.GCGen;
import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import test.parallel.MarkerWithLastValueGadget;
import test.parallel.PrefixSumGadget;
import test.parallel.SubtractGadget;

public class Machine<T> {
	public static boolean DEBUG = true;

	private InputStream masterIs;
	private OutputStream masterOs;
	private Socket masterSocket;
	private ServerSocket serverSocket;
	private Socket[] upSocket;
	private Socket[] downSocket;
	int peerPort;
	int totalMachines;
	int masterPort;
	private Object input;
	boolean isGen;

	protected int machineId;
	protected InputStream[] peerIsUp;
	protected OutputStream[] peerOsUp;
	protected InputStream[] peerIsDown;
	protected OutputStream[] peerOsDown;
	protected int numberOfIncomingConnections;
	protected int numberOfOutgoingConnections;

	public Machine(int masterPort) {
		this.masterPort = masterPort;
	}

	protected void connect() throws InterruptedException, IOException, BadCommandException, ClassNotFoundException {
		connectToMaster(Constants.LOCALHOST, masterPort);
		System.out.println("Connected to master");
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
									 if (isGen) {
										 GCGen.R = GCSignal.receive(masterIs);
									 }
									 int inputLength = NetworkUtil.readInt(masterIs);
									 int inputSize = NetworkUtil.readInt(masterIs);
									 GCSignal[][] gcInput = new GCSignal[inputLength][inputSize];
									 for (int j = 0; j < inputLength; j++)
											for (int k = 0; k < inputSize; k++)
												gcInput[j][k] = GCSignal.receive(masterIs);
									 input = gcInput;
									 /*ObjectInputStream ois = new ObjectInputStream(masterIs);
									 GCSignal[][] input = (GCSignal[][])ois.readObject();
									 System.out.println("first " + input[0][0].toHexStr());*/
									
									 int logMachines = Machine.log2(machines);
									 Master.LOG_MACHINES = logMachines;
									 Master.MACHINES = machines;
									peerIsUp = new BufferedInputStream[logMachines];
									peerOsUp = new BufferedOutputStream[logMachines];
									peerIsDown = new BufferedInputStream[logMachines];
									peerOsDown = new BufferedOutputStream[logMachines];
									upSocket = new Socket[logMachines];
									downSocket = new Socket[logMachines];
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
			System.out.println(getMachineId() + ": " + debug);
		}
	}

	private String getMachineId() {
		return (machineId < 10) ? "0" + machineId : "" + machineId;
	}

	public static void main(String args[]) throws InterruptedException, IOException, BadCommandException, InstantiationException, IllegalAccessException, ClassNotFoundException, BadLabelException {
		int masterPort = Integer.parseInt(args[0]);
		int machineId = Integer.parseInt(args[1]);
		int compPoolGenEvaPort = Integer.parseInt(args[2]);
		Mode mode = Mode.REAL;
		Machine machine = new Machine(masterPort);
		machine.isGen = Boolean.parseBoolean(args[3]);
		// TODO(OT)
		// Connect to the other party
		CompEnv env = machine.connectToOtherParty(machine.isGen, mode, compPoolGenEvaPort);

		System.out.println("Connected to other party");
		// Connect to master and then to peers
		machine.connect();

		long startTime = System.nanoTime();
		Class c = Class.forName("test.parallel.HistogramMapper");
		Gadget histogramMapper = (Gadget) c.newInstance();
		Object[] histogramInputs = new Object[1];
		histogramInputs[0] = machine.input;
		histogramMapper.setInputs(histogramInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown);
		Object[] output = (Object[]) histogramMapper.secureCompute();

		// System.out.println("hello");
		// listen
		c = Class.forName("test.parallel.AnotherSortGadget");
		Gadget gadge = (Gadget) c.newInstance();
		Object[] inputs = new Object[2];
		inputs[0] = output[0];
		inputs[1] = output[1];
		gadge.setInputs(inputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown);
		output = (Object[]) gadge.secureCompute();

		// System.out.println("hello1");
		c = Class.forName("test.parallel.PrefixSumGadget");
		PrefixSumGadget prefixSumGadget = (PrefixSumGadget) c.newInstance();
		Object[] prefixSumInputs = new Object[1];
		prefixSumInputs[0] = output[1];
		prefixSumGadget.setInputs(prefixSumInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections);
		Object[] prefixSumDataResult = (Object[]) prefixSumGadget.secureCompute();

		// System.out.println("hello2");

		c = Class.forName("test.parallel.MarkerWithLastValueGadget");
		MarkerWithLastValueGadget markerGadget = (MarkerWithLastValueGadget) c.newInstance();
		Object[] markerInputs = new Object[1];
		markerInputs[0] = output[0];
		markerGadget.setInputs(markerInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections,
				machine.totalMachines);
		Object marker = markerGadget.secureCompute();

		c = Class.forName("test.parallel.SubtractGadget");
		SubtractGadget subtractGadget = (SubtractGadget) c.newInstance();
		inputs = new Object[3];
		inputs[0] = marker; // sort by flag
		inputs[1] = markerInputs[0]; // actual value
		inputs[2] = prefixSumDataResult[0]; // frequency
		subtractGadget.setInputs(inputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections);
		output = (Object[]) subtractGadget.secureCompute();

		long endTime = System.nanoTime();
		System.out.println(machineId + ": " + env.party + " " + (endTime - startTime)/1000000000.0);
		machine.disconnect();
	}

	CompEnv connectToOtherParty(boolean isGen, Mode mode, int compPoolGenEvaPort) throws InterruptedException, IOException, ClassNotFoundException {
		Party party = isGen ? Party.Alice : Party.Bob;
		InputStream is = null;
		OutputStream os = null;
		if (isGen) {
			Server server = new Server();
			server.listen(compPoolGenEvaPort);
			is = server.is;
			os = server.os;
			/*os.write(10);
			os.flush();*/
		} else {
			Client client = new Client();
			client.connect(Constants.LOCALHOST, compPoolGenEvaPort);
			is = client.is;
			os = client.os;
			/*int inte = is.read();
			while(inte == -1) {
			  inte = is.read();
			}
			System.out.println(inte);*/
		}
		return CompEnv.getEnv(mode, party, is, os);
	}
}
