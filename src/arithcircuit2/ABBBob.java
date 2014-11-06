package arithcircuit2;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import network.RWBigInteger;
import paillier.PublicKey;
import flexsc.CompEnv;
import flexsc.Party;

public class ABBBob extends ABBParty{

	public PublicKey pk = new PublicKey();
	public ABBBob(InputStream is, OutputStream os) {
		super(is, os, Party.Bob);
		pk.n = RWBigInteger.readBI(is);
		pk.modulous = RWBigInteger.readBI(is);
		pk.k1 = ABBAlice.securityParameter;
	}

	@Override
	public BigInteger inputOfBob(BigInteger a) {
		BigInteger rand = new BigInteger(ABBAlice.securityParameter, CompEnv.rnd);
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
		return a.add(b).mod(pk.n);
	}

	@Override
	public BigInteger multiply(BigInteger a, BigInteger b) {
		BigInteger r1 = new BigInteger(ABBAlice.securityParameter, CompEnv.rnd);
		BigInteger r2 = new BigInteger(ABBAlice.securityParameter, CompEnv.rnd);
		RWBigInteger.writeBI(os, r1.add(a));
		RWBigInteger.writeBI(os, r2.add(b));
		flush();

		BigInteger encA2 = paillier.Paillier.encrypt(a, pk);
		BigInteger encB2 = paillier.Paillier.encrypt(b, pk);
		BigInteger encA1 = RWBigInteger.readBI(is);
		BigInteger encA = paillier.Paillier.multiply(
				paillier.Paillier.add(encA2, encA1, pk), r2.negate(), pk);
		BigInteger encB1 = RWBigInteger.readBI(is);
		BigInteger encB = paillier.Paillier.multiply(
				paillier.Paillier.add(encB2, encB1, pk), r1.negate(), pk);
	
		BigInteger paddedmul = RWBigInteger.readBI(is);
		BigInteger res = paillier.Paillier.add(paddedmul, encB, pk);
		res = paillier.Paillier.add(res, encA, pk);

		RWBigInteger.writeBI(os, res);
		flush();
		
		return r1.multiply(r2).negate().mod(pk.n);
	}

	@Override
	public BigInteger outputToAlice(BigInteger a) {
		RWBigInteger.writeBI(os, a);
		flush();
		return BigInteger.ZERO;
	}

}
