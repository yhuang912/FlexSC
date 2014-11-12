package arithcircuit2;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import circuits.arithmetic.IntegerLib;
import network.RWBigInteger;
import paillier.PrivateKey;
import paillier.PublicKey;
import util.Utils;
import flexsc.CompEnv;
import flexsc.Party;

public class ABBAlice extends ABBParty{

	static final int securityParameter = 512;
	public PublicKey pk = new PublicKey();
	public PrivateKey sk = new PrivateKey(securityParameter);
	public ABBAlice(InputStream is, OutputStream os, int bitlength) {
		super(is, os, Party.Alice);
		paillier.Paillier.keyGen(sk,pk);
		RWBigInteger.writeBI(os, pk.n);
		RWBigInteger.writeBI(os, pk.modulous);
		flush();
	}

	public <T>BigInteger inputFromGC(T[] number, CompEnv<T> gen) {
		if(gen.getParty() != Party.Alice)
			try {
				throw new Exception("Party mismatch");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		
		IntegerLib<T> lib = new IntegerLib<>(gen);
		number = lib.padSignedSignal(number, securityParameter);
		T[] rand = lib.randBools(number.length);
		flush();
		gen.outputToBob(lib.sub(number, rand));
		boolean[] res = gen.outputToAlice(rand);
		flush();
		return Utils.toBigInteger(res);
	}
	
	@Override
	public BigInteger inputOfAlice(BigInteger a) {
		BigInteger rand = new BigInteger(securityParameter, CompEnv.rnd);
		RWBigInteger.writeBI(os, rand);
		flush();
		return a.subtract(rand).mod(pk.n);
	}

	@Override
	public BigInteger inputOfBob(BigInteger a) {
		return RWBigInteger.readBI(is).mod(pk.n);
	}

	@Override
	public BigInteger add(BigInteger a, BigInteger b) {
		return a.add(b).mod(pk.n);
	}

	@Override
	public BigInteger multiply(BigInteger a, BigInteger b) {
		BigInteger encA = paillier.Paillier.encrypt(a, pk);
		BigInteger encB = paillier.Paillier.encrypt(b, pk);
		RWBigInteger.writeBI(os, encA);
		RWBigInteger.writeBI(os, encB);
		flush();
		
		BigInteger paddedA2 = RWBigInteger.readBI(is);
		BigInteger paddedsumA2 = paddedA2.add(a);
		BigInteger paddedB2 = RWBigInteger.readBI(is);
		BigInteger paddedsumB2 = paddedB2.add(b);
		
		BigInteger paddedmul = paillier.Paillier.encrypt(paddedsumA2.multiply(paddedsumB2), pk);
		RWBigInteger.writeBI(os, paddedmul);
		flush();
		
		return paillier.Paillier.decrypt(RWBigInteger.readBI(is), sk);
	}

	@Override
	public BigInteger outputToAlice(BigInteger a) {
		return a.add(RWBigInteger.readBI(is)).mod(pk.n);
	}

}
