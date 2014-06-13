package flexsc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import network.Client;
import network.Server;


public abstract class Gadget<T> implements Callable<Object>{
	Party party;
	int port;
	String host;
	Object[] inputs;
	CompEnv<T>env;
	public Gadget(CompEnv<T>e , String host, int port, Object[] input, Party p) {
		this.port = port;
		this.host = host;
		this.inputs = input;
		this.env = e;
		this.party = p;
	}

	public abstract Object secureCompute(CompEnv<T> e, Object[] o) throws Exception;

	@Override
	public Object call() throws Exception {
		Object res = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Server server = null;
		Client client  = null;
		try {
			if(party == Party.Alice){
				server = new Server();
				server.listen(port);
				inputStream = server.is;
				outputStream = server.os;
			}
			else{
				client = new Client();
				client.connect(host, port);
				inputStream = client.is;
				outputStream = client.os;
			}

			CompEnv<T> newEnv = env.getNewInstance(inputStream, outputStream, party);
			res = secureCompute(newEnv, inputs);
			outputStream.flush();

			if (Party.Alice == party) {
				server.disconnect();
			} else {
				client.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return res;
	}

}