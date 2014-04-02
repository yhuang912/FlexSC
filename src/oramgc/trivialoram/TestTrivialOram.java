package oramgc.trivialoram;

import oramgc.OramParty;
import oramgc.OramParty.BlockInBinary;
import test.Utils;


public class TestTrivialOram {
	class GenRunnable extends network.Server implements Runnable {
		BlockInBinary[] b;
		GenRunnable () {
		}

		public void run() {
			try {
				listen(54321);

				TrivialOramClient client = new TrivialOramClient(is, os, 4, 10, 5);
				
				client.add(client.new BlockInBinary(Utils.fromInt(1, client.lengthOfIden), 
						Utils.fromInt(1, client.lengthOfPos),
						Utils.fromInt(1, client.lengthOfData)));

				client.add(client.new BlockInBinary(Utils.fromInt(2, client.lengthOfIden), 
						Utils.fromInt(1, client.lengthOfPos),
						Utils.fromInt(1, client.lengthOfData)));
				
				BlockInBinary r = client.pop();
				System.out.println("poped:"+Utils.toInt(r.iden));
				
				client.add(client.new BlockInBinary(Utils.fromInt(3, client.lengthOfIden), 
						Utils.fromInt(1, client.lengthOfPos),
						Utils.fromInt(1, client.lengthOfData)));
				
				 r = client.readAndRemove(Utils.fromInt(3, client.lengthOfIden));
				System.out.println("readandremove:"+Utils.toInt(r.iden));
				
				b = client.blocks;
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		BlockInBinary[] b;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				TrivialOramServer server = new TrivialOramServer(is, os, 4, 10, 5);
				System.out.flush();
				server.add();
				server.add();
				server.pop();
				server.add();
				server.readAndRemove();
				b = server.blocks;

				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();

		for(int i = 0; i < gen.b.length; ++i) {
			boolean[] tmp = xor(gen.b[i].iden, eva.b[i].iden);
			System.out.println(Utils.toInt(tmp));
		}
	}
	
	public boolean[] xor(boolean[]a, boolean[] b) {
		boolean[] res = new boolean[a.length];
		for(int i = 0; i <res.length; ++i)
			res[i] = a[i]^b[i];
		return res;
		
	}
	
	public static void main(String [ ] args) throws Exception{
		TestTrivialOram t = new TestTrivialOram();
		t.runThreads();
	}

	

}