package harness;

import flexsc.Flag;
import flexsc.Mode;

public class TestHarness {
	static public int testCases;
	public TestHarness() {
		if (Flag.mode == Mode.COUNT) {
			testCases = 1;
		}
		else if (Flag.mode == Mode.REAL || Flag.mode == Mode.OPT || Flag.mode == Mode.OFFLINE) {
			testCases = 30;
		}
		else if (Flag.mode == Mode.VERIFY)
			testCases = 1000;
	}
}