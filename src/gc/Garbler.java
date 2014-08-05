package gc;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


final class Garbler {
	private MessageDigest sha1 = null;

	Garbler() {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public GCSignal enc(GCSignal lb0, GCSignal lb1, long k, GCSignal m) {
		return getPadding(lb0, lb1, k).xor(m);
	}

	public GCSignal dec(GCSignal lb0, GCSignal lb1, long k, GCSignal c) {
		return getPadding(lb0, lb1, k).xor(c);
	}

	private GCSignal getPadding(GCSignal lb0, GCSignal lb1, long k) {
		sha1.update((ByteBuffer.allocate(GCSignal.len+GCSignal.len+8).put(lb0.bytes).put(lb1.bytes).putLong(k)));
		GCSignal ret = GCSignal.newInstance(sha1.digest());
		return ret;
	}
}
