package test.parallel;

import java.io.IOException;
import java.lang.reflect.Array;

import network.BadCommandException;
import network.Master;

import org.apache.commons.lang.ArrayUtils;

import test.Utils;
import circuits.BitonicSortLib;
import flexsc.Gadget;
import gc.BadLabelException;
// Not used
public class SortGadget<T> extends Gadget<T> {

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] x = (T[][]) inputs[0];

		// IntegerLib<T> lib =  new IntegerLib<T>(env);
		BitonicSortLib<T> lib =  new BitonicSortLib<T>(env);
		T dir = (machineId % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		lib.sort(x, dir);

		int id = machineId;
		for (int k = 0; k < Master.LOG_MACHINES; k++) {
			if (id % 2 == 1) {
				send(k, x);
				break;
			} else {
				id /= 2;
				T[][] receive = receive(k, x.length, x[0].length);
				T[][] array = concatenate(x, receive);
				T mergeDir = (id % 2 == 0) ? dir : lib.not(dir);
				// T mergeDir = (id % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
				/*if (machineId == 0 & k == 0 (machineId == 0 && k == 0)) {
					for (int i = 0; i < array.length; i++) {
						debug(" " + Utils.toInt(lib.getBooleans(array[i])));
					}
					debug("Iteration " + k + " done");
				}*/
				lib.bitonicMerge(array, 0, array.length, dir);
				/*if (machineId == 0 & k == 0) {
					for (int i = 0; i < array.length; i++) {
						debug(" " + Utils.toInt(lib.getBooleans(array[i])));
					}
					debug("Iteration " + k + " merge done");
				}*/
				x = array;
				dir = mergeDir;
			}
			if (/*machineId == 0 || */(machineId == 0 && k == 0)) {
				synchronized(this) {
					for (int i = 0; i < x.length; i++) {
						// debug(" " + Utils.toInt(lib.getBooleans(x[i])));
						System.out.println(" " + Utils.toInt(lib.getBooleans(x[i])));
					}
					//debug("Length of input: " + x.length);
					//debug("Iteration " + k + " done");
					System.out.println("Length of input: " + x.length);
					System.out.println("Iteration " + k + " done");
				}
			}
		}

		/*if (machineId == 0) {
			for (int i = 0; i < x.length; i++) {
				debug(" " + Utils.toInt(lib.getBooleans(x[i])));
			}
		}
		debug("Length of input: " + x.length);*/
		return null;
	}

	private void send(int round, T[][] x) throws IOException {
		for (int i = 0; i < x.length; i++) {
			send(peerOsUp[round], x[i]);
		}

		peerOsUp[round].flush();
	}

	private T[][] receive(int round, int arrayLength, int intLength) throws IOException {
		T[][] y = env.newTArray(arrayLength, intLength);
		for (int i = 0; i < arrayLength; i++) {
			y[i] = (T[]) read(peerIsDown[round], intLength);
		}

		return y;
	}

	public <T> T[] concatenate (T[] A, T[] B) {
	    int aLen = A.length;
	    int bLen = B.length;

	    @SuppressWarnings("unchecked")
	    T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(A, 0, C, 0, aLen);
	    System.arraycopy(B, 0, C, aLen, bLen);

	    return C;
	}
}
