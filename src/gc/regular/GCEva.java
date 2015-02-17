package gc.regular;

import flexsc.Flag;
import gc.GCEvaComp;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public class GCEva extends GCEvaComp {
	Garbler gb = new Garbler();
	GCSignal[][] gtt = new GCSignal[2][2];

	public GCEva(InputStream is, OutputStream os) {
		super(is, os);
		gtt[0][0] = GCSignal.ZERO;
	}

	private void receiveGTT() {
		try {
			Flag.sw.startGCIO();
			gtt[0][1] = GCSignal.receive(is);
			gtt[1][0] = GCSignal.receive(is);
			gtt[1][1] = GCSignal.receive(is);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	GCSignal false_Signal = new GCSignal(false);
	GCSignal true_Signal = new GCSignal(true);
	public GCSignal and(GCSignal a, GCSignal b) {
		Flag.sw.startGC();
		GCSignal res;
		if (a.isPublic() && b.isPublic()) {
			res = (a.v && b.v) ? true_Signal : false_Signal;
		}
		else if (a.isPublic())
			res = a.v ? b : false_Signal;
		else if (b.isPublic())
			res = b.v ? a : false_Signal;
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