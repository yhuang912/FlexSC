package gc;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

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
	
	private GCSignal getPadding(GCSignal lb0, GCSignal lb1, long k) {
        sha1.update(lb0.bytes);
        sha1.update(lb1.bytes);
        sha1.update(ByteBuffer.allocate(8).putLong(k).array());
        GCSignal ret = GCSignal.newInstance(sha1.digest());
        return ret;
    }
}
