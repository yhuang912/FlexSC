package arithcircuit2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import flexsc.Party;

public abstract class ABBParty {
	public InputStream is;
	public OutputStream os;
	public Party p;
	public ABBParty(InputStream is, OutputStream os, Party p) {
		this.is = is;
		this.os = os;
		this.p = p;
	}
	abstract public BigInteger inputOfAlice(BigInteger a);
	abstract public BigInteger inputOfBob(BigInteger a);
	abstract public BigInteger add(BigInteger a, BigInteger b);
	abstract public BigInteger multiply(BigInteger a, BigInteger b);
	abstract public BigInteger outputToAlice(BigInteger a);
	
	public void flush() {
		try {
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
