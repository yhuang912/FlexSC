package caes;



public class AesWrapper {

	SWIGTYPE_p_unsigned_char pt, ct, ky;

	public AesWrapper() {
		pt = Aes.new_char_array(16);
		ct = Aes.new_char_array(16);
		ky = Aes.new_char_array(16);
	}

	/*finalize() {
		Aes.delete_char_array(pt);
		Aes.delete_char_array(ct);
		Aes.delete_char_array(ky);
	}*/

	// key should be 128 bits
	public short[] garble(short[] plainText, short[] key) {
		int inputLength = getAesLength(plainText.length);
		//final long startTime1 = System.nanoTime();
		int padding = (inputLength % 16 == 0) ? 0 : 1;

		// TODO(kartik): set key
		// short[] key = new short[] {1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
		setArray(pt, plainText);
		setArray(ky, key);
		Aes.intel_AES_enc128(pt, ct, ky, inputLength/16 + padding);

        short[] ret = getArray(ct, inputLength);
        //final long endTime1 = System.nanoTime();
		//System.out.println(endTime1 - startTime1);
		return ret;
	}

	public short[] decrypt(short[] cipherText) {
		int inputLength = getAesLength(cipherText.length);
		int padding = (inputLength % 16 == 0) ? 0 : 1;
		SWIGTYPE_p_unsigned_char ct = Aes.new_char_array(inputLength);
		SWIGTYPE_p_unsigned_char pt = Aes.new_char_array(inputLength);
		SWIGTYPE_p_unsigned_char ky = Aes.new_char_array(16);

		// TODO(kartik): set key
		short[] key = new short[] {1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
		setArray(ct, cipherText);
		setArray(ky, key);

		Aes.intel_AES_dec128(ct, pt, ky, inputLength/16 + padding);
		short[] ret = getArray(pt, inputLength);
		Aes.delete_char_array(pt);
		Aes.delete_char_array(ct);
		Aes.delete_char_array(ky);
		return ret;
	}

	int getAesLength(int len) {
		int ret = len;
		if (len % 16 == 0) {
			return ret;
		}
		return ret + (16 - len % 16);
	}

	void setArray(SWIGTYPE_p_unsigned_char ar, short[] jArr) {
		for (int i = 0; i < jArr.length; i++) {
			Aes.char_array_setitem(ar, i, jArr[i]);
		}
		// padding?
	}

	short[] getArray(SWIGTYPE_p_unsigned_char ar, int len) {
		short[] jArr = new short[len];
		for (int i = 0; i < len; i++) {
			jArr[i] = Aes.char_array_getitem(ar, i);
		}
		// padding?
		return jArr;
	}

	// 16 bytes to 16 shorts
	public short[] getShortFromBytes(byte[] k) {
		// System.out.println(k.length);
		short[] b = new short[16];
		int i = 0;
		while (i < Math.min(k.length, 16)) {
			b[i] = k[i];
			i++;
		}
		while (i < 16) {
			b[i] = 0;
			i++;
		}
		return b;
	}

	// returns the first 10 bytes of the 16 entry short array
	public byte[] getBytesFromShort(short[] k) {
		int i = 0;
		byte[] b = new byte[10];
		while (i < 10) {
			b[i] = (byte) k[i];
			i++;
		}
		return b;
	}
}
