package gc;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class Garbler {
	private MessageDigest sha1 = null;
	private Cipher cipher = null;

	Garbler() {
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpecification = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpecification, new IvParameterSpec(Arrays.copyOf(key, 16)));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
	
	
	public GCSignal enc(GCSignal lb0, GCSignal lb1, long k, GCSignal m) {
		return getPadding(lb0, lb1, k).xor(m);
	}

	static byte b = 4;
	public GCSignal dec(GCSignal lb0, GCSignal lb1, long k, GCSignal c) throws IllegalBlockSizeException, BadPaddingException {
		if (GCGen.useCGarble) {
			  cipher.update((ByteBuffer.allocate(lb0.len+lb1.len+12).put(lb0.bytes).put(lb1.bytes).putLong(k).put(b).put(b).put(b).put(b)).array());
			 // byte[] haha = new byte[10];
	          GCSignal ret = GCSignal.newInstance(cipher.doFinal());
	          return ret;
		} else {
			return getPadding(lb0, lb1, k).xor(c);
		}
	}
	
	static byte[] a = new byte[2*16];
	static byte[] key = {1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};
	static{for(int i = 0; i < key.length; ++i)
		key[i] += '0';
		}
	private GCSignal getPadding(GCSignal lb0, GCSignal lb1, long k) {
		
//        sha1.update(lb0.bytes);
//        sha1.update(lb1.bytes);
//        sha1.update(ByteBuffer.allocate(8).putLong(k).array());
		
//		byte[] cipherText = Aes.intel_AES_enc128_char((ByteBuffer.allocate(lb0.len+lb1.len).put(lb0.bytes).put(lb1.bytes).putLong(k)).array(), a, key, 2);
		  sha1.update((ByteBuffer.allocate(lb0.len+lb1.len+8).put(lb0.bytes).put(lb1.bytes).putLong(k)));
          GCSignal ret = GCSignal.newInstance(sha1.digest());
//		  sha1.digest();
//      GCSignal ret = GCSignal.newInstance(cipherText);

		//        sha1.update(ByteBuffer.allocate(lb0.len+lb1.len+8).put(lb0.bytes).put(lb1.bytes).putLong(k).array());
//        GCSignal ret = GCSignal.newInstance(sha1.digest(ByteBuffer.allocate(lb0.len+lb1.len+8).put(lb0.bytes).put(lb1.bytes).putLong(k).array()));

        return ret;
//		return f;
    }
//	GCSignal f = GCSignal.freshLabel(NEW SecureRandom());
}
