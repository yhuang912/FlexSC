package test.DFT;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.util.Random;

import objects.Float.Representation;

import org.junit.Test;

import test.harness.TestDFTHarness;
import circuits.DFTLib;


public class TestDFT extends TestDFTHarness<GCSignal> {

@Test
public void testAllCases() throws InterruptedException {
	Random rng = new Random();
	
	double[] real = new double[LENGTH];
	double[] img = new double[LENGTH];
	for(int i = 0; i < LENGTH; ++i) {
		real[i] = rng.nextInt()%10000;
		img[i] = 0;
	}
	int testCases = 1;

	for (int i = 0; i < testCases; i++) {
		runThreads(
				new Helper(real, img, Mode.REAL) {
			@Override
			public void secureCompute(Representation<GCSignal>[] a, Representation<GCSignal>[] b,
					CompEnv<GCSignal> env) {
				new DFTLib<GCSignal>(env).FFT(a, b);
			}

			@Override
			public void plainCompute(double[] a, double[] b) {
				new DFT(LENGTH).fft(a, b);
			}
		});
	}
}
}