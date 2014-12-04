package compiledlib.stack;

import compiledlib.stack.TestStack.EvaRunnable;

import flexsc.Flag;

public class TestStackClient {

	public  static void main(String args[]) throws Exception {
			EvaRunnable gen = new EvaRunnable(20);
			gen.run();
	}
}