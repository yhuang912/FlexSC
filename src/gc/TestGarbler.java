package gc;

import java.security.SecureRandom;

import org.junit.Test;

public class TestGarbler {
	SecureRandom rnd = new SecureRandom();
	GCSignal a = GCSignal.freshLabel(rnd);
	GCSignal b = GCSignal.freshLabel(rnd);
	GCSignal m = GCSignal.freshLabel(rnd);
	Garbler gb = new Garbler();
	
	public void test() {
		gb.enc(a, b, 0, m);
		
//		Assert.assertTrue(m.equals(gb.dec(a, b, 0L, gb.enc(a, b, 0L, m))));
	}

	@Test
	public void test1000() {
		long start = System.nanoTime();
		for(int i = 0; i<10000*1000; i++)
			gb.enc(a, b, 0, m);
		long end = System.nanoTime();
		System.out.println((end - start)*1.0/1000000);
	}
}
