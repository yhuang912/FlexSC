package flexsc;

import gc.BadLabelException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import network.BadCommandException;
import network.Machine;

public abstract class Gadget<T> {
	protected CompEnv<T> env;
	protected Machine machine;

	abstract public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException;

	public Object compute() throws InterruptedException, IOException, BadCommandException, BadLabelException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
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

	public Gadget(CompEnv<T> env, 
			Machine machine) {
		this.env = env;
		this.machine = machine;
	}
}