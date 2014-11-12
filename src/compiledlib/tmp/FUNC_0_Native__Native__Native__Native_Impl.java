package compiledlib.tmp;
import arithcircuit.FHEInteger;
import flexsc.CompEnv;
public class FUNC_0_Native__Native__Native__Native_Impl extends FUNC_0_Native__Native__Native__Native_ {
	public FUNC_0_Native__Native__Native__Native_Impl(CompEnv<Boolean> env) throws Exception {
		super(env);
	}

	public FHEInteger<Boolean> calc(FHEInteger<Boolean> a, FHEInteger<Boolean> b, FHEInteger<Boolean> c) throws Exception {
		FHEInteger<Boolean> f_tmp_0 = a.multiply(b);
		FHEInteger<Boolean> __tmp0 = f_tmp_0;
		FHEInteger<Boolean> f_tmp_1 = __tmp0.add(c);
		FHEInteger<Boolean> __tmp1 = f_tmp_1;
		return __tmp1;
	}
}
