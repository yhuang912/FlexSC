package gc.offline;

import flexsc.Flag;
import gc.GCGenComp;
import gc.GCSignal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GCGen extends GCGenComp {
	Garbler gb;

	public static BufferedInputStream fin = null;
	public static BufferedOutputStream fout = null;
	static{
		try {
			if(Flag.offline) {
				fin = new BufferedInputStream(new FileInputStream("table"), 1024*1024*50);
				R = GCSignal.receive(fin);
				R.setLSB();
			}
			else {
				fout = new BufferedOutputStream(new FileOutputStream("table"), 1024*1024*50);
				R.send(fout);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public GCGen(InputStream is, OutputStream os) {
		super(is, os);
		gb = new Garbler();
	}
	
	@Override
	public void finalize( ){
		try {
			fout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private GCSignal[][] gtt = new GCSignal[2][2];
	private GCSignal labelL[] = new GCSignal[2];
	private GCSignal labelR[] = new GCSignal[2];
	GCSignal[] lb = new GCSignal[2];;
	private GCSignal garble(GCSignal a, GCSignal b) {
		labelL[0] = a;
		labelL[1] = R.xor(labelL[0]);
		labelR[0] = b;
		labelR[1] = R.xor(labelR[0]);

		int cL = a.getLSB() ? 1 : 0;
		int cR = b.getLSB() ? 1 : 0;

		lb[cL & cR] = gb.enc(labelL[cL], labelR[cR], gid, GCSignal.ZERO);
		lb[1 - (cL & cR)] = R.xor(lb[cL & cR]);

		gtt[0 ^ cL][0 ^ cR] = lb[0];
		gtt[0 ^ cL][1 ^ cR] = lb[0];
		gtt[1 ^ cL][0 ^ cR] = lb[0];
		gtt[1 ^ cL][1 ^ cR] = lb[1];

		if (cL != 0 || cR != 0)
			gtt[0 ^ cL][0 ^ cR] = gb.enc(labelL[0], labelR[0], gid,
					gtt[0 ^ cL][0 ^ cR]);
		if (cL != 0 || cR != 1)
			gtt[0 ^ cL][1 ^ cR] = gb.enc(labelL[0], labelR[1], gid,
					gtt[0 ^ cL][1 ^ cR]);
		if (cL != 1 || cR != 0)
			gtt[1 ^ cL][0 ^ cR] = gb.enc(labelL[1], labelR[0], gid,
					gtt[1 ^ cL][0 ^ cR]);
		if (cL != 1 || cR != 1)
			gtt[1 ^ cL][1 ^ cR] = gb.enc(labelL[1], labelR[1], gid,
					gtt[1 ^ cL][1 ^ cR]);
		// assert(gb.enc(labelL[cL], labelR[cR], gid,
		// gtt[0][0]).equals(Label.ZERO)) : "Garbling problem.";
		return lb[0];
	}

	private GCSignal readGateFromFile(){
		gtt[0][1] = GCSignal.receive(fin);
		gtt[1][0] = GCSignal.receive(fin);
		gtt[1][1] = GCSignal.receive(fin);
		return GCSignal.receive(fin);
	}

	private void writeGateToFile(GCSignal a){
		gtt[0][1].send(fout);
		gtt[1][0].send(fout);
		gtt[1][1].send(fout);
		a.send(fout);
	}

	private void sendGTT() {
		try {
			Flag.sw.startGCIO();
			gtt[0][1].send(os);
			gtt[1][0].send(os);
			gtt[1][1].send(os);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public GCSignal and(GCSignal a, GCSignal b) {
		++Flag.sw.ands;

		Flag.sw.startGC();
		GCSignal res = null;
		if (a.isPublic() && b.isPublic())
			res = new GCSignal(a.v && b.v);
		else if (a.isPublic())
			res = a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			res = b.v ? a : new GCSignal(false);
		else {
			if(Flag.offline) {
				res = readGateFromFile();
			} else {
				res = garble(a, b);
				writeGateToFile(res);
			}
			sendGTT();
			gid++;
			gatesRemain = true;
		}
		Flag.sw.stopGC();
		return res;
	}

}
