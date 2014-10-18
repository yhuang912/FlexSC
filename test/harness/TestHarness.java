package harness;

import flexsc.Mode;

public class TestHarness {
	public int testCases = 10;
	static public Mode m;
	public TestHarness() {
		m = Mode.REAL;
		if (m == Mode.COUNT) {
			testCases = 1;
		}
	}
}