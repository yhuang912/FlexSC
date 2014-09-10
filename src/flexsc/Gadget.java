package flexsc;

import gc.BadLabelException;
import gc.GCSignal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import network.BadCommandException;
import network.NetworkUtil;

public abstract class Gadget<T> implements Callable<Object> {
	protected Object[] inputs;
	protected CompEnv<T> env;
	protected int machineId;
	protected InputStream[] peerIsUp;
	protected OutputStream[] peerOsUp;
	protected InputStream[] peerIsDown;
	protected OutputStream[] peerOsDown;

	abstract public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException;

	@Override
	public Object call() throws InterruptedException, IOException, BadCommandException, BadLabelException {
		// connect(port);
		Object ret = secureCompute();
		env.flush();
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
		} /* handle PM */
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
		} /* handle PM */
		// shouldn't happen;
		return null;
	}

	public void setInputs(Object[] inputs, 
			CompEnv<T> env,
			int machineId, 
			InputStream[] peerIsUp,
			OutputStream[] peerOsUp,
			InputStream[] peerIsDown,
			OutputStream[] peerOsDown) {
		this.inputs = inputs;
		this.env = env;
		this.peerIsUp = peerIsUp;
		this.peerOsUp = peerOsUp;
		this.peerIsDown = peerIsDown;
		this.peerOsDown = peerOsDown;
		this.machineId = machineId;
	}
}