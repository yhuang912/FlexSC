package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import network.BadCommandException;
import network.Machine;
import circuits.BitonicSortLib;
import circuits.Comparator;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SortGadget<T>  extends Gadget<T> {

	private T[][] x;
	private T[][] data;
	private Comparator<T> comp;

	public SortGadget(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public SortGadget<T> setInputs(T[][] x, T[][] data, Comparator<T> comp) {
		this.x = x;
		this.data = data;
		this.comp = comp;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		long communicate = 0;
		long compute = 0;
		long concatenate = 0;
		long initTimer = System.nanoTime();
		BitonicSortLib<T> lib =  new BitonicSortLib<T>(env, comp);
		T dir = (machine.machineId % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		lib.sort(x, data, dir);

		for (int k = 0; k < machine.logMachines; k++) {
			int diff = (1 << k);
			T mergeDir = ((machine.machineId / (2 * (1 << k))) % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
			while (diff != 0) {
				long startCommunicate = System.nanoTime();
				boolean up = (machine.machineId / diff) % 2 == 1 ? true : false;
				InputStream is;
				OutputStream os;
				int commMachine = Machine.log2(diff);
				if (up) {
					is = machine.peerIsUp[commMachine];
					os = machine.peerOsUp[commMachine];
				} else {
					is = machine.peerIsDown[commMachine];
					os = machine.peerOsDown[commMachine];
				}

				T[][] receiveKey = sendReceive(os, is, x, x.length, x[0].length);
				T[][] receiveData = sendReceive(os, is, data, data.length, data[0].length);
				long endCommunicate = System.nanoTime(), startConcatenate = System.nanoTime();

				T[][] arrayKey, arrayData;
				if (up) {
					arrayKey = concatenate(receiveKey, x);
					arrayData = concatenate(receiveData, data);
				} else {
					arrayKey = concatenate(x, receiveKey);
					arrayData = concatenate(data, receiveData);
				}
				long endConcatenate = System.nanoTime();
				lib.compareAndSwapFirst(arrayKey, arrayData, 0, arrayKey.length, mergeDir);

				long startConcatenate2 = System.nanoTime();
				if (up) {
					System.arraycopy(arrayKey, arrayKey.length / 2, x, 0, arrayKey.length / 2);
					System.arraycopy(arrayData, arrayData.length / 2, data, 0, arrayData.length / 2);
				} else {
					System.arraycopy(arrayKey, 0, x, 0, arrayKey.length / 2);
					System.arraycopy(arrayData, 0, data, 0, arrayData.length / 2);
				}
				long endConcatenate2 = System.nanoTime();
				communicate += (endCommunicate - startCommunicate);
				concatenate += (endConcatenate2 - startConcatenate2) + (endConcatenate - startConcatenate);
				diff /= 2;
			}
			lib.bitonicMerge(x, data, 0, x.length, mergeDir);
		}

		env.os.flush();
		T[][][] output = env.newTArray(2, x.length, x[0].length);
		output[0] = x;
		output[1] = data;
		long finalTimer = System.nanoTime();
		compute = finalTimer - initTimer - (communicate + concatenate);
		/* if (machineId == 0 && env.party.equals(Party.Alice)) {
			System.out.println((1 << logMachines) + "," + inputLength + "," + compute/1000000000.0 + ",Compute");
			System.out.println((1 << logMachines) + "," + inputLength + "," + concatenate/1000000000.0 + ",Concatenate");
			System.out.println((1 << logMachines) + "," + inputLength + "," + communicate/1000000000.0 + ",Communicate");
		}*/
		return output;
	}

	private T[][] sendReceive(OutputStream os, InputStream is, T[][] x, int arrayLength, int intLength) throws IOException {
		T[][] y = env.newTArray(arrayLength, intLength);
		int toTransfer = x.length;
		int i = 0, j = 0;
		while (toTransfer > 0) {
			int curTransfer = Math.min(toTransfer, 256);
			toTransfer -= curTransfer;
			for (int k = 0; k < curTransfer; k++, i++) {
				send(os, x[i]);
			}
			os.flush();
			for (int k = 0; k < curTransfer; k++, j++) {
				y[j] = (T[]) read(is, intLength);
			}
		}
		return y;
	}

	public <T> T[] concatenate(T[] A, T[] B) {
	    int aLen = A.length;
	    int bLen = B.length;

	    @SuppressWarnings("unchecked")
	    T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(A, 0, C, 0, aLen);
	    System.arraycopy(B, 0, C, aLen, bLen);

	    return C;
	}
}