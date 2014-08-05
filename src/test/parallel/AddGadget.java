package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.Master;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.GCSignal;

public class AddGadget<T> extends Gadget<T> {

	@Override
	public Object secureCompute(CompEnv<T> e, Object[] o, int port) throws Exception {
		connect(port);

		T[][] x = (T[][]) o[0];

		IntegerLib<T> lib =  new IntegerLib<T>(e);

		T[] result = x[0];
		for(int i = 1; i < x.length; ++i)
			result = lib.add(result, x[i]);

		prefixSum(result, lib);
		return null;
	}

	private void prefixSum(T[] sum, IntegerLib<T> lib) throws Exception {
		int noOfIncomingConnections = numberOfIncomingConnections;
		int noOfOutgoingConnections = numberOfOutgoingConnections;
		GCSignal[] prefixSum = (GCSignal[]) sum;
		for (int k = 0; k < Master.LOG_MACHINES; k++) {
			if (noOfOutgoingConnections > 0) {
				for (int i = 0; i < prefixSum.length; i++) {
					prefixSum[i].send(peerOsUp[k]);
				}
				try {
					((BufferedOutputStream) peerOsUp[k]).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				noOfOutgoingConnections--;
			}
			if (noOfIncomingConnections > 0) {
				GCSignal[] read = new GCSignal[prefixSum.length];
				for (int i = 0; i < prefixSum.length; i++) {
					read[i] = GCSignal.receive(peerIsDown[k]);
				}
				prefixSum = (GCSignal[]) lib.add((T[]) prefixSum, (T[]) read);
				noOfIncomingConnections--;
			}
		}
		System.out.println(machineId + " Sum = " + Utils.toInt(lib.getBooleans((T[]) prefixSum)));
		// disconnectFromPeers();
	}
}
