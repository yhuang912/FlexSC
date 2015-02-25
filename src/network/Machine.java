package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

import test.parallel.ParallelGadget;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.BadLabelException;
import gc.GCGen;
import gc.GCSignal;

public class Machine {
	public static boolean DEBUG = false;
	public static int BW = 0;
	public static int ITERATION = -1;

	private InputStream masterIs;
	private OutputStream masterOs;
	private Socket masterSocket;
	private ServerSocket serverSocket;
	private Socket[] upSocket;
	private Socket[] downSocket;
	int peerPort;
	public int totalMachines;
	public int logMachines;
	int masterPort;
	public Object input;
	boolean isGen;
	public int inputLength;

	public int machineId;
	public InputStream[] peerIsUp;
	public OutputStream[] peerOsUp;
	public InputStream[] peerIsDown;
	public OutputStream[] peerOsDown;
	public int numberOfIncomingConnections;
	public int numberOfOutgoingConnections;
	ParallelGadget parallelGadget;
	public CountingInputStream[] cisUp;
	public CountingInputStream[] cisDown;
	public CountingOutputStream[] cosUp;
	public CountingOutputStream[] cosDown;

	public CompEnv env;

	public CountingOutputStream cosOther;
	public CountingInputStream cisOther;

	public static double RAND[];

	public static int RAND_CNT = 0;

	public Machine(int masterPort) {
		this.masterPort = masterPort;
	}

	protected void connect(IPManager ipManager) throws InterruptedException, IOException, BadCommandException, ClassNotFoundException {
		String masterIp = isGen ? ipManager.masterGarblerIp : ipManager.masterEvaluatorIp;
		connectToMaster(masterIp, masterPort);
		debug("Connected to master");
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
//									 int inputSize = NetworkUtil.readInt(masterIs);
				                     input = parallelGadget.readInputFromMaster(inputLength, masterIs, env);
									 int logMachines = Machine.log2(machines);
									 peerIsUp = new BufferedInputStream[logMachines];
									 peerOsUp = new BufferedOutputStream[logMachines];
									 peerIsDown = new BufferedInputStream[logMachines];
									 peerOsDown = new BufferedOutputStream[logMachines];

									 cisUp = new CountingInputStream[logMachines];
									 cisDown = new CountingInputStream[logMachines];
									 cosUp = new CountingOutputStream[logMachines];
									 cosDown = new CountingOutputStream[logMachines];

									 upSocket = new Socket[logMachines];
									 downSocket = new Socket[logMachines];
									 break;
				case CONNECT: connectToPeers(ipManager);
							  break;
				case COMPUTE: return;
				default:
					throw new BadCommandException("Unknown command. Default switch case");
			}
		}
	}

	public void setMachineId(int machineId, int peerPort, int totalMachines) {
		this.totalMachines = totalMachines;
		this.logMachines = Machine.log2(totalMachines);
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
				CountingOutputStream cos = null;
				CountingInputStream cis = null;
				if (Flag.countIO) {
					cos = new CountingOutputStream(clientSock.getOutputStream());
					cis = new CountingInputStream(clientSock.getInputStream());
					os = new BufferedOutputStream(cos, Constants.BUFFER_SIZE);
					is = new BufferedInputStream(cis, Constants.BUFFER_SIZE);
				} else {
					os = new BufferedOutputStream(clientSock.getOutputStream(), Constants.BUFFER_SIZE);
					is = new BufferedInputStream(clientSock.getInputStream(), Constants.BUFFER_SIZE);
				}
				int id = NetworkUtil.readInt(is);
				int index = log2(id - machineId);
				debug("Accepted a connection from " + id + ". Stored at index " + index);
				downSocket[index] = clientSock;
				peerIsDown[index] = is;
				peerOsDown[index] = os;
				cisDown[index] = cis;
				cosDown[index] = cos;
				debug(id + " peerIsDown " + peerIsDown[index].hashCode());
			} catch (IOException e) {
				e.printStackTrace();
			}  
        }
	}

	public void connectToPeers(IPManager ipManager) throws InterruptedException {
		for (int i = 0; i < numberOfOutgoingConnections; i++) {
			debug("I'm trying to connect to " + (machineId - (1 << i)) + " at " + (peerPort + machineId - (1 << i)) + ". Storing connection at " + i);
			// System.out.println(machineId + ": I have " + (numberOfOutgoingConnections - i) + " remaining");
			String peerIp = null;
			if (isGen) {
				peerIp = ipManager.gIp[(machineId - (1 << i))];
			} else {
				peerIp = ipManager.eIp[(machineId - (1 << i))];
			}
			Socket peerSocket = NetworkUtil.connect(peerIp, peerPort + machineId - (1 << i));
			try {
				upSocket[i] = peerSocket;
				if (Flag.countIO) {
					cosUp[i] = new CountingOutputStream(peerSocket.getOutputStream());
					cisUp[i] = new CountingInputStream(peerSocket.getInputStream());
					peerOsUp[i] = new BufferedOutputStream(cosUp[i], Constants.BUFFER_SIZE);
					peerIsUp[i] = new BufferedInputStream(cisUp[i], Constants.BUFFER_SIZE);
				} else {
					peerOsUp[i] = new BufferedOutputStream(peerSocket.getOutputStream(), Constants.BUFFER_SIZE);
					peerIsUp[i] = new BufferedInputStream(peerSocket.getInputStream(), Constants.BUFFER_SIZE);
				}
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

	CompEnv connectToOtherParty(boolean isGen, Mode mode, int compPoolGenEvaPort, IPManager ipManager) throws InterruptedException, IOException, ClassNotFoundException {
		Party party = isGen ? Party.Alice : Party.Bob;
		InputStream is = null;
		OutputStream os = null;
		if (isGen) {
			Server server = new Server();
			server.listen(compPoolGenEvaPort);
			is = server.is;
			os = server.os;
		} else {
			Client client = new Client();
			// my evaluator's machine id is going to be the same as mine
			client.connect(ipManager.gIp[machineId], compPoolGenEvaPort);
			is = client.is;
			os = client.os;
//			cisOther = client.cis;
//			cosOther = client.cos;
		}
		return CompEnv.getEnv(mode, party, is, os);
	}

	public static <T> void main(String args[]) throws InterruptedException, IOException, BadCommandException, InstantiationException, IllegalAccessException, ClassNotFoundException, BadLabelException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		BW = Integer.parseInt(args[10]);
		ITERATION = Integer.parseInt(args[11]);
		int machines = Integer.parseInt(args[7]);
		String machineConfig = args[8];
		IPManager ipManager = IPManager.loadIPs(machines, machineConfig);
		int masterPort = Integer.parseInt(args[0]);
		int machineId = Integer.parseInt(args[1]);
		int compPoolGenEvaPort = Integer.parseInt(args[2]);
		Mode mode = Mode.valueOf(args[9]);
		Machine machine = new Machine(masterPort);
		machine.isGen = Boolean.parseBoolean(args[3]);
		machine.inputLength = Integer.parseInt(args[4]);
		int firstPhysicalMachineId = Integer.parseInt(args[5]);
		String experiment = args[6];
		machine.machineId = machineId;
		Class c = Class.forName("test.parallel." + experiment);
		// machine.parallelGadget = new Histogram();
		machine.parallelGadget = (ParallelGadget) c.newInstance();
		// TODO(OT)
		// Connect to the other party
		machine.env = machine.connectToOtherParty(machine.isGen, mode, compPoolGenEvaPort, ipManager);

		// Connect to master and then to peers
		machine.connect(ipManager);

		long startTime = System.nanoTime();
		machine.debug("Calling compute");
		machine.parallelGadget.compute(machineId, machine, machine.env);

		long endTime = System.nanoTime();
		/*Statistics a = ((PMCompEnv) env).statistic;
		a.finalize();
		System.out.println(machineId + ": " + a.andGate + " " + a.NumEncAlice);*/
		if (Flag.countIO) {
			long outCountUp = 0, inCountUp = 0;
			for (int i = 0; i < machine.numberOfOutgoingConnections; i++) {
				outCountUp += machine.cosUp[i].getByteCount();
				inCountUp += machine.cisUp[i].getByteCount();
			}
			long outCountDown = 0, inCountDown = 0;
			for (int i = 0; i < machine.numberOfIncomingConnections; i++) {
				outCountDown += machine.cosDown[i].getByteCount();
				inCountDown += machine.cisDown[i].getByteCount();
			}
			if (!machine.isGen) {
				System.out.println(machine.machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + getMega(outCountUp + outCountDown) + ",out" );
				System.out.println(machine.machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + getMega(inCountUp + inCountDown) + ",in" );
				System.out.println(machine.machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + getMega(machine.cisOther.getByteCount()) + ",GE" );
			}	
		}
		machine.disconnect();

//		if (machine.machineId == firstPhysicalMachineId && (Mode.REAL.equals(machine.env.getMode()) || Party.Alice.equals(machine.env.party))) {
		if (machine.machineId == firstPhysicalMachineId) {
			// Thread.sleep(60000);
			PrintWriter writer = new PrintWriter("mutex.txt");
			writer.close();
		}
//		FloatLib lib = new FloatLib(machine.env, 20, 11);
//		T[] one = (T[]) machine.env.inputOfAlice(Utils.fromFloat(0, 20, 11));
//		T[] two = (T[]) machine.env.inputOfAlice(Utils.fromFloat(2, 20, 11));
//		T[] div = (T[]) lib.div((T[]) one, (T[]) two);
//		double d = lib.outputToAlice(div);
//		if (Party.Alice.equals(machine.env.party)) {
//			System.out.println("hello " + d);
//		}
	}

	private static double getMega(double bytes) {
		return bytes/(1024.0 * 1024);
	}
}
