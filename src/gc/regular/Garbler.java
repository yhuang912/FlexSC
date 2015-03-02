package gc.regular;

import gc.GCSignal;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;

final class Garbler {
	private MessageDigest sha1 = null;
	Garbler() {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public GCSignal enc(GCSignal lb0, GCSignal lb1, long k, GCSignal m) {
		return getPadding(lb0, lb1, k).xor(m);
	}

	public GCSignal dec(GCSignal lb0, GCSignal lb1, long k, GCSignal c) {
		return getPadding(lb0, lb1, k).xor(c);
	}

	public void enc(GCSignal lb0, GCSignal lb1, long k, GCSignal m, GCSignal ret) {
		getPadding(lb0, lb1, k, ret);
		GCSignal.xor(m, ret, ret);
	}
	
	public void dec(GCSignal lb0, GCSignal lb1, long k, GCSignal c, GCSignal ret) {
//		return getPadding(lb0, lb1, k).xor(c);
		getPadding(lb0, lb1, k, ret);
		GCSignal.xor(c, ret, ret);
	}
	
	ByteBuffer buffer = ByteBuffer.allocate(GCSignal.len*2+8); 
	private GCSignal getPadding(GCSignal lb0, GCSignal lb1, long k) {
		buffer.clear();
		sha1.update((buffer.put(lb0.bytes).put(lb1.bytes).putLong(k)));
		GCSignal ret = GCSignal.newInstance(sha1.digest());
		return ret;
	}
	
	private void getPadding(GCSignal lb0, GCSignal lb1, long k, GCSignal ret) {
		buffer.clear();
		sha1.update((buffer.put(lb0.bytes).put(lb1.bytes).putLong(k)));
		System.arraycopy(sha1.digest(), 0, ret.bytes, 0, 10);
	}
}
