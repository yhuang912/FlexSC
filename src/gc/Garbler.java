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
	
	public Signal enc(Signal lb0, Signal lb1, long k, Signal m) {
		return getPadding(lb0, lb1, k).xor(m);
	}

	public Signal dec(Signal lb0, Signal lb1, long k, Signal c) {
		return getPadding(lb0, lb1, k).xor(c);
	}
	
	private Signal getPadding(Signal lb0, Signal lb1, long k) {
        sha1.update(lb0.bytes);
        sha1.update(lb1.bytes);
        sha1.update(ByteBuffer.allocate(8).putLong(k).array());
        Signal ret = Signal.newInstance(sha1.digest());
        return ret;
    }
}
