package gc.halfANDs;

import flexsc.CompEnv;
import flexsc.Flag;
import gc.GCGenComp;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public class GCGen extends GCGenComp {
	Garbler gb;

	public long ands = 0;
	public GCGen(InputStream is, OutputStream os){
		super(is, os);
		gb = new Garbler();
		labelR[0] = GCSignal.freshLabel(CompEnv.rnd);
		labelR[1] = GCSignal.freshLabel(CompEnv.rnd);
		labelL[0] = GCSignal.freshLabel(CompEnv.rnd);
		labelL[1] = GCSignal.freshLabel(CompEnv.rnd);
		
	}

	private GCSignal labelL[] = new GCSignal[2];
	private GCSignal labelR[] = new GCSignal[2];

	private GCSignal TG = GCSignal.freshLabel(CompEnv.rnd);
	private GCSignal WG = GCSignal.freshLabel(CompEnv.rnd);
	private GCSignal TE = GCSignal.freshLabel(CompEnv.rnd);
	private GCSignal WE = GCSignal.freshLabel(CompEnv.rnd);
//	, WG, TE, WE;
	
	private GCSignal garble(GCSignal a, GCSignal b) {
		labelL[0] = a;
		GCSignal.xor(R, labelL[0], labelL[1]);
//		 = R.xor();
		labelR[0] = b;
		GCSignal.xor(R, labelR[0], labelR[1]);
//		labelR[1] = R.xor(labelR[0]);

		int cL = a.getLSB() ? 1 : 0;
		int cR = b.getLSB() ? 1 : 0;

		// first half gate
		GCSignal G1 = gb.hash(labelL[0], gid, false);
		GCSignal lblH = gb.hash(labelL[1], gid, false);
		GCSignal.xor(lblH, G1, TG);
		GCSignal.xor(TG, ((cR == 1) ? R : GCSignal.ZERO), TG);
//		TG = xor;
		GCSignal.xor(G1, ((cL == 1) ? TG : GCSignal.ZERO), WG);
//		WG = G1.xor;
		
		// second half gate
		G1 = gb.hash(labelR[0], gid, true);
		GCSignal.xor(G1,gb.hash(labelR[1], gid, true), TE );
		GCSignal.xor(TE, labelL[0], TE);
//		TE = G1.xor().xor();
		GCSignal.xor(((cR == 1) ? (TE.xor(labelL[0])) : GCSignal.ZERO), G1, WE);
//		WE = G1.xor;
		
		// send the encrypted gate
		try {
			Flag.sw.startGCIO();
			TG.send(os);
			TE.send(os);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// combine halves
		return WG.xor(WE);
	}
	
	public GCSignal and(GCSignal a, GCSignal b) {
		++ands;
		Flag.sw.ands++;
		Flag.sw.startGC();
		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res = ( (a.v && b.v)? _ONE:_ZERO);
		else if (a.isPublic())
			res = a.v ? b : _ZERO;
		else if (b.isPublic())
			res = b.v ? a : _ZERO;
		else {
			GCSignal ret = garble(a, b);
			gid++;
			res = ret;
			gatesRemain = true;
		}
		Flag.sw.stopGC();
		return res;
	}
}