package flexsc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sun.net.www.content.image.gif;
import flexsc.Test_2Input1Output.AddGadget;
import network.Client;
import network.Server;


public abstract class Gadget<T> implements secureComputable<T>, Callable<Object>{
	int port;
	String host;
	Object[] inputs;
	CompEnv<T>env;
	protected Gadget(CompEnv<T>e , String host, int port, Object[] input) {
		this.port = port;
		this.host = host;
		this.inputs = input;
		this.env = e;
	}
	
	public Gadget(CompEnv<T>e , String host, int port) {
		this.port = port;
		this.host = host;
		this.env = e;
	}

//	public  Object secureCompute(CompEnv<T> e, Object[] o) throws Exception;

	@Override
	public Object call() throws Exception {
		Object res = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Server server = null;
		Client client  = null;
		try {
			if(env.getParty() == Party.Alice){
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

			CompEnv<T> newEnv = env.getNewInstance(inputStream, outputStream);
			
			res = secureCompute(newEnv, inputs);
			outputStream.flush();

			if (Party.Alice == env.getParty()) {
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
	
	protected abstract Gadget<T> getGadget(CompEnv<T>e , String host, int port, Object[] inputs2);
	
	public <G extends Gadget<T>> Object[] runGadget(G g, Object[] inputs, CompEnv<T> env) throws InterruptedException, ExecutionException{
		int threads = inputs.length;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		ArrayList<Future<Object> > list = new ArrayList<Future<Object>>();
		
		for(int i = 0; i < threads; ++i) {
			Gadget<T> gadge = g.getGadget(env, "localhost", 54311+i, (Object[]) inputs[i]);
			Future<Object> future = executorService.submit(gadge);
			list.add(future);
		}
		
		Object[] result = new Object[threads];
		int cnt = 0;
		for(Future<Object> future: list) {
			result[cnt++] = future.get();
		}
		executorService.shutdown();
		return result;

	}

}