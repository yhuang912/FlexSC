package flexsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import network.Client;
import network.Master;
import network.Server;

public class CompPool<T> {
	CompEnv<T>[] envs;
	Server[] servers;
	Client[] clients;
	ExecutorService executorService;
	int masterPort;

	@SuppressWarnings("unchecked")
	public CompPool(CompEnv<T> env, String host, int port, int masterPort, int machines) throws IOException, InterruptedException {
		envs = new CompEnv[machines];
		servers = new Server[machines];
		clients = new Client[machines];

	    executorService = Executors.newFixedThreadPool(machines);

		for (int i = 0; i < machines; ++i) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if(env.getParty() == Party.Alice) {
				servers[i] = new Server();
				servers[i].listen(port + i);
				inputStream = servers[i].is;
				outputStream = servers[i].os;
			}
			else {
				clients[i] = new Client();
				clients[i].connect(host, port+i);
				inputStream = clients[i].is;
				outputStream = clients[i].os;
			}
			envs[i]= env.getNewInstance(inputStream, outputStream);
		}
		this.masterPort = masterPort;
	}

	public void finalize(int machines) throws IOException {
		for (int i = 0; i < machines; ++i){
			if(servers[i] != null)
				servers[i].disconnect();
			if(clients[i] != null)
				clients[i].disconnect();
		}
		executorService.shutdown();
	}

	/*public <G extends Gadget<T>> void runGadget(String gadget, Object[] inputArray) throws InterruptedException, ExecutionException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		ArrayList<Future<Object> > list = new ArrayList<Future<Object>>();
		for (int i = 0; i < inputArray.length; ++i) {
			Class c = Class.forName(gadget);
			Gadget<T> gadge = (Gadget<T>) c.newInstance();
			gadge.setInputs((Object[]) inputArray[i], envs[i], machineId);
			Future<Object> future = executorService.submit(gadge);
			list.add(future);
		}

		// TODO(kartiknayak): Verify that on multi machine model, removing this
		// doesn't affect socket connections
		for(Future<Object> future: list) {
			future.get();
		}
	}*/

}	