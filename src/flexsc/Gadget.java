package flexsc;

import gc.BadLabelException;
import gc.GCSignal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;

public abstract class Gadget<T> extends Machine implements Callable<Object> {
	protected Object[] inputs;
	protected CompEnv<T> env;
	private int port;

	abstract public void secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException;

	@Override
	public Object call() throws InterruptedException, IOException, BadCommandException, BadLabelException {
		connect(port);
		secureCompute();
		env.flush();
		disconnect();
		return null;
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

	void setInputs(Object[] inputs, CompEnv<T> env, int port) {
		this.port = port;
		this.inputs = inputs;
		this.env = env;
	}
}