package arithcircuit;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import network.RWBigInteger;
import util.paillier.Paillier;
import flexsc.CompEnv;
import flexsc.Party;

public class ABBAlice extends ABBParty{

	static final int securityParameter = 80;
	public Paillier paillier ;
	public ABBAlice(InputStream is, OutputStream os, int bitlength) {
		super(is, os, Party.Alice);
		paillier = new Paillier(bitlength, securityParameter);
		RWBigInteger.writeBI(os, paillier.n);
		flush();
	}

	@Override
	public BigInteger inputOfAlice(BigInteger a) {
		BigInteger rand = new BigInteger(paillier.bitLength, CompEnv.rnd);
		RWBigInteger.writeBI(os, rand);
		flush();
		return a.subtract(rand);
	}

	@Override
	public BigInteger inputOfBob(BigInteger a) {
		return RWBigInteger.readBI(is);
	}

	@Override
	public BigInteger add(BigInteger a, BigInteger b) {
		return a.add(b).mod(paillier.n);
	}

	@Override
	public BigInteger multiply(BigInteger a, BigInteger b) {
		BigInteger encA = paillier.Encryption(a);
		BigInteger encB = paillier.Encryption(b);
		RWBigInteger.writeBI(os, encA);
		RWBigInteger.writeBI(os, encB);
		flush();
		
		BigInteger paddedA2 = RWBigInteger.readBI(is);
		BigInteger paddedsumA2 = paddedA2.add(a);
		BigInteger paddedB2 = RWBigInteger.readBI(is);
		BigInteger paddedsumB2 = paddedB2.add(b);
		
		BigInteger paddedmul = paillier.Encryption(paddedsumA2.multiply(paddedsumB2));
		RWBigInteger.writeBI(os, paddedmul);
		flush();
		
		return paillier.Decryption(RWBigInteger.readBI(is));
	}

	@Override
	public BigInteger outputToAlice(BigInteger a) {
		return a.add(RWBigInteger.readBI(is)).mod(paillier.n);
	}

}
