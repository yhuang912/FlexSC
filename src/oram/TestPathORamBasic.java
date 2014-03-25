package oram;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.BitSet;

import org.junit.Test;


public class TestPathORamBasic {
	int N = (int) Math.pow(2,  2); //(int) Math.pow(2, 4);
	int B = 12;
	
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
		
		PathORamBasic oram = new PathORamBasic(rnd);
		
		// generate data
		BitSet[] data = new BitSet[N];
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
				
		BitSet[] pm = oram.initialize(data, (B+7)/8);

		for (int i = 0; i < iter; i++) {
			PathORamBasic.Tree.Block b = oram.read(pm, i % N);
			assert (b != null) : "read failed when i = " + i;
			assertEquals(b.data, data[i % N]);
		}
	}
	
	@Test
	public void testORAMWrites() throws Exception {
		System.out.println("N = " + N);
		PathORamBasic oram = new PathORamBasic(rnd);
		
		BitSet[] data = new BitSet[N];
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
		BitSet[] pm = oram.initialize(data, (B+7)/8);

		for (int i = 0; i < iter; i++) {
			int k = rnd.nextInt(N);
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[k] = BitSet.valueOf(temp);

			oram.write(pm, k, data[k]);
			PathORamBasic.Tree.Block b = oram.read(pm, k);
			assertEquals("break point: i = "+i, b.data, data[k]);
		}
	}
	
	@Test
	public void testORAMReadsAndWrites() throws Exception {
		PathORamBasic oram = new PathORamBasic(rnd);
		
		BitSet[] data = new BitSet[N];
		for (int i = 0; i < N; i++) {
			byte[] temp = new byte[(int) Math.ceil(B/8.0)];
			rnd.nextBytes(temp);
			data[i] = BitSet.valueOf(temp);
		}
		BitSet[] pm = oram.initialize(data, (B+7)/8);
		
		for (int i = 0; i < iter; i++) {
			int k1 = rnd.nextInt(N);
			PathORamBasic.Tree.Block b = oram.read(pm, k1);
			b.data.flip(1);

			oram.write(pm, k1, b.data);

			data[k1].flip(1);
			
			int k2 = rnd.nextInt(N);
			b = oram.read(pm, k2);
			assertEquals(b.data, data[k2]);
		}
	}
	
	@Test
	public void testAllOnVariousN() throws Exception {
		for (int i = 2; i < 3000; i+=395) {
			N = i;
			testORAMReads();
			testORAMWrites();
			testORAMReadsAndWrites();
		}
	}
	
//	@Test
//	public void testIBitsPrefix() {
//		assertEquals(0b011011110100, PathORamBasic.Utils.iBitsPrefix(0b011011110101, 12, 10));
//		assertEquals(0b11011110100, PathORamBasic.Utils.iBitsPrefix(0b011011110101, 11, 10));
//		assertEquals(0b01011110100, PathORamBasic.Utils.iBitsPrefix(0b001011110101, 11, 10));
//	}
}