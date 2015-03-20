package gc.offline;

import flexsc.CompEnv;
import flexsc.Flag;
import gc.GCEvaComp;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public class GCEva extends GCEvaComp {
	Garbler gb;
	GCSignal[][] gtt = new GCSignal[2][2];

	public GCEva(InputStream is, OutputStream os) {
		super(is, os);
		gb = new Garbler();
		gtt[0][0] = GCSignal.ZERO;
		gtt[0][1] = GCSignal.freshLabel(CompEnv.rnd);
		gtt[1][0] = GCSignal.freshLabel(CompEnv.rnd);
		gtt[1][1] = GCSignal.freshLabel(CompEnv.rnd);
	}

	private void receiveGTT() {
		try {
			Flag.sw.startGCIO();
			GCSignal.receive(is, gtt[0][1]);
			GCSignal.receive(is, gtt[1][0]);
			GCSignal.receive(is, gtt[1][1]);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public GCSignal and(GCSignal a, GCSignal b) {
		Flag.sw.startGC();

		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res = ( (a.v && b.v) ? _ONE :_ZERO);
		else if (a.isPublic())
			res = a.v ? b :_ZERO;
		else if (b.isPublic())
			res = b.v ? a :_ZERO;
		else {
			receiveGTT();

			int i0 = a.getLSB();
			int i1 = b.getLSB();

			res = gb.dec(a, b, gid, gtt[i0][i1]);
			gid++;
		}
		Flag.sw.stopGC();
		return res;
	}
}