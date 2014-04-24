//Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package ot;

import java.security.*;
import java.math.*;

public final class Cipher {
	private static final int unitLength = 160; // SHA-1 has 160-bit output.

	private MessageDigest sha1;

	public Cipher() {
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public BigInteger encrypt(BigInteger key, BigInteger msg, int msgLength) {
		assert (msgLength <= unitLength) : "Message longer than hash block width.";
		return msg.xor(getPaddingOfLength(key, msgLength));
	}

	public BigInteger decrypt(BigInteger key, BigInteger cph, int cphLength) {
		assert (cphLength > unitLength) : "Ciphertext longer than hash block width.";
		return cph.xor(getPaddingOfLength(key, cphLength));
	}

	private BigInteger getPaddingOfLength(BigInteger key, int padLength) {
		sha1.update(key.toByteArray());
		return new BigInteger(1, sha1.digest());
	}

	public BigInteger encrypt(int j, BigInteger key, BigInteger msg,
			int msgLength) {
		return msg.xor(getPaddingOfLength(j, key, msgLength));
	}

	public BigInteger decrypt(int j, BigInteger key, BigInteger cph,
			int cphLength) {
		return cph.xor(getPaddingOfLength(j, key, cphLength));
	}

	private BigInteger getPaddingOfLength(int j, BigInteger key, int padLength) {
		sha1.update(BigInteger.valueOf(j).toByteArray());
		sha1.update(key.toByteArray());
		BigInteger pad = BigInteger.ZERO;
		byte[] tmp = new byte[unitLength / 8];
		for (int i = 0; i < padLength / unitLength; i++) {
			System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength / 8);
			pad = pad.shiftLeft(unitLength).xor(new BigInteger(1, tmp));
			sha1.update(tmp);
		}
		System.arraycopy(sha1.digest(), 0, tmp, 0, unitLength / 8);
		pad = pad.shiftLeft(padLength % unitLength).xor(
				(new BigInteger(1, tmp)).shiftRight(unitLength
						- (padLength % unitLength)));
		return pad;
	}
}