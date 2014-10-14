package test.harness;

import flexsc.Mode;

public class TestHarness<T> {
	public int testCases = 1000;
//	public Mode m = Mode.REAL;
	public Mode m = Mode.OPT;
//	public Mode m = Mode.VERIFY;
//	 public Mode m = Mode.COUNT;
	public TestHarness() {
		if (m == Mode.COUNT) {
			testCases = 1;
		}
	}
}