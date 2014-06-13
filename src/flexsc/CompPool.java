package flexsc;
import gc.GCGen;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import test.Utils;
import network.Client;
import network.Server;

public class CompPool<T> {
	final static int MaxNumberTask = 8;

	CompEnv<T>[] envs = new CompEnv[MaxNumberTask];
	Server[] servers = new Server[MaxNumberTask];
	Client[] clients = new Client[MaxNumberTask];
	ExecutorService executorService;
	public CompPool(CompEnv<T> env, String host, int port) throws Exception{
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
			Gadget<T> gadge = g.getGadget();
			gadge.env = envs[i];
			gadge.inputs = (Object[]) inputArray[i];

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