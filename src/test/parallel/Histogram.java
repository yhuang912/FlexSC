package test.parallel;

import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class Histogram {

	public static void getHistogram(int machineId, Machine machine, CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {
		Class c = Class.forName("test.parallel.HistogramMapper");
		Gadget histogramMapper = (Gadget) c.newInstance();
		Object[] histogramInputs = new Object[1];
		histogramInputs[0] = machine.input;
		histogramMapper.setInputs(histogramInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength);
		Object[] output = (Object[]) histogramMapper.compute();

		// System.out.println(machineId + ": histogram mappper done");
		// listen
		c = Class.forName("test.parallel.AnotherSortGadget");
		Gadget gadge = (Gadget) c.newInstance();
		Object[] inputs = new Object[2];
		inputs[0] = output[0];
		inputs[1] = output[1];
		gadge.setInputs(inputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength);
		output = (Object[]) gadge.compute();

		// System.out.println(machineId + ": sorting done");

		c = Class.forName("test.parallel.PrefixSumGadget");
		PrefixSumGadget prefixSumGadget = (PrefixSumGadget) c.newInstance();
		Object[] prefixSumInputs = new Object[1];
		prefixSumInputs[0] = output[1];
		prefixSumGadget.setInputs(prefixSumInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections);
		Object[] prefixSumDataResult = (Object[]) prefixSumGadget.compute();

		/*long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		if (machineId == 0 && env.party.equals(Party.Alice)) {
			System.out.println(machineId + ": " + (totalMemory - freeMemory));
		}
		prefixSumGadget = null;
		System.gc();
		totalMemory = Runtime.getRuntime().totalMemory();
		freeMemory = Runtime.getRuntime().freeMemory();
		if (machineId == 0 && env.party.equals(Party.Alice)) {
			System.out.println(machineId + ": " + (totalMemory - freeMemory));
		}*/

		c = Class.forName("test.parallel.MarkerWithLastValueGadget");
		MarkerWithLastValueGadget markerGadget = (MarkerWithLastValueGadget) c.newInstance();
		Object[] markerInputs = new Object[1];
		markerInputs[0] = output[0];
		markerGadget.setInputs(markerInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections,
				machine.totalMachines);
		Object marker = markerGadget.compute();

		c = Class.forName("test.parallel.SubtractGadget");
		SubtractGadget subtractGadget = (SubtractGadget) c.newInstance();
		inputs = new Object[3];
		inputs[0] = marker; // sort by flag
		inputs[1] = markerInputs[0]; // actual value
		inputs[2] = prefixSumDataResult[0]; // frequency
		subtractGadget.setInputs(inputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections);
		output = (Object[]) subtractGadget.compute();
	}

}
