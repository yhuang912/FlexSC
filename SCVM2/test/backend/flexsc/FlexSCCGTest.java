package backend.flexsc;

import java.io.FileNotFoundException;
import java.io.IOException;

import parser.ParseException;

public class FlexSCCGTest {

	public static void compile(String source) throws 
	IOException, ParseException {
		FlexSCCodeGenerator compiler = new FlexSCCodeGenerator(source);
//		System.out.println(compiler.function.toString());
//		TargetAST targetAST = compiler.compile(compiler.function);
//		System.out.println(targetAST.toString());
		compiler.FlexSCCodeGen(new Config(), true, false);
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
//		compile("input2/cpu.lcc"); // TESTED
//		compile("input2/stack.lcc"); // TESTED
//		compile("input2/simpleloop.lcc"); // TESTED
//		compile("input2/bug2.lcc"); // TESTED
//		compile("input2/oramstack.lcc"); // TESTED
//		compile("input2/pq.lcc"); // TESTED
//		compile("input2/ams_sketch.lcc"); // TESTED
//		compile("input2/oramstack.lcc"); // TESTED
//		compile("input2/priority_queue.lcc");
//		compile("input2/matrix.lcc");
//		compile("input2/avl.lcc"); // TESTED
//		compile("input2/avl_withoutstack.lcc"); // TESTED
		compile("input3/leadingZero.lcc"); // TESTED
//		compile("input2/mapreduce.lcc"); // TESTED
	}
}
