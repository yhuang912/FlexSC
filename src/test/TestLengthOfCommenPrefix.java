package test;

import java.util.Random;

import gc.CompEnv;
import gc.IntegerLib;
import gc.Signal;
import org.junit.Test;


public class TestLengthOfCommenPrefix extends Test_2Input1Output{
	
	public int commonPrefix(int l1, int l2) {
		int res = 0;
		int diff = l1 ^ l2;
		if( (diff & 0xFFFF0000) == 0) {
			res += 16;
			diff <<= 16;
		}
		if( (diff & 0xFF000000) == 0) {
			res +=8;
			diff <<= 8;
		}
		if((diff & 0xF0000000) == 0) {
			res += 4;
			diff <<=4;
		}
		if((diff & 0xC0000000) == 0) {
			res += 2;
			diff <<=2;
		}
		if((diff & 0x80000000) == 0) {
			res +=1;
			diff <<=1;
		}
		if(diff == 0) {
			res +=1;
		}
		return res;
	}
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1000;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1<<30), rnd.nextInt(1<<30)) {
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).lengthOfCommenPrefix(Signala ,Signalb);}

				int plainCompute(int x, int y) {
					return commonPrefix(x, y);}
			});
		}		
	}
}