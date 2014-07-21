package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Machine {
	public InputStream masterIs;
	public OutputStream masterOs;
	private Socket masterSocket;

	public Machine() {
	}

	public void connectToMaster(String server, int port) throws Exception {
		while(true){
			try{
				masterSocket = new Socket(server, port);          // create socket and connect
				if(masterSocket != null)
					break;
			}
			catch(IOException e){
				Thread.sleep(100);
			}
		}

		masterOs = new BufferedOutputStream(masterSocket.getOutputStream(), Master.BUFFER_SIZE);  
		masterIs = new BufferedInputStream(masterSocket.getInputStream(), Master.BUFFER_SIZE);
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
}
