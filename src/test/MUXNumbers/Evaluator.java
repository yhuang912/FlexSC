package test.MUXNumbers;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

import gc.*;

public class Evaluator extends network.Client {

	boolean choice;
	int width;

	// Evaluator(boolean c, int w) {
	// choice = c;
	// width = w;
	// }

	public void run() {
		try {
			connect("localhost", 54321);
			Signal[] a = new Signal[width];
			Signal[] b = new Signal[width];
			Signal[] d = null;
			Signal c;
			GCEva eva = new GCEva(is, os);
			for (int i = 0; i < width; i++) {
				a[i] = eva.inputOfGen(false);
				b[i] = eva.inputOfGen(false);
			}
			c = eva.inputOfEva(choice);
			d = new CircuitLib(eva).mux(a, b, c);
			for (int i = 0; i < width; i++)
				eva.outputToGen(d[i]);
			os.flush();

			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Evaluator(int w, boolean c) throws Exception {
		width = w;
		choice = c;
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new GnuParser();

		Options ops = new Options();
		ops.addOption("c", true, "");
		ops.addOption("w", "width", true, "");

		CommandLine line = parser.parse(ops, args);
		String s = line.getOptionValue("c");
		boolean c = (s==null) ? false : Boolean.parseBoolean(s);
		s = line.getOptionValue("w");
		int w = (s==null) ? 22 : Integer.parseInt(s);
		System.out.println("c: " + c + " w:" + w);
		Evaluator eva = new Evaluator(w, c);
		eva.run();
	}

}