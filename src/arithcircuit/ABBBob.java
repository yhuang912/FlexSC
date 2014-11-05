package arithcircuit;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import network.RWBigInteger;
import util.paillier.Paillier;
import flexsc.CompEnv;
import flexsc.Party;

public class ABBBob extends ABBParty{

	Paillier paillier = new Paillier();
	public ABBBob(InputStream is, OutputStream os) {
		super(is, os, Party.Bob);
		paillier.n = RWBigInteger.readBI(is);
		paillier.nsquare = paillier.n.multiply(paillier.n);
	}

	@Override
	public BigInteger inputOfBob(BigInteger a) {
		BigInteger rand = new BigInteger(paillier.bitLength, CompEnv.rnd);
		RWBigInteger.writeBI(os, rand);
		flush();
		return a.subtract(rand);
	}

	@Override
	public BigInteger inputOfAlice(BigInteger a) {
		return RWBigInteger.readBI(is);
	}

	@Override
	public BigInteger add(BigInteger a, BigInteger b) {
		return a.add(b).mod(paillier.n);
	}

	@Override
	public BigInteger multiply(BigInteger a, BigInteger b) {
		BigInteger r1 = new BigInteger(paillier.bitLength, CompEnv.rnd);
		BigInteger r2 = new BigInteger(paillier.bitLength, CompEnv.rnd);
		RWBigInteger.writeBI(os, r1.add(a));
		RWBigInteger.writeBI(os, r2.add(b));
		flush();

		BigInteger encA2 = paillier.Encryption(a);
		BigInteger encB2 = paillier.Encryption(b);
		
		
		BigInteger encA1 = RWBigInteger.readBI(is);
		BigInteger encA = paillier.multiply(paillier.add(encA2, encA1), r2.negate());
		BigInteger encB1 = RWBigInteger.readBI(is);
		BigInteger encB = paillier.multiply(paillier.add(encB2, encB1), r1.negate());
		

		BigInteger paddedmul = RWBigInteger.readBI(is);
		BigInteger res = paillier.add(paddedmul, encB);
		res = paillier.add(res, encA);

		RWBigInteger.writeBI(os, res);
		flush();
		
		return r1.multiply(r2).negate().mod(paillier.n);
	}

	@Override
	public BigInteger outputToAlice(BigInteger a) {
		RWBigInteger.writeBI(os, a);
		flush();
		return BigInteger.ZERO;
	}

}
