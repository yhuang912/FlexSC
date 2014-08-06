package pm;

import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;
import flexsc.BooleanCompEnv;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

/*
 * The computational environment for performance measurement. 
 */
public class PMCompEnv extends BooleanCompEnv {
	public static class Statistics {
		public int andGate = 0;
		public int xorGate = 0;
		public int notGate = 0;
		public int OTs = 0;
		public int NumEncAlice = 0;
		public int NumEncBob = 0;
		public long bandwidth = 0;
		public void flush() {
			bandwidth = 0;
			andGate = 0;
			xorGate = 0;
			notGate = 0;
			OTs = 0;
			NumEncAlice = 0;
			NumEncBob = 0;
		}
		public void add(Statistics s2) {
			andGate += s2.andGate;
			xorGate += s2.xorGate;
			notGate += s2.notGate;
			OTs += s2.OTs;
			NumEncAlice += s2.NumEncAlice;
			NumEncBob+=s2.NumEncBob;
			bandwidth +=s2.bandwidth;
		}
		public void finalize() {
			NumEncAlice = andGate*4+OTs*2;
			NumEncBob= andGate*1+OTs*1;
		}
		
		public Statistics newInstance() {
			Statistics s = new Statistics();
			s.andGate = andGate;
			s.xorGate = xorGate;
			s.notGate = notGate;
			s.OTs = OTs;
			s.NumEncAlice = NumEncAlice;
			s.NumEncBob = NumEncBob;
			s.bandwidth = bandwidth;
			return s;
		}
	}
	
	public Statistics statistic;
	Boolean t = true;
	Boolean f = false;

	public PMCompEnv(InputStream is, OutputStream os, Party p) {
		super(is, os, p, Mode.COUNT);
		this.party = p;
		t = true;
		f = false;
		statistic = new Statistics();
	}
	
	@Override
	public Boolean inputOfAlice(boolean in) {
		++statistic.OTs;
		statistic.bandwidth+=10;
		return f;
	}

	@Override
	public Boolean inputOfBob(boolean in) {
		return f;
	}

	@Override
	public boolean outputToAlice(Boolean out) {
		statistic.bandwidth+=10;
		return false;
	}

	@Override
	public Boolean and(Boolean a, Boolean b) {
		++statistic.andGate;
		statistic.bandwidth += 3*10;
		return f;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		++statistic.xorGate;
		return f;
	}

	@Override
	public Boolean not(Boolean a) {
		++statistic.notGate;
		return f;
	}

	@Override
	public Boolean ONE() {
		return t;
	}

	@Override
	public Boolean ZERO() {
		return f;
	}

	@Override
	public boolean[] outputToAlice(Boolean[] out) {
		statistic.bandwidth += 10 * out.length;
		return Utils.tobooleanArray(out);
	}

	@Override
	public Boolean[] inputOfAlice(boolean[] in) {
		statistic.OTs += in.length;
		statistic.bandwidth+=10*2*(80+in.length);
		return Utils.toBooleanArray(in);	
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) {
		return Utils.toBooleanArray(in);
	}

	@Override
	public CompEnv<Boolean> getNewInstance(InputStream in, OutputStream os) {
		return new PMCompEnv(in, os, this.getParty());
	}
}