package cv.test.ints;

import java.util.Random;
import org.junit.Test;
import cv.CVCompEnv;
import cv.test.harness.Test_2Input1Output;
import circuits.IntegerLib;


public class TestAdd extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runTest(new Helper(rnd.nextInt()%(1<<30), rnd.nextInt()%(1<<30)) {
				public Boolean[] secureCompute(Boolean[] Signala, Boolean[] Signalb, CVCompEnv e) throws Exception {
					return new IntegerLib<Boolean>(e).add(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x+y;}
			});
			
			System.out.println(i);
		}		
	}
}