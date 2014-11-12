package compiledlib.tmp;
import arithcircuit.FHEInteger;
import circuits.arithmetic.FloatLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
abstract public class FUNC_0_Native__Native__Native__Native_ {
	CompEnv<Boolean> env;
	IntegerLib<Boolean> intLib;
	FloatLib<Boolean> floatLib;

	public FUNC_0_Native__Native__Native__Native_(CompEnv<Boolean> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
	}

	public abstract FHEInteger<Boolean> calc(FHEInteger<Boolean> x0, FHEInteger<Boolean> x1, FHEInteger<Boolean> x2) throws Exception;
}
