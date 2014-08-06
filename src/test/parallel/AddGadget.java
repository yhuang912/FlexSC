package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Master;
import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.Gadget;
import flexsc.Mode;
import gc.BadLabelException;
import gc.GCSignal;

public class AddGadget<T> extends Gadget<T> {

	@Override
	public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException {

		T[][] x = (T[][]) inputs[0];

		IntegerLib<T> lib =  new IntegerLib<T>(env);

		T[] result = x[0];
		for(int i = 1; i < x.length; ++i)
			result = lib.add(result, x[i]);

		prefixSum(result, lib);
		return null;
	}

	private void prefixSum(T[] prefixSum, IntegerLib<T> lib) throws IOException, BadLabelException {
		int noOfIncomingConnections = numberOfIncomingConnections;
		int noOfOutgoingConnections = numberOfOutgoingConnections;
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
		debug("Sum = " + Utils.toInt(lib.getBooleans(prefixSum)));
	}
}
