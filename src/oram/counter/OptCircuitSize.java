package oram.counter;

import pm.PMCompEnv.Statistics;

public class OptCircuitSize {

	public OptCircuitSize() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		args = new String[5];
		int dataSize = 100*100*10;
		for(int i = 6; i < 24; ++i)
			{
//				CountCircuitORAM  c1 = new CountCircuitORAM(log, 3, dataSize, 80);
				CountCircuitORAMRecursion c1 = new CountCircuitORAMRecursion(i, 3, dataSize, 80, 64, 8);
				c1.count();c1.statistic.finalize();
				Statistics s1 = c1.statistic.newInstance();
				System.out.println(i+"\t"+s1.andGate+"\t"+((i*i+i)/4*(dataSize*2+i)));
			}
	}
}
