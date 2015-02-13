package compiledlib;
import circuits.arithmetic.FloatLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class NoClass implements IWritable<NoClass, Boolean> {

	public static CompEnv<Boolean> env;
	public static IntegerLib<Boolean> intLib;
	public static FloatLib<Boolean> floatLib;

	public NoClass(CompEnv<Boolean> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
	}

	public int numBits() {
		return 0;
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		return ret;
}

	public NoClass newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass ret = new NoClass(env);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

}
