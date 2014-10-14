package compiledlib.sketch;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class NoClass implements IWritable<NoClass, Boolean> {

	public static CompEnv<Boolean> env;
	public static IntegerLib<Boolean> lib;

	public NoClass(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
		this.env = env;
		this.lib = lib;
	}

	public int numBits() {
		return 0;
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		return ret;
}

	public NoClass newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass ret = new NoClass(env, lib);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

}
