package test;

import gc.GCEva;
import gc.GCGen;
import gc.Signal;

import org.junit.Test;

import junit.framework.Assert;

public class TestBinaryGates {
	static enum GateType {AND, XOR, NOT};
	
	class GenRunnable extends network.Server implements Runnable {
		boolean x;
		boolean z = true;
		GateType gtype;
		GenRunnable (boolean x, GateType type) {
			this.x = x;
			gtype = type;
		}
		
		public void run() {
			try {
				listen(54321);
				Signal a, b, c=null;
				GCGen gen = new GCGen(is, os);
				a = gen.inputOfGen(x);
				b = gen.inputOfEva(false);
				switch (gtype) {
					case AND:	c = gen.and(a, b);	break;
					case XOR:	c = gen.xor(a, b);	break;
					case NOT:	c = gen.not(a);		break;
					default:	break;
				}
				os.flush();
				z = gen.outputToGen(c);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		boolean y;
		GateType gtype;
		EvaRunnable (boolean y, GateType type) {
			this.y = y;
			gtype = type;
		}
		
		public void run() {
			try {
				connect("localhost", 54321);				
				Signal a, b, c=null;
				GCEva eva = new GCEva(is, os);
				a = eva.inputOfGen(false);
				b = eva.inputOfEva(y);
				switch (gtype) {
					case AND:	c = eva.and(a, b);	break;
					case XOR:	c = eva.xor(a, b);	break;
					case NOT:	c = eva.not(a);		break;
					default:	break;
				}
				eva.outputToGen(c);
				os.flush();
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void test1Case(boolean x, boolean y, GateType type) throws Exception {
		GenRunnable gen = new GenRunnable(x, type);
		EvaRunnable eva = new EvaRunnable(y, type);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(1);
		tEva.start(); 
		tGen.join();

		switch (type) {
			case AND:	Assert.assertEquals(x&&y, gen.z);	break;
			case XOR:	Assert.assertEquals(x^y, gen.z);	break;
			case NOT:	Assert.assertEquals(!x, gen.z);		break;
			default:	break;
		}
		
	}

	@Test
	public void testAllCases() throws Exception {
		System.out.println("Testing AND...");
		GateType t = GateType.AND;
		test1Case(false, false, t);
		test1Case(false, true, t);
		test1Case(true, false, t);
		test1Case(true, true, t);
		
		System.out.println("Testing XOR...");
		t = GateType.XOR;
		test1Case(false, false, t);
		test1Case(false, true, t);
		test1Case(true, false, t);
		test1Case(true, true, t);
		
		System.out.println("Testing NOT...");
		t = GateType.NOT;
		test1Case(false, false, t);
		test1Case(true, false, t);

	}
}