package compiledlib.priority_queue;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class NodeId implements IWritable<NodeId, Boolean> {
	public Boolean[] pos;
	public Boolean[] id;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private int m;

	public NodeId(CompEnv<Boolean> env, IntegerLib<Boolean> lib, int m) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.pos = env.inputOfAlice(Utils.fromInt(0, m));
		this.id = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return ((0)+(m))+(m);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = pos;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = id;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public NodeId newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NodeId ret = new NodeId(env, lib, m);
		Boolean[] tmp;
		int now = 0;
		ret.pos = new Boolean[m];
		System.arraycopy(data, now, ret.pos, 0, m);
		now += m;
		ret.id = new Boolean[m];
		System.arraycopy(data, now, ret.id, 0, m);
		now += m;
		return ret;
}

}
