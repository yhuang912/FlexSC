package flexsc;

import gc.BadLabelException;
import gc.GCSignal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;

public abstract class Gadget<T> {
	protected Object[] inputs;
	protected CompEnv<T> env;
	protected Machine machine;
	/*protected int machineId;
	protected InputStream[] peerIsUp;
	protected OutputStream[] peerOsUp;
	protected InputStream[] peerIsDown;
	protected OutputStream[] peerOsDown;
	protected int logMachines;
	protected int inputLength;*/

	abstract public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException;

	public Object compute() throws InterruptedException, IOException, BadCommandException, BadLabelException {
		// connect(port);
		long startTime = System.nanoTime();
		Object ret = secureCompute();
		env.flush();
		long endTime = System.nanoTime();
		if (machine.machineId == 0 && env.party.equals(Party.Alice)) {
			String[] gadge = this.getClass().getName().split("\\.");
			// System.out.println((1 << logMachines) + "," + inputLength + "," + (endTime - startTime)/1000000000.0 + "," + gadge[gadge.length - 1]);
		}
		// disconnect();
		return ret;
	}

	protected void send(OutputStream os, T[] data) throws IOException {
		Mode mode = env.getMode();
		if (mode == Mode.REAL) {
			GCSignal[] gcData = (GCSignal []) data;
			for (int i = 0; i < gcData.length; i++) {
				gcData[i].send(os);
			}
		} else if(mode == Mode.VERIFY) {
			Boolean[] vData = (Boolean[]) data;
			for (int i = 0; i < vData.length; i++) {
				NetworkUtil.writeBoolean(os, (Boolean) data[i]);
			}
		} else if (mode == Mode.COUNT) {
			
		}
	}

	protected T[] read(InputStream is, int length) throws IOException {
		Mode mode = env.getMode();
		if (mode == Mode.REAL) {
			GCSignal[] signal = new GCSignal[length];
			for (int i = 0; i < length; i++) {
				signal[i] = GCSignal.receive(is);
			}
			return (T[]) signal;
		} else if(mode == Mode.VERIFY) {
			Boolean[] vData = new Boolean[length];
			for (int i = 0; i < length; i++) {
				vData[i] = NetworkUtil.readBoolean(is);
			}
			return (T[]) vData;
		} else if (mode == Mode.COUNT) {
			
		}
		// shouldn't happen;
		return null;
	}

	public Gadget(Object[] inputs, 
			CompEnv<T> env,
			Machine machine) {
		this.inputs = inputs;
		this.env = env;
		this.machine = machine;
	}
}