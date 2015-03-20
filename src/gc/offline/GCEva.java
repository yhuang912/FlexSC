package gc.offline;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCEvaComp;
import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GCEva extends GCEvaComp {
	Garbler gb;
	GCSignal[][] gtt = new GCSignal[2][2];
	public static FileReader fread = null;
	static{
//		try {
			if(Flag.offline && Flag.mode == Mode.OFFLINE) {
//				fin = new BufferedInputStream(new FileInputStream("table"), 1024*1024*1024);
//				GCSignal.receive(fin);
				fread = new FileReader(Flag.tableName);
				fread.read(10);
//				R.setLSB();
			}
			else {
//				fout = new BufferedOutputStream(new FileOutputStream(Flag.tableName), 1024*1024*1024);
//				R.send(fout);
			}
//		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
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
//			if(Flag.offline) {
//			fread.read(gtt[0][1].bytes);
//			fread.read(gtt[1][0].bytes);
//			fread.read(gtt[1][1].bytes);}
//			else{
			GCSignal.receive(is, gtt[0][1]);
			GCSignal.receive(is, gtt[1][0]);
			GCSignal.receive(is, gtt[1][1]);
//			}
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