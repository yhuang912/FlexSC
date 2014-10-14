package compiledlib.stack;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class BoolArray implements IWritable<BoolArray, Boolean> {
	public Boolean[] data;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public BoolArray(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
		this.env = env;
		this.lib = lib;
		this.data = env.inputOfAlice(Utils.fromInt(0, 32));
	}

	public int numBits() {
		return (0)+(32);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		tmp = data;
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		return ret;
}

	public BoolArray newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		BoolArray ret = new BoolArray(env, lib);
		Boolean[] tmp;
		int now = 0;
		ret.data = new Boolean[32];
		System.arraycopy(data, now, ret.data, 0, 32);
		now += 32;
		return ret;
}

}
