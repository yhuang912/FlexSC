package compiledlib.priority_queue;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
class NodeId implements IWritable<NodeId, Boolean> {
	Boolean[] id;
	Boolean[] pos;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public NodeId(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
		this.env = env;
		this.lib = lib;
		this.id = env.inputOfAlice(Utils.fromInt(0, 32));
		this.pos = env.inputOfAlice(Utils.fromInt(0, 32));
	}

	public int numBits() {
		return ((0)+(32))+(32);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		tmp = id;
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		tmp = pos;
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		return ret;
}

	public NodeId newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NodeId ret = new NodeId(env, lib);
		Boolean[] tmp;
		int now = 0;
		ret.id = new Boolean[32];
		System.arraycopy(data, now, ret.id, 0, 32);
		now += 32;
		ret.pos = new Boolean[32];
		System.arraycopy(data, now, ret.pos, 0, 32);
		now += 32;
		return ret;
}

}
