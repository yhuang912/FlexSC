package test.ods;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;
import ods.ostack.OStack;

import org.junit.Test;

import test.Utils;
import circuits.IntegerLib;

public class TestOStack {

	public GCSignal[] compute(OStack<GCSignal> ostack, IntegerLib<GCSignal> lib) throws Exception{
//		ostack.push(lib.toSignals(111));
//		ostack.push(lib.toSignals(222));
//		ostack.push(lib.toSignals(333));
//		GCSignal[] res = ostack.pop();
//		res = ostack.pop();
//		res = ostack.pop();
		ostack.access(lib.SIGNAL_ZERO, lib.toSignals(111));
		ostack.access(lib.SIGNAL_ONE, lib.toSignals(444));
		ostack.access(lib.SIGNAL_ZERO, lib.toSignals(222));
		ostack.access(lib.SIGNAL_ZERO, lib.toSignals(333));
		

//		ostack.access(lib.SIGNAL_ONE, lib.toSignals(444));
		return ostack.access(lib.SIGNAL_ONE, lib.toSignals(444));
	}
	
	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		GenRunnable () {
		}

		public void run() {
			try {
				listen(54321);
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Alice, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				OStack<GCSignal> ostack = new OStack<>(env, 1<<20, 32);
				GCSignal[] res = compute(ostack, lib);
				int resInt = Utils.toInt(env.outputToAlice(res));
				System.out.print(resInt + "\n" +Flag.sw.ands/5);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Bob, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				OStack<GCSignal> ostack = new OStack<>(env, 1<<20, 32);
				GCSignal[] res = compute(ostack, lib);
				env.outputToAlice(res);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
	}
}
