package test.others;

import java.util.Random;

import test.Utils;
import circuits.IntegerLib;
import flexsc.MinimumCompEnv;

public class TestAddingNoNetwork {

	static public void main(String args[]) throws Exception
	{
		Random rnd = new Random();
		int intA = rnd.nextInt()%(1<<15);
		int intB = rnd.nextInt()%(1<<15);
		System.out.println(intA+" "+intB+" "+((intA+intB)*intA));

		MinimumCompEnv gen = new MinimumCompEnv(null, null, null);
		
		Boolean[] a = gen.inputOfAlice(Utils.fromInt(intA, 32));
		Boolean[] b = gen.inputOfBob(Utils.fromInt(intB, 32));

		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(gen);
		Boolean[] d = lib.add(a ,b);
		d = lib.multiply(a, d);
		
		boolean[] z = gen.outputToAlice(d);
		System.out.println(Utils.toInt(z));
	}
}