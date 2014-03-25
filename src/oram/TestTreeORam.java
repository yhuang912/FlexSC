package oram;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.BitSet;
import java.util.Random;

import org.junit.Test;


public class TestTreeORam {
	int N = (int) Math.pow(2,  12); //(int) Math.pow(2, 4);
	int B = 32;
	
	static SecureRandom rnd;
	
	static {
		try {
			rnd = SecureRandom.getInstance("SHA1PRNG");
			rnd.setSeed(new byte[]{1,2,3,4});
		} catch (Exception e) {
			
		}
	}

	int iter = 2000;
	
	@Test
	public void testORAMReads() throws Exception {
		
		TreeORam oram = new TreeORam(rnd);
		
		// generate data
		BitSet[] data = new BitSet[N];
		Random random = new Random();
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			random.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
				
		oram.initialize(data, B);

		for (int i = 0; i < iter; i++) {
			TreeORam.Tree.Block b = oram.read(i % N);
			assertEquals(b.data, data[i % N]);
		}
	}
	
	@Test
	public void testORAMWrites() throws Exception {
		TreeORam oram = new TreeORam(rnd);
		
		BitSet[] data = new BitSet[N];
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
		oram.initialize(data, B);

		for (int i = 0; i < iter; i++) {
			int k = rnd.nextInt(N);
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			data[k] = BitSet.valueOf(temp);

			oram.write(k, data[k]);
			TreeORam.Tree.Block b = oram.read(k);

			assertEquals(b.data, data[k]);
		}
	}
	
	@Test
	public void testORAMReadsAndWrites() throws Exception {
		TreeORam oram = new TreeORam(rnd);
		
		BitSet[] data = new BitSet[N];
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
		oram.initialize(data, B);
		
		for (int i = 0; i < iter; i++) {
			int k1 = rnd.nextInt(N);
			TreeORam.Tree.Block b = oram.read(k1);
			b.data.flip(1);

			oram.write(k1, b.data);

			data[k1].flip(1);
			
			int k2 = rnd.nextInt(N);
			b = oram.read(k2);
			assertEquals(b.data, data[k2]);
		}
	}
	
	@Test
	public void testAllOnVariousN() throws Exception {
		for (int i = 2; i < 1500; i+=395) {
			N = i;
			testORAMReads();
			testORAMWrites();
			testORAMReadsAndWrites();
		}
	}
}