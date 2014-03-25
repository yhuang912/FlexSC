package gc;

public class DbgUtils {

	static void debugMsg(CompEnv<Signal> env, String msg) {
		if (env instanceof GCEva)
			System.err.println(msg);
	}
	
	static void debugVal(CompEnv<Signal> env, Signal bs, String msg)
			throws Exception {
		if (env instanceof GCGen) {
			bs.send(((GCGen) env).os);
			((GCGen) env).R.send(((GCGen) env).os);
		} else {
			int x;
			Signal glb = Signal.receive(((GCEva) env).is);
			Signal R = Signal.receive(((GCEva) env).is);
			if (bs.equals(glb))
				x = 0;
			else if (bs.equals(R.xor(glb)))
				x = 1;
			else
				throw new Exception(String.format("bad label: %s",
						bs.toHexStr()));
			System.out.println(String.format("%s = %d", msg, x));

		}
	}

	static void debugLabel(CompEnv<Signal> env, Signal bs, String msg) {
		if (env instanceof GCGen) {
			System.err.println(String.format("[%s] %s, %s",  msg, bs.toHexStr(), ((GCGen) env).R.xor(bs).toHexStr()));
		} else
			System.out.println(String.format("[%s] %s",  msg, bs.toHexStr()));
	}
}