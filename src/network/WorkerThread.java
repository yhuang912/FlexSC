package network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class WorkerThread implements Callable<Socket> {
	ServerSocket serverSock;

	WorkerThread(ServerSocket serverSock) {
		this.serverSock = serverSock;
	}

	@Override
	public Socket call() throws Exception {
		Socket sock = null;
		try {
			sock = serverSock.accept();
		} catch (Exception e) {
			System.out.println("Server socket accept fail");
		}
		return sock;
	}
}
