package ods;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import rand.ISAACProvider;
import flexsc.CompEnv;

public class ObliviousDataStructure<T> {
	public static SecureRandom rnd;
	final public int sp = 80;
	final public int capacity = 3;

	static{
		Security.addProvider(new ISAACProvider ());
		try {
			rnd = SecureRandom.getInstance ("ISAACRandom");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public CompEnv<T> env;
	public ObliviousDataStructure(CompEnv<T> env){
		this.env = env;
	}
}
