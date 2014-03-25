package test.MUXNumbers;

import static org.junit.Assert.*;

public class Test {

	public static void startJVM(Class<? extends Object> clazz, String args,
			boolean redirectStream) throws Exception {
		System.out.println(clazz.getCanonicalName());
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin"
				+ separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp",
				classpath, clazz.getCanonicalName(), args);
		processBuilder.redirectErrorStream(redirectStream);
		Process process = processBuilder.start();
//		process.waitFor();
		System.out.println("Fin");
	}

	@org.junit.Test
	public void testAllCases() throws Exception {
		System.out.println("Testing MUX-ing numbers...");
		
		int x = 0, y = 543, w = 22;
		boolean c = false;
		startJVM(Generator.class, String.format("-x %d -y %d -w %d", x, y, w), false);
	    startJVM(Evaluator.class, String.format("-c %b -w %d", c, w), false);
	}
}
