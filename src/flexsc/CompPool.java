package flexsc;

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
import test.parallel.AddGadget;

public class CompPool<T> {
	public final static int MaxNumberTask = Master.MACHINES;

	CompEnv<T>[] envs;
	Server[] servers;
	Client[] clients;
	ExecutorService executorService;
	int masterPort;

	@SuppressWarnings("unchecked")
	public CompPool(CompEnv<T> env, String host, int port, int masterPort) throws Exception{
		envs = new CompEnv[MaxNumberTask];
		servers = new Server[MaxNumberTask];
		clients = new Client[MaxNumberTask];

	     executorService = Executors.newFixedThreadPool(MaxNumberTask);

		for(int i = 0; i < MaxNumberTask; ++i) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if(env.getParty() == Party.Alice){
				servers[i] = new Server();
				servers[i].listen(port+i);
				inputStream = servers[i].is;
				outputStream = servers[i].os;
			}
			else{
				clients[i] = new Client();
				clients[i].connect(host, port+i);
				inputStream = clients[i].is;
				outputStream = clients[i].os;
			}
			envs[i]= env.getNewInstance(inputStream, outputStream);
		}
		this.masterPort = masterPort;
	}

	public void finalize() throws Exception{
		for(int i = 0; i < MaxNumberTask; ++i){
			if(servers[i] != null)
				servers[i].disconnect();
			if(clients[i] != null)
				clients[i].disconnect();
		}
		executorService.shutdown();
	}

	public <G extends Gadget<T>> Object[] runGadget(G g, Object[] inputArray) throws InterruptedException, ExecutionException{
		ArrayList<Future<Object> > list = new ArrayList<Future<Object>>();
		for(int i = 0; i < inputArray.length; ++i) {
			Gadget<T> gadge = new AddGadget();
			gadge.env = envs[i];
			gadge.inputs = (Object[]) inputArray[i];
			gadge.port = masterPort + i;
			Future<Object> future = executorService.submit(gadge);
			list.add(future);
		}

		Object[] result = new Object[inputArray.length];
		int cnt = 0;
		for(Future<Object> future: list) {
			result[cnt++] = future.get();
		}
		return result;
	}

}	