package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ot.IncorrectOtUsageException;
import test.parallel.ParallelGadget;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCGen;
import gc.GCSignal;

public class Master {
	public static int START_PORT;

	private ServerSocket[] serverSocket;
	public InputStream[] is;
	public OutputStream[] os;
	public boolean isGen;
	private int machines;
	ParallelGadget parallelGadget;

	public Master(int machines) {
		this.machines = machines;
		serverSocket = new ServerSocket[machines];
		is = new InputStream[machines];
		os = new OutputStream[machines];
	}

	public void listen(int port, int index) throws IOException {
		Socket clientSock;
        serverSocket[index] = new ServerSocket(port);            // create socket and bind to port
        clientSock = serverSocket[index].accept();                   // wait for client to connect
        
        os[index] = new BufferedOutputStream(clientSock.getOutputStream(), Constants.BUFFER_SIZE);  
        is[index] = new BufferedInputStream(clientSock.getInputStream(), Constants.BUFFER_SIZE);
	}

	public void disconnect() throws IOException {
		for (int i = 0; i < machines; i++) {
			serverSocket[i].close();
		}
	}

	/*public void func() throws IOException {
		int length = 32;
		GCSignal[][] a = new GCSignal[machines][length];
		for (int k = 0; k < logMachines; k++) {
			for (int j = 0; j < machines; j++) {
				for (int i = 0; i < length; i++) {
					a[j][i] = GCSignal.receive(is[j]);
				}
			}
			for (int j = 0; j < machines; j++) {
				int to = j - (1 << k);
				GCSignal[] data = new GCSignal[length];
				for (int i = 0; i < length; i++) {
					data[i] = (GCSignal) GCSignal.ZERO;
					if (to >= 0) {
						data[i] = a[j][i];
					}
				}
				to = (to + machines) % machines;
				for (int i = 0; i < length; i++) {
					data[i].send(os[to]);
				}
				os[to].flush();
			}
		}
	}*/

	public void setUp(int peerPort, int inputLength, CompEnv env) throws IOException, BadResponseException, IncorrectOtUsageException {
		// set machineId for each of the machines
		for (int i = 0; i < machines; i++) {
			NetworkUtil.writeInt(os[i], Command.SET_MACHINE_ID.getValue());
			NetworkUtil.writeInt(os[i], i);
			NetworkUtil.writeInt(os[i], peerPort);
			NetworkUtil.writeInt(os[i], machines);
			if (isGen) {
				GCGen.R.send(os[i]);
			}
			os[i].flush();
			// oos.flush();
		}
		parallelGadget.sendInputToMachines(inputLength, machines, isGen, env, os);
		// System.out.println("OT done");

		// Ask all machines except for the last to listen
		for (int i = 0; i < machines - 1; i++) {
			NetworkUtil.writeInt(os[i], Command.LISTEN.getValue());
			os[i].flush();
		}

		// Wait for machines' to start listening
		for (int i = 0; i < machines - 1; i++) {
			readResponse(i);
		}

		// Ask all machines to connect, one at a time
		// System.out.println("Everyone started listening!");
		for (int i = machines - 1; i > 0; i--) {
			NetworkUtil.writeInt(os[i], Command.CONNECT.getValue());
			os[i].flush();

			readResponse(i);
		}

		// let all machines start computing
		for (int i = 0; i < machines; i++) {
			NetworkUtil.writeInt(os[i], Command.COMPUTE.getValue());
			os[i].flush();
		}
	}

	private void readResponse(int machineId) throws IOException, BadResponseException{
		int ret = NetworkUtil.readInt(is[machineId]);
		if (ret == -1) {
			throw new BadResponseException("Why is ret -1?");
		}
		if (ret != Response.SUCCESS.getValue()) {
			throw new BadResponseException("Slave listen failed");
		}
	}

	public static void main(String args[]) throws IOException, BadResponseException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, IncorrectOtUsageException {
		int machines = Integer.parseInt(args[4]);
		String machineConfig = args[6];
		IPManager ipManager = IPManager.loadIPs(machines, machineConfig);
		Master master = new Master(machines);
		Master.START_PORT = Integer.parseInt(args[0]);
		int peerPort = Integer.parseInt(args[1]);
		int masterMasterConnectionPort = Integer.parseInt(args[2]);
		master.isGen = Boolean.parseBoolean(args[3]);
		int inputLength = Integer.parseInt(args[5]);
		Party party = master.isGen ? Party.Alice : Party.Bob;
		Mode mode = Mode.REAL;
		InputStream is;
		OutputStream os;
		if (master.isGen) {
			Server server = new Server();
			server.listen(masterMasterConnectionPort);
			is = server.is;
			os = server.os;
		} else {
			Client client = new Client();
			client.connect(ipManager.masterGarblerIp, masterMasterConnectionPort);
			is = client.is;
			os = client.os;
		}
		// System.out.println("connected to other master");
		CompEnv<GCSignal> env = CompEnv.getEnv(mode, party, is, os);
		String experiment = args[6];
		Class c = Class.forName("test.parallel." + experiment);
		// machine.parallelGadget = new Histogram();
		master.parallelGadget = (ParallelGadget) c.newInstance();

		for (int i = 0; i < machines; i++) {
			master.listen(Master.START_PORT + i, i);
		}
		// System.out.println("Connected to master");
		// master tells the machines what their ports are for peer connections
		master.setUp(peerPort, inputLength, env);
		// System.out.println("Connections successful");
	}
}