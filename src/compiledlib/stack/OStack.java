package compiledlib.stack;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import oram.BucketLib;
import oram.CircuitOram;
import rand.ISAACProvider;
import util.Utils;
import flexsc.CompEnv;


//hand made version
public class OStack<T> {
	CircuitOram<T> oram;
	BucketLib<T> lib;
	T[] top;
	T[] counter;
	public static SecureRandom rnd;
	final public int sp = 80;
	final public int capacity = 3;

	static {
		Security.addProvider(new ISAACProvider());
		try {
			rnd = SecureRandom.getInstance("ISAACRandom");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public CompEnv<T> env;

	public OStack(CompEnv<T> env, int N, int dataSize, int sp) throws Exception {
		this.env = env;
		oram = new CircuitOram<T>(env, N, dataSize, capacity, sp);
		oram = new CircuitOram<T>(env, N, dataSize + oram.lengthOfPos,
				capacity, sp);
		lib = oram.lib;
		top = lib.randBools(oram.lengthOfPos);
		counter = env.inputOfAlice(Utils.fromInt(1, oram.lengthOfIden));
	}

	public OStack(CompEnv<T> env, int N, int dataSize) throws Exception {
		this.env = env;
		oram = new CircuitOram<T>(env, N, dataSize, capacity, sp);
		oram = new CircuitOram<T>(env, N, dataSize + oram.lengthOfPos,
				capacity, sp);
		lib = oram.lib;
		top = lib.randBools(oram.lengthOfPos);
		counter = env.inputOfAlice(Utils.fromInt(1, oram.lengthOfIden));
	}

	public T[] access(T op, T[] data) throws Exception {
		T[] newIter = lib.randBools(oram.lengthOfPos);
		// boolean[] pos = lib.declassifyToBoth(top);
		T[] block = oram.conditionalReadAndRemove(counter, top, op);
		T[] newBlock = oram.env.newTArray(data.length + oram.lengthOfPos);
		System.arraycopy(top, 0, newBlock, 0, top.length);
		System.arraycopy(data, 0, newBlock, top.length, data.length);
		counter = lib.mux(
				lib.add(counter, lib.toSignals(1, oram.lengthOfIden)),
				lib.sub(counter, lib.toSignals(1, oram.lengthOfIden)), op);

		oram.conditionalPutBack(counter, newIter, newBlock, lib.not(op));

		top = lib.mux(newIter, Arrays.copyOf(block, top.length), op);

		return Arrays.copyOfRange(block, top.length, block.length);
	}

	// (d1, p1) - > (d2, p2) -> (d3, p3)
	// op = 0
	public void push(T[] data) throws Exception {
		T[] newIter = lib.randBools(oram.lengthOfPos);
		T[] block = oram.env.newTArray(data.length + oram.lengthOfPos);
		System.arraycopy(top, 0, block, 0, top.length);
		System.arraycopy(data, 0, block, top.length, data.length);
		counter = lib.add(counter, lib.toSignals(1, oram.lengthOfIden));
		oram.putBack(counter, newIter, block);
		top = newIter;
	}

	// op = 1
	public T[] pop() throws IOException, Exception {
		boolean[] pos = lib.declassifyToBoth(top);
		T[] block = oram.readAndRemove(counter, pos, false);
		counter = lib.sub(counter, lib.toSignals(1, oram.lengthOfIden));
		top = Arrays.copyOf(block, top.length);

		oram.conditionalPutBack(lib.dummyBlock.iden, lib.dummyBlock.pos,
				lib.dummyBlock.data, lib.SIGNAL_ZERO);

		return Arrays.copyOfRange(block, top.length, block.length);
	}
}
