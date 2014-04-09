package oramgc.pathoram;

import java.util.Random;
import flexsc.CompEnv;
import gc.Signal;
import org.junit.Test;
import test.harness.Test_2Input1Output;


public class TestDeepestLevel extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt()%(1<<30), rnd.nextInt()%(1<<30)) {
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new PathOramLib(0, 0, 0, 0, e).deepestLevel(Signala ,Signalb);
					}

				public int plainCompute(int x, int y) {
					int res = 0;
					for(int i = 31; i >= 0 ; --i){
						if(ithBit(x, i) == ithBit(y, i)){
							res++;
						}
						else return res;
					}
					return res;
				}
				public int ithBit(int a, int i){
					return (a >> i) & 0x1;
				}
			});
		}		
	}
}