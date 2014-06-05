package caes;

import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;


public class TestAes {
	private static byte[] key = {
        0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
	};
	static SecureRandom rnd = new SecureRandom();

	/* public static void main(String args[]) throws Exception {
		System.loadLibrary("aes");
		short[] ar = new short[] {1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
		AesWrapper aesWrapper = new AesWrapper();
		javaCrypto();
		short[] key = new short[] {1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
		short[] ct = aesWrapper.garble(ar, key);
		short[] pt = aesWrapper.decrypt(ct);
		//for (int i = 0; i < pt.length; i++)
			//System.out.println(pt[i]);
        // test();
	}*/

	/*private static void test() {
		SWIGTYPE_p_unsigned_char ar = Aes.new_char_array(16);
        SWIGTYPE_p_unsigned_char out = Aes.new_char_array(16);
        SWIGTYPE_p_unsigned_char key = Aes.new_char_array(16);
        SWIGTYPE_p_unsigned_char dec = Aes.new_char_array(16);

        for (int i = 0; i < 16; i++) {
            Aes.char_array_setitem(ar, i, (short)(i+10));
            Aes.char_array_setitem(key, i, (byte)(-121));
        }
        // for (int i = 0; i < 4; i++)
        	// System.out.println(Aes.char_array_getitem(out, i));
        Aes.intel_AES_enc128(ar, out, key, 1);

        for (int i = 0; i < 4; i++)
        	System.out.println(Aes.char_array_getitem(out, i));
        
        Aes.intel_AES_dec128(out, dec, key, 1);

    	for (int i = 0; i < 4; i++)
    	    System.out.println(Aes.char_array_getitem(dec, i));
	}

	private static void javaCrypto() throws Exception {
		String input = "";
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
        final long startTime2 = System.nanoTime();
		//for (int j = 0; j < 16; j++) {
			input = "AAAAAAAAAAAAAAAA";
		
			//.currentTimeMillis();
			//for (int i = 0; i < 1; i++) {
				cipher.doFinal(input.getBytes());
			//}
	        //.currentTimeMillis();
	        //System.out.println(input.length());
	        //System.out.println(endTime - startTime);// + " " + (endTime2 - startTime2));
		//}
				final long endTime2 = System.nanoTime();
				System.out.println(endTime2 - startTime2);
	}*/
	
	public static void main(String args[]) {
		System.loadLibrary("aes");
		String ky = new String(key);
		byte[] temp = {
		        (byte) 0xff, (byte) 0x00, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
			};
		byte[] pt = {
				2,65,-128,0,-127,127,70,-114,111,-74,-83,-127,63,110,41,-38
		        //(byte) 0xff, (byte) 0x00, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
			};
		byte[] ct = {
				-71,98,-77,127,95,-47,115,-22,116,-97,-9,-40,-85,-105,-106,85
		        //(byte) 0xff, (byte) 0x00, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
			};
		byte[] cipherText = Aes.intel_AES_enc128_char(pt, temp, key, 1);
		System.out.println("Java " + cipherText.length);
		for (int i = 0; i < cipherText.length; i++) {
			System.out.println((byte) cipherText[i]);
		}
		System.out.println("--------");
		byte[] plainText = Aes.intel_AES_dec128_char(cipherText, temp, key, 1);
		// System.out.println("old " + new String(pt));
		//System.out.println("new " + plainText);
		for (int i = 0; i < plainText.length; i++) {
			if (pt[i] != plainText[i])
				System.out.println("Failed");
			System.out.println("hi" + (byte) plainText[i]);
	
		}
	}
}
