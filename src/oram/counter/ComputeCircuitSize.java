package oram.counter;

import java.util.Arrays;
import pm.PMCompEnv.Statistics;

public class ComputeCircuitSize {

	public ComputeCircuitSize() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		args = new String[5];
		args[0] = "20";
		args[1] = "1024";
		args[2] = "4";
		args[3] = "1";

		System.out.println(args[0]+" "+args[1]+" "+args[2]+" "+args[3]);
		int base = new Integer(args[0]);
		int cutoff = new Integer(args[1]);
		int recur = new Integer(args[2]);
		int num = new Integer(args[3]);

		int[] logs = new int[num];
		long[][] y1 = new long[5][num];
		long[][] y2 = new long[5][num];
		long[][] y3 = new long[5][num];
		long[][] y4 = new long[5][num];
		long[][] y5 = new long[5][num];
		long[][] y6 = new long[5][num];
		long[][] y7 = new long[5][num];
		int dataSize = 32;

		for(int log = base; log < base+num; log+=1) {


			CountCSCORAMRecursion  c1 = new CountCSCORAMRecursion(log, 6, dataSize, 80, cutoff, recur, 12345);
//			CountNewORAM c1 = new CountNewORAM(log, 6, dataSize, 27);
			c1.count();c1.statistic.finalize();
			Statistics s1 = c1.statistic.newInstance();
			c1=null;System.gc();


			CountSwapORAMRecursion c2 = new CountSwapORAMRecursion(log, 4, dataSize, 80, cutoff, recur);
//			CountKaiminORAM c2 = new CountKaiminORAM(log, 6, dataSize, 60);
			c2.count();c2.statistic.finalize();
			Statistics s2 = c2.statistic.newInstance();
			c2=null;System.gc();


			CountPathORAMRecursion c3 = new CountPathORAMRecursion(log, 4, dataSize, 80, cutoff, recur);
			c3.count();
			//			CountPathORAM c3 = new CountPathORAM(log, 4, dataSize, 80);
			c3.statistic.finalize();
			Statistics s3 = c3.statistic.newInstance();
			c3=null;System.gc();


			CountPathORAMNaiveRecursion c4 = new CountPathORAMNaiveRecursion(log, 4, dataSize, 80, cutoff, recur);
			//			CountPathORAMNaive c4 = new CountPathORAMNaive(log, 4, dataSize, 80);
			c4.count();c4.statistic.finalize();
			Statistics s4 = c4.statistic.newInstance();
			c4=null;System.gc();

			
//			CountTreeORAMRecursion c5 = new CountTreeORAMRecursion(log, 120, dataSize, 80, cutoff, recur);
//			CountTreeORAM c5 = new CountTreeORAM(log, dataSize, 80);

			CountKaiminORAMRecursion c5 = new CountKaiminORAMRecursion(log, 4, dataSize, 80, cutoff, recur);
			c5.count();c5.statistic.finalize();
			Statistics s5 = c5.statistic.newInstance();
			c5=null;System.gc();


			logs[log-base] = log;			


			y1[0][log-base] = (int) (s1.OTs);
			y2[0][log-base] = (int) (s2.OTs);
			y3[0][log-base] = (int) (s3.OTs);
			y4[0][log-base] = (int) (s4.OTs);
//						y7[0][log-base] = (int) (s7.OTs);
//			y6[0][log-base] = (int) trivial(log,  dataSize)[3];
			y5[0][log-base] = (int) (s5.OTs);


			y1[1][log-base] = (int) (s1.NumEncAlice);
			y2[1][log-base] = (int) (s2.NumEncAlice);
			y3[1][log-base] = (int) (s3.NumEncAlice);
			y4[1][log-base] = (int) (s4.NumEncAlice);
//									y7[1][log-base] = (int) (s7.NumEncAlice);
//			y6[1][log-base] = (int) trivial(log,  dataSize)[2];
			y5[1][log-base] = (int) (s5.NumEncAlice);


			y1[2][log-base] = (int) (s1.andGate);
			y2[2][log-base] = (int) (s2.andGate);
			y3[2][log-base] = (int) (s3.andGate);
			y4[2][log-base] = (int) (s4.andGate);
//									y7[2][log-base] = (int) (s7.andGate);
//			y6[2][log-base] = (int) (trivial(log,  dataSize)[0]);
			y5[2][log-base] = (int) (s5.andGate);


			y1[3][log-base] = (int) ((s1.andGate+s1.xorGate));
			y2[3][log-base] = (int) ((s2.andGate+s2.xorGate));
			y3[3][log-base] = (int) ((s3.andGate+s3.xorGate));
			y4[3][log-base] = (int) ((s4.andGate+s4.xorGate));
//									y7[3][log-base] = (int) ((s7.andGate+s7.xorGate));
			y5[3][log-base] = (int) ((s5.andGate+s5.xorGate));
//			y6[3][log-base] = (int) (trivial(log,  dataSize)[1]);


			y1[4][log-base] = (int) (s1.bandwidth);
			y2[4][log-base] = (int) (s2.bandwidth);
			y3[4][log-base] = (int) (s3.bandwidth);
			y4[4][log-base] = (int) (s4.bandwidth);
//			//						y7[4][log-base] = (int) (s7.bandwidth);
			y5[3][log-base] = (int) ((s5.andGate+s5.xorGate));
//			y6[4][log-base] = (int) (trivial(log,  dataSize)[1]);


			System.out.println(log);
		}
		for(int j = 0; j < 5; ++j){
			if( j == 0 || j == 2 || j == 3){
			System.out.print("hold on;\nx="+Arrays.toString(logs)+"\n");
			System.out.print("our SCORAM="+Arrays.toString(y1[j])+"\n");
			System.out.print("Swap="+Arrays.toString(y2[j])+"\n");
			System.out.print("PathSC="+Arrays.toString(y3[j])+"\n");
			System.out.print("Path="+Arrays.toString(y4[j])+"\n");
			System.out.print("CLP="+Arrays.toString(y5[j])+"\n");
			System.out.print("Trivial="+Arrays.toString(y6[j])+"\nEND");
			}
		}
	}



	static long[] trivial(int logN, int dataSize) {
		// TODO Auto-generated method stub
		long tg = (1<<logN)*(logN*2 + dataSize*3);
		long fg = (1<<logN)*(logN + dataSize*2);
		long enc = (tg-fg)*4 + (1<<logN)*dataSize*2;
		long ot = (1<<logN)*dataSize;
		return new long[]{tg-fg, tg, enc, ot};
	}
}
