	package backend.flexsc;

import java.io.FileNotFoundException;
import java.io.IOException;

import parser.ParseException;

public class CommandTool {

	public static void compile(String source, boolean count) throws 
	IOException, ParseException {
		FlexSCCodeGenerator compiler = new FlexSCCodeGenerator(source);
//		System.out.println(compiler.function.toString());
//		TargetAST targetAST = compiler.compile(compiler.function);
//		System.out.println(targetAST.toString());
		compiler.FlexSCCodeGen(new Config(), count, false);
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		Config.packageName = args[1];
		String path = args[1].replace(".", "/");
		if(args.length > 3)
			Config.path = args[3] + "/" + path;
		else
			Config.path = "/Users/wangxiao/git/FlexSC_rc/src/"+ path;
		if(args.length > 4)
			Config.implementedInterface = args[4];
		if(args.length > 2) {
			if(args[2].equalsIgnoreCase("true"))
				compile(args[0], true);
			else
				compile(args[0], false);
		}
		else compile(args[0], true);
	}
}
