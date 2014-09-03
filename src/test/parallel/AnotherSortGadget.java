package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import network.BadCommandException;
import network.Machine;
import network.Master;
import circuits.AnotherBitonicSortLib;
import flexsc.Gadget;
import gc.BadLabelException;

public class AnotherSortGadget<T>  extends Gadget<T> {

	@Override
	public void secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		System.out.println("Secure compute starting");
		T[][] x = (T[][]) inputs[0];
		AnotherBitonicSortLib<T> lib =  new AnotherBitonicSortLib<T>(env);
		T dir = (machineId % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		lib.sort(x, dir);

		for (int k = 0; k < Master.LOG_MACHINES; k++) {
			int diff = (1 << k);
			T mergeDir = ((machineId / (2 * (1 << k))) % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
			while (diff != 0) {
				boolean up = (machineId / diff) % 2 == 1 ? true : false;
				InputStream is;
				OutputStream os;
				int commMachine = Machine.log2(diff);
				if (up) {
					is = peerIsUp[commMachine];
					os = peerOsUp[commMachine];
				} else {
					is = peerIsDown[commMachine];
					os = peerOsDown[commMachine];
				}

				send(os, x);
				T[][] receive = receive(is, x.length, x[0].length);
				T[][] array;
				if (up) {
					array = concatenate(receive, x);
				} else {
					array = concatenate(x, receive);
				}

				lib.compareAndSwapFirst(array, 0, array.length, mergeDir);
				if (up) {
					System.arraycopy(array, array.length / 2, x, 0, array.length / 2);
				} else {
					System.arraycopy(array, 0, x, 0, array.length / 2);
				}
				diff /= 2;
			}
			lib.bitonicMerge(x, 0, x.length, mergeDir);
		}

		// if (machineId == 2 || machineId == 3) {
			/*for (int i = 0; i < x.length; i++) {
				debug(" " + Utils.toInt(lib.getBooleans(x[i])));
			}*/
		// }
		// debug("Length of input: " + x.length);
	}

	private void send(OutputStream os, T[][] x) throws IOException {
		for (int i = 0; i < x.length; i++) {
			send(os, x[i]);
		}
		os.flush();
	}

	private T[][] receive(InputStream is, int arrayLength, int intLength) throws IOException {
		T[][] y = env.newTArray(arrayLength, intLength);
		for (int i = 0; i < arrayLength; i++) {
			y[i] = (T[]) read(is, intLength);
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