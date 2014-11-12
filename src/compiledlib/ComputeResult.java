package compiledlib;

import compiledlib.stack.TestStack;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {

	static public void main(String args[]) throws InterruptedException {
		for (int logN = 10; logN <= 30; logN+=2) {
			 TestStack t1 = new TestStack();
			// TestCUMStack t2 = new TestCUMStack();
//			TestPriorityQueue t2 = new TestPriorityQueue();
//			TestAVL t2 = new TestAVL();
//			TestORAMAVL t1 = new TestORAMAVL(logN, 32);
			// Statistics stack = t1.getCount(logN);
//			Statistics cstack = t2.getCount(logN);
			Statistics oram = t1.getCount(logN);
//			 System.out.println(logN+"\t"+stack.andGate+"\t"+cstack.andGate+"\t"+stack.NumEncAlice+"\t"+cstack.NumEncAlice);
			System.out.println(logN + "\t" + oram.andGate);
//					+ "\t" + oram.andGate);
		}
	}
}