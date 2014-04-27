package cv.test.harness;

import objects.Float.Representation;
import org.junit.Assert;
import cv.CVCompEnv;


public class TestFloat {
	public abstract class Helper {
		double a,b;
		public Helper(double a, double b) {
			this.b = b;
			this.a = a;
		}
		public abstract Representation<Boolean> secureCompute(Representation<Boolean> a, Representation<Boolean> b, CVCompEnv env) throws Exception;
		public abstract double plainCompute(double a, double b);
	}
	
	public void runTest(Helper h) throws Exception {	
		CVCompEnv e = new CVCompEnv();
		Representation<Boolean> fgc1 = e.fromDouble(h.a, 23, 9);
		Representation<Boolean> fgc2 = e.fromDouble(h.b, 23, 9);

		Representation<Boolean> re = h.secureCompute(fgc1, fgc2, e);
		
		if(Math.abs(h.plainCompute(h.a, h.b) - e.toDouble(re))>3E-4)
			System.out.print(e.toDouble(re)+" "+h.plainCompute(h.a, h.b)+" "+h.a+" "+h.b+"\n");
		Assert.assertTrue(Math.abs(h.plainCompute(h.a, h.b)-e.toDouble(re))<=3E-6);
	}
}