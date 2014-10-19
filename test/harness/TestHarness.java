package harness;

import flexsc.Mode;

public class TestHarness {
	static public int testCases;
	static public Mode m;
	public TestHarness() {
		m = Mode.REAL;
		if (m == Mode.COUNT) {
			testCases = 1;
		}
		else if (m == Mode.REAL || m == Mode.OPT) {
			testCases = 10;
		}
		else if (m == Mode.VERIFY)
			testCases = 1000;
	}
}