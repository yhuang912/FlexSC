package gc;

import flexsc.CompEnv;

public class DbgUtils {

	static void debugMsg(CompEnv<GCSignal> env, String msg) {
		if (env instanceof GCEva)
			System.err.println(msg);
	}
	
	static void debugVal(CompEnv<GCSignal> env, GCSignal bs, String msg)
			throws Exception {
		if (env instanceof GCGen) {
			bs.send(((GCGen) env).os);
			((GCGen) env).R.send(((GCGen) env).os);
		} else {
			int x;
			GCSignal glb = GCSignal.receive(((GCEva) env).is);
			GCSignal R = GCSignal.receive(((GCEva) env).is);
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

	static void debugLabel(CompEnv<GCSignal> env, GCSignal bs, String msg) {
		if (env instanceof GCGen) {
			System.err.println(String.format("[%s] %s, %s",  msg, bs.toHexStr(), ((GCGen) env).R.xor(bs).toHexStr()));
		} else
			System.out.println(String.format("[%s] %s",  msg, bs.toHexStr()));
	}
}