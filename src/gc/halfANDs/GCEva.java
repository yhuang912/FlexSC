package gc.halfANDs;

import flexsc.Flag;
import gc.GCEvaComp;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public class GCEva extends GCEvaComp {
	Garbler gb;

	public GCEva(InputStream is, OutputStream os) {
		super(is, os);
		gb = new Garbler();
	}

	public GCSignal and(GCSignal a, GCSignal b) {
		Flag.sw.startGC();
		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res =  ( (a.v && b.v) ? _ONE:_ZERO);
		else if (a.isPublic())
			res =  a.v ? b :_ZERO;
		else if (b.isPublic())
			res = b.v ? a : _ZERO;
		else {

			int i0 = a.getLSB() ? 1 : 0;
			int i1 = b.getLSB() ? 1 : 0;

			GCSignal TG = GCSignal.ZERO, WG, TE = GCSignal.ZERO, WE;
			try {
				Flag.sw.startGCIO();
				TG = GCSignal.receive(is);
				TE = GCSignal.receive(is);
				Flag.sw.stopGCIO();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			WG = gb.hash(a, gid, false).xor((i0 == 1) ? TG : GCSignal.ZERO);
			WE = gb.hash(b, gid, true).xor((i1 == 1) ? (TE.xor(a)) : GCSignal.ZERO);
			
			res = WG.xor(WE);
			
			gid++;
		}
		Flag.sw.stopGC();
		return res;
	}
}