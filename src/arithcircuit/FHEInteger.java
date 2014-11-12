package arithcircuit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import network.RWBigInteger;
import paillier.PrivateKey;
import paillier.PublicKey;
import flexsc.CompEnv;
import flexsc.Party;

public abstract class FHEInteger<T> {
	public InputStream is;
	public OutputStream os;
	public CompEnv env;
	public BigInteger data;

	static int securityParameter = 1024;
	public PublicKey pk = new PublicKey();

	public static FHEInteger newInstance(CompEnv env, InputStream is, OutputStream os) {
		if(env.p == Party.Alice)
			return new Alice(env, is, os, new PublicKey(), null, null);
		else
			return new Bob(env, is, os, new PublicKey(), null);
	}
	
	public FHEInteger(CompEnv env, InputStream is, OutputStream os, BigInteger data, PublicKey pk) {
		this.is = is;
		this.os = os;
		this.env = env;
		this.data = data;
		this.pk = pk;
	}

	public FHEInteger input(Party inputParty, long data) {
		if(env.p == Party.Alice)
			return new Alice(env, is, os, pk, ((Alice)this).sk, inputParty, data);
		else
			return new Bob(env, is, os, pk, inputParty, data);
	}
	
	public abstract void setup();

	public FHEInteger add(FHEInteger b) {
		FHEInteger ret = FHEInteger.newInstance(env,  is,  os);
		ret.pk = pk;
		if(env.p == Party.Alice)
			((Alice)ret).sk = ((Alice)this).sk;
		ret.data = data.add(b.data).mod(pk.n);
		return ret;
	}

	abstract public FHEInteger multiply(FHEInteger b);
	abstract public long output();

	public void flush() {
		try {
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static class Alice extends FHEInteger {

		public PrivateKey sk;

		public Alice(CompEnv env, InputStream is, OutputStream os, PublicKey pk, PrivateKey sk, BigInteger data) {
			super(env, is, os, data, pk);
		}

		public Alice(CompEnv env, InputStream is, OutputStream os, PublicKey pk, PrivateKey sk, Party inputParty, long data) {
			super(env, is, os, null, pk);
			this.sk = sk;
			if(inputParty == Party.Alice) {
				BigInteger aa = BigInteger.valueOf(data);
				BigInteger rand = new BigInteger(securityParameter, env.rnd);
				RWBigInteger.writeBI(os, rand);
				flush();
				this.data = aa.subtract(rand);
			} else if(inputParty == Party.Bob) {
				this.data = RWBigInteger.readBI(is);
			} else
				throw new RuntimeException("Unknown input parties!");
		}

		public void setup() {
			sk = new PrivateKey(securityParameter);
			paillier.Paillier.keyGen(sk,pk);
			RWBigInteger.writeBI(os, pk.n);
			RWBigInteger.writeBI(os, pk.modulous);
			flush();
		}

		@Override
		public FHEInteger multiply(FHEInteger b) {
			BigInteger encA = paillier.Paillier.encrypt(data, pk);
			BigInteger encB = paillier.Paillier.encrypt(b.data, pk);
			RWBigInteger.writeBI(os, encA);
			RWBigInteger.writeBI(os, encB);
			flush();

			BigInteger paddedA2 = RWBigInteger.readBI(is);
			BigInteger paddedsumA2 = paddedA2.add(data);
			BigInteger paddedB2 = RWBigInteger.readBI(is);
			BigInteger paddedsumB2 = paddedB2.add(b.data);

			BigInteger paddedmul = paillier.Paillier.encrypt(paddedsumA2.multiply(paddedsumB2), pk);
			RWBigInteger.writeBI(os, paddedmul);
			flush();

			return new Alice(env, is, os, pk, sk, paillier.Paillier.decrypt(RWBigInteger.readBI(is), sk));
		}

		@Override
		public long output() {
			return data.add(RWBigInteger.readBI(is)).mod(pk.n).longValue();
		}

	}


	public static class Bob extends FHEInteger {

		public Bob(CompEnv env, InputStream is, OutputStream os, PublicKey pk, BigInteger data) {
			super(env, is, os, data, pk);
		}

		public Bob(CompEnv env, InputStream is, OutputStream os, PublicKey pk, Party inputParty, long data) {
			super(env, is, os, null, pk);
			if(inputParty == Party.Bob) {
				BigInteger aa = BigInteger.valueOf(data);
				BigInteger rand = new BigInteger(securityParameter, env.rnd);
				RWBigInteger.writeBI(os, rand);
				flush();
				this.data = aa.subtract(rand);
			} else if(inputParty == Party.Alice) {
				this.data = RWBigInteger.readBI(is);
			} else
				throw new RuntimeException("Unknown input parties!");
		}

		public void setup() {
			pk.n = RWBigInteger.readBI(is);
			pk.modulous = RWBigInteger.readBI(is);
			pk.k1 = Alice.securityParameter;
		}

		@Override
		public FHEInteger multiply(FHEInteger b) {
			BigInteger r1 = new BigInteger(pk.k1, env.rnd);
			BigInteger r2 = new BigInteger(pk.k1, env.rnd);
			RWBigInteger.writeBI(os, r1.add(data));
			RWBigInteger.writeBI(os, r2.add(b.data));
			flush();

			BigInteger encA2 = paillier.Paillier.encrypt(data, pk);
			BigInteger encB2 = paillier.Paillier.encrypt(b.data, pk);
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

			return new Bob(env, is, os, pk, r1.multiply(r2).negate().mod(pk.n));
		}

		@Override
		public long output() {
			RWBigInteger.writeBI(os, data);
			flush();
			return 0;
		}

	}
}