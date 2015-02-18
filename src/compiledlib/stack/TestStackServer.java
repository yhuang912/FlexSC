package compiledlib.stack;

import compiledlib.stack.TestStack.GenRunnable;

public class TestStackServer {

	public  static void main(String args[]) throws Exception {
			GenRunnable gen = new GenRunnable(20);
			gen.run();
	}
}