package backend.flexsc;


public class Config {
	public static String packageName = "compiledlib.libs";
	public static String[] importPackage = new String[]{
		"java.security.SecureRandom",
		"oram.SecureArray",
		"oram.CircuitOram",
		"flexsc.Mode",
		"flexsc.Party",
		"flexsc.CompEnv",
		"java.util.BitSet", 
		"circuits.arithmetic.IntegerLib",
//		"circuits.IntegerLib",
		"circuits.arithmetic.FloatLib",
		"util.Utils",
		"gc.regular.GCEva",
		"gc.regular.GCGen",
//		"gc.GCEva",
//		"gc.GCGen",
		"gc.GCSignal",
		"java.util.Arrays",
		"java.util.Random",
//		"flexsc.IWritable",
		"flexsc.Comparator",
		"java.lang.reflect.Array"};

	public static String implementedInterface = "flexsc.IWritable";

	public static String path = "../../git/FlexSC/src/"+packageName.replace(".", "/");
	
	public static boolean useTemplate = true;
	
	public static String[] types = new String[] {
		"CompEnv<@dataType>"
		,"IntegerLib<@dataType>"
		,"FloatLib<@dataType>"
		,};
	
	public static String phantomVariable = "__isPhantom";
	
	public static String[] fields = new String[] {
		"env"
		,"intLib"
		,"floatLib"
	};
}
