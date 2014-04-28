package cv.test.ints;

import java.util.Random;
import org.junit.Test;
import test.Utils;
import cv.CVCompEnv;
import cv.test.harness.Test_2Input1Output;
import circuits.IntegerLib;


public class TestLeq extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runTest(new Helper(rnd.nextInt()%(1<<30), rnd.nextInt()%(1<<30)) {


				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[]{(x<=y)});}

				@Override
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CVCompEnv e) throws Exception {
					return new Boolean[]{new IntegerLib<Boolean>(e).leq(Signala ,Signalb)}; }
			});
		}
		
		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt(1<<30);
			runTest(new Helper(a, a) {
				public Boolean[] secureCompute(Boolean[] Signala, Boolean[] Signalb, CVCompEnv e) throws Exception {
					return new Boolean[]{new IntegerLib<Boolean>(e).leq(Signala ,Signalb)}; }

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[]{(x<=y)});}
			});
		}
	}
}