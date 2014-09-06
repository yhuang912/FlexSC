package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Master;
import test.Utils;
import circuits.IntegerLib;
import flexsc.Gadget;
import gc.BadLabelException;

public class AddGadget<T> extends Gadget<T> {

	@Override
	public void secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException {

		T[][] x = (T[][]) inputs[0];

		IntegerLib<T> lib =  new IntegerLib<T>(env);

		T[] result = x[0];
		for(int i = 1; i < x.length; ++i)
			result = lib.add(result, x[i]);

		prefixSum(result, lib);
	}

	private void prefixSum(T[] prefixSum, IntegerLib<T> lib) throws IOException, BadLabelException {
		int noOfIncomingConnections = 2;
		int noOfOutgoingConnections = 2;
		for (int k = 0; k < Master.LOG_MACHINES; k++) {
			if (noOfOutgoingConnections > 0) {
				send(peerOsUp[k], prefixSum);
				try {
					((BufferedOutputStream) peerOsUp[k]).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				noOfOutgoingConnections--;
			}
			if (noOfIncomingConnections > 0) {
				T[] read = read(peerIsDown[k], prefixSum.length);
				prefixSum = lib.add(prefixSum, read);
				noOfIncomingConnections--;
			}
		}
		// debug("Sum = " + Utils.toInt(lib.getBooleans(prefixSum)));
	}
}
