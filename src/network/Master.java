package network;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCGen;
import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import test.Utils;

public class Master {
	public static int MACHINES;// = 4;
	public static int LOG_MACHINES;// = Machine.log2(MACHINES);
	public static int START_PORT;
	public static int INPUT_LENGTH = 1024;

	private ServerSocket[] serverSocket;
	public InputStream[] is;
	public OutputStream[] os;
	private boolean isGen;

	public Master() {
		serverSocket = new ServerSocket[MACHINES];
		is = new InputStream[MACHINES];
		os = new OutputStream[MACHINES];
	}

	public void listen(int port, int index) throws IOException {
		Socket clientSock;
        serverSocket[index] = new ServerSocket(port);            // create socket and bind to port
        clientSock = serverSocket[index].accept();                   // wait for client to connect
        
        os[index] = new BufferedOutputStream(clientSock.getOutputStream(), Constants.BUFFER_SIZE);  
        is[index] = new BufferedInputStream(clientSock.getInputStream(), Constants.BUFFER_SIZE);
	}

	public void disconnect() throws IOException {
		for (int i = 0; i < MACHINES; i++) {
			serverSocket[i].close();
		}
	}

	public void func() throws IOException {
		int length = 32;
		GCSignal[][] a = new GCSignal[MACHINES][length];
		for (int k = 0; k < LOG_MACHINES; k++) {
			for (int j = 0; j < MACHINES; j++) {
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

	public void setUp(int peerPort, Object[] input) throws IOException, BadResponseException {
		// set machineId for each of the machines
		//GCSignal[][][] gcInput1 = (GCSignal[][][]) input;
		for (int i = 0; i < MACHINES; i++) {
			NetworkUtil.writeInt(os[i], Command.SET_MACHINE_ID.getValue());
			NetworkUtil.writeInt(os[i], i);
			NetworkUtil.writeInt(os[i], peerPort);
			NetworkUtil.writeInt(os[i], MACHINES);
			if (isGen) {
				GCGen.R.send(os[i]);
			}
			GCSignal[][] gcInput = (GCSignal[][]) input[i];
			// GCSignal[][] gcInput = gcInput1[i];
			NetworkUtil.writeInt(os[i], gcInput.length);
			NetworkUtil.writeInt(os[i], gcInput[0].length);
			for (int j = 0; j < gcInput.length; j++)
				for (int k = 0; k < gcInput[j].length; k++)
					gcInput[j][k].send(os[i]);
		    /*ObjectOutputStream oos = new ObjectOutputStream(os[i]);
		    oos.writeObject(input[i]);
		    GCSignal[][] signal = (GCSignal[][]) input[i];
		    System.out.println("first " + signal[0][0].toHexStr());*/
			os[i].flush();
			// oos.flush();
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
		}

		// let all machines start computing
		for (int i = 0; i < MACHINES; i++) {
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

	public static void main(String args[]) throws IOException, BadResponseException, InterruptedException, ClassNotFoundException {
		Master.MACHINES = Integer.parseInt(args[4]);
		Master.LOG_MACHINES = Machine.log2(Master.MACHINES);
		// System.out.println("Machines " + Master.MACHINES);
		// System.out.println("Log Machines " + Master.LOG_MACHINES);
		Master master = new Master();
		Master.START_PORT = Integer.parseInt(args[0]);
		int peerPort = Integer.parseInt(args[1]);
		int masterMasterConnectionPort = Integer.parseInt(args[2]);
		master.isGen = Boolean.parseBoolean(args[3]);
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
			client.connect(Constants.LOCALHOST, masterMasterConnectionPort);
			is = client.is;
			os = client.os;
		}
		System.out.println("connected to other master");
		CompEnv<GCSignal> env = CompEnv.getEnv(mode, party, is, os);
		GCSignal[][] Ta = env.newTArray(INPUT_LENGTH /* number of entries in the input */, 0);
		if (master.isGen) {
			for(int i = 0; i < Ta.length; ++i)
				Ta[i] = env.inputOfBob(new boolean[32]);
		} else {
			boolean[][] a = getInput();
			for(int i = 0; i < Ta.length; ++i)
				Ta[i] = env.inputOfBob(a[i]);
		}
		Object[] input = new Object[Master.MACHINES];

		for(int i = 0; i < Master.MACHINES; ++i)
			input[i] = Arrays.copyOfRange(Ta, i * Ta.length / Master.MACHINES, (i + 1) * Ta.length / Master.MACHINES);

		System.out.println("OT done");

		for (int i = 0; i < MACHINES; i++) {
			master.listen(Master.START_PORT + i, i);
		}
		System.out.println("Connected to master");
		// master tells the machines what their ports are for peer connections
		master.setUp(peerPort, input);
		System.out.println("Connections successful");
	}

	private static boolean[][] getInput() {
		int[] aa = new int[INPUT_LENGTH];
		boolean[][] a = new boolean[aa.length][];
		int limit = 20;
		Random rn = new Random();
		int[] freq = new int[limit + 1];
		for (int i = 0; i < limit + 1; i++)
			freq[i] = 0;
		for (int i = 0; i < a.length; ++i) {
			aa[i] = rn.nextInt(limit);
			freq[aa[i]]++;
		}
		for(int i = 0; i < aa.length; ++i)
			a[i] = Utils.fromInt(aa[i], 32);
		System.out.println("Frequencies");
		for (int i = 0; i < limit + 1; i++)
			System.out.println(i + ": " + freq[i]);
		return a;
	}
}