package test.MUXNumbers;

import org.apache.commons.cli.*;

import circuits.CircuitLib;
import gc.*;
import test.Utils;

public class Generator extends network.Server {
	boolean[] x, y, z;

	public void run() {
		try {
			listen(54321);
			Signal[] a = new Signal[x.length];
			Signal[] b = new Signal[y.length];
			Signal[] d = null;
			Signal c;
			GCGen gen = new GCGen(is, os);
			for (int i = 0; i < x.length; i++) {
				a[i] = gen.inputOfGen(x[i]);
				b[i] = gen.inputOfGen(y[i]);
			}
			c = gen.inputOfEva(false);
			d = new CircuitLib(gen).mux(a, b, c);
			os.flush();

			z = new boolean[x.length];
			for (int i = 0; i < x.length; i++)
				z[i] = gen.outputToGen(d[i]);

			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}

	public Generator(int a, int b, int w) throws Exception {
		x = Utils.fromInt(a, w);
		y = Utils.fromInt(b, w);
	}
	
	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new GnuParser();
		
		Options ops = new Options();
		ops.addOption("x", true, "");
		ops.addOption("y", true, "");
		ops.addOption("w", "width", true, "");
		
		CommandLine line = parser.parse(ops, args);
		String s = line.getOptionValue("x");
		int x = (s==null) ? 0 : Integer.parseInt(s);
		s = line.getOptionValue("y");
		int y = (s==null) ? 5432 : Integer.parseInt(s);
		s = line.getOptionValue("w");
		int w = (s==null) ? 22 : Integer.parseInt(s);
		
		System.out.println("x: " + x + " y:"+ y + " w:" + w);
		Generator gen = new Generator(x, y, w);
		gen.run();
	}
}