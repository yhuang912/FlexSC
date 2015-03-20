package gc.regular;

import flexsc.CompEnv;
import flexsc.Flag;
import gc.GCGenComp;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public class GCGen extends GCGenComp {
	Garbler gb;

	public GCGen(InputStream is, OutputStream os) {
		super(is, os);
		gb = new Garbler();
		labelR[0] = GCSignal.freshLabel(CompEnv.rnd);
		labelR[1] = GCSignal.freshLabel(CompEnv.rnd);
		labelL[0] = GCSignal.freshLabel(CompEnv.rnd);
		labelL[1] = GCSignal.freshLabel(CompEnv.rnd);
		lb[0] = GCSignal.freshLabel(CompEnv.rnd);
		lb[1] = GCSignal.freshLabel(CompEnv.rnd);
		for(int i = 0; i < 2; ++i)
			for(int j = 0; j < 2; ++j) {
					gtt[i][j] = GCSignal.freshLabel(CompEnv.rnd);
					gttSend[i][j] = GCSignal.freshLabel(CompEnv.rnd);
			}
	}

	private GCSignal[][] gtt = new GCSignal[2][2];
	private GCSignal[][] gttSend = new GCSignal[2][2];
	private GCSignal labelL[] = new GCSignal[2];
	private GCSignal labelR[] = new GCSignal[2];
	GCSignal[] lb = new GCSignal[2];
	
	private GCSignal garble(GCSignal a, GCSignal b) {
		labelL[0] = a;
		GCSignal.xor(R, labelL[0], labelL[1]);
//		 = R.xor();
		labelR[0] = b;
		GCSignal.xor(R, labelR[0], labelR[1]);
//		 = R.xor();

		int cL = a.getLSB();
		int cR = b.getLSB() ;


		gb.enc(labelL[cL], labelR[cR], gid, GCSignal.ZERO, lb[cL & cR]);
		
		GCSignal.xor(R, lb[cL & cR], lb[1 - (cL & cR)]);
		

		gtt[0 ^ cL][0 ^ cR] = lb[0];
		gtt[0 ^ cL][1 ^ cR] = lb[0];
		gtt[1 ^ cL][0 ^ cR] = lb[0];
		gtt[1 ^ cL][1 ^ cR] = lb[1];

		if (cL != 0 || cR != 0)
			 gb.enc(labelL[0], labelR[0], gid,
					gtt[0 ^ cL][0 ^ cR], gttSend[0 ^ cL][0 ^ cR]);
		if (cL != 0 || cR != 1)
			gb.enc(labelL[0], labelR[1], gid,
					gtt[0 ^ cL][1 ^ cR], gttSend[0 ^ cL][1 ^ cR]);
		if (cL != 1 || cR != 0)
			gb.enc(labelL[1], labelR[0], gid,
					gtt[1 ^ cL][0 ^ cR], gttSend[1 ^ cL][0 ^ cR]);
		if (cL != 1 || cR != 1)
			gb.enc(labelL[1], labelR[1], gid,
					gtt[1 ^ cL][1 ^ cR], gttSend[1 ^ cL][1 ^ cR]);

		// assert(gb.enc(labelL[cL], labelR[cR], gid,
		// gtt[0][0]).equals(Label.ZERO)) : "Garbling problem.";
		return lb[0];
	}

	private void sendGTT() {
		try {
			Flag.sw.startGCIO();
			gttSend[0][1].send(os);
			gttSend[1][0].send(os);
			gttSend[1][1].send(os);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public GCSignal and(GCSignal a, GCSignal b) {
//if(Flag.sw.ands%10000000 == 0)System.out.println(Flag.sw.ands);
		Flag.sw.startGC();
		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res = ((a.v && b.v)?_ONE:_ZERO);
		else if (a.isPublic())
			res = a.v ? b : _ZERO;
		else if (b.isPublic())
			res = b.v ? a : _ZERO;
		else {
			++Flag.sw.ands;
			GCSignal ret;
			ret = garble(a, b);

			sendGTT();
			gid++;
			gatesRemain = true;
			res = ret;
		}
		Flag.sw.stopGC();
		return res;
	}

}
