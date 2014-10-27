package compiledlib;

import compiledlib.priority_queue.TestPriorityQueue;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {

	static public void main(String args[]) throws InterruptedException {
		for (int logN = 10; logN <= 30; logN+=2) {
			// TestStack t1 = new TestStack();
			// TestCUMStack t2 = new TestCUMStack();
			TestPriorityQueue t2 = new TestPriorityQueue();
			// Statistics stack = t1.getCount(logN);
			Statistics cstack = t2.getCount(logN);
			// System.out.println(logN+"\t"+stack.andGate+"\t"+cstack.andGate+"\t"+stack.NumEncAlice+"\t"+cstack.NumEncAlice);
			System.out.println(logN + "\t" + "\t" + cstack.andGate + "\t"
					+ "\t" + cstack.NumEncAlice);
		}
	}
}