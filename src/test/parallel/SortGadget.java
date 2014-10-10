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

	private GraphNode<T>[] nodes;
	private Comparator<T> comp;

	public SortGadget(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public SortGadget<T> setInputs(GraphNode<T>[] nodes, Comparator<T> comp) {
		this.nodes = nodes;
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
		lib.sort(nodes, dir);

		for (int k = 0; k < machine.logMachines; k++) {
			int diff = (1 << k);
			T mergeDir = ((machine.machineId / (2 * (1 << k))) % 2 == 0) ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
			while (diff != 0) {
				long startCommunicate = System.nanoTime();
				boolean up = (machine.machineId / diff) % 2 == 1 ? true : false;
				InputStream is;
				OutputStream os;
				int commMachine = Machine.log2(diff);
				is = up ? machine.peerIsUp[commMachine] : machine.peerIsDown[commMachine];
				os = up ? machine.peerOsUp[commMachine] : machine.peerOsDown[commMachine]; 

				GraphNode<T>[] receivedNodes = sendReceive(os, is, nodes, nodes.length);
				long endCommunicate = System.nanoTime(), startConcatenate = System.nanoTime();

				GraphNode<T>[] concatenatedNodes = up ? concatenate(receivedNodes, nodes) : concatenate(nodes, receivedNodes); 

				long endConcatenate = System.nanoTime();
				lib.compareAndSwapFirst(concatenatedNodes, 0, concatenatedNodes.length, mergeDir);

				long startConcatenate2 = System.nanoTime();
				int srcPos = up ? concatenatedNodes.length / 2 : 0;
				System.arraycopy(concatenatedNodes, srcPos, nodes, 0, concatenatedNodes.length / 2);
				/*if (up) {
					System.arraycopy(concatenatedNodes, concatenatedNodes.length / 2, nodes, 0, concatenatedNodes.length / 2);
				} else {
					System.arraycopy(concatenatedNodes, 0, nodes, 0, concatenatedNodes.length / 2);
				}*/
				long endConcatenate2 = System.nanoTime();
				communicate += (endCommunicate - startCommunicate);
				concatenate += (endConcatenate2 - startConcatenate2) + (endConcatenate - startConcatenate);
				diff /= 2;
			}
			lib.bitonicMerge(nodes, 0, nodes.length, mergeDir);
		}

		// env.os.flush();
		long finalTimer = System.nanoTime();
		compute = finalTimer - initTimer - (communicate + concatenate);
		/* if (machineId == 0 && env.party.equals(Party.Alice)) {
			System.out.println((1 << logMachines) + "," + inputLength + "," + compute/1000000000.0 + ",Compute");
			System.out.println((1 << logMachines) + "," + inputLength + "," + concatenate/1000000000.0 + ",Concatenate");
			System.out.println((1 << logMachines) + "," + inputLength + "," + communicate/1000000000.0 + ",Communicate");
		}*/
		return null;
	}

	private GraphNode<T>[] sendReceive(OutputStream os, InputStream is, GraphNode<T>[] nodes, int arrayLength) throws IOException {
		GraphNode<T>[] a = (GraphNode<T>[]) Array.newInstance(nodes.getClass().getComponentType(), arrayLength);
		int toTransfer = nodes.length;
		int i = 0, j = 0;
		while (toTransfer > 0) {
			int curTransfer = Math.min(toTransfer, 256);
			toTransfer -= curTransfer;
			for (int k = 0; k < curTransfer; k++, i++) {
				nodes[i].send(os, env);
			}
			os.flush();
			for (int k = 0; k < curTransfer; k++, j++) {
				try {
					a[j] = (GraphNode<T>) nodes.getClass().getComponentType().newInstance();
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
				a[j].read(is, env);
			}
		}
		return a;
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