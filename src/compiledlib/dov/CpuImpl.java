/**
 * 
 */
package compiledlib.dov;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import flexsc.CompEnv;
import flexsc.CpuFcn;

/**
 * @author mcintosh
 *
 */
public class CpuImpl<T> extends CPU<T> implements CpuFcn<T> {
	public CpuImpl(CompEnv<T> env) throws Exception {
		super(env);
	}

	public static String opcodes[] = {
		"ADDIU",
		"ANDI",
		"LUI",
		"SLTI",
		"ADDU",
		"XOR",
		"SLT",
		"SUBU",
		"SRL",
		"SLL",
		"OR",
		"JAL",
		"BGEZAL",	// called BAL, value 1 which is all of REGIMM.
					// This is wrong.  FIXME
		"JAL",
		"JR",
		"BNE",
		"BEQ"
	};
	
	Set<String> opcodeSet = new HashSet<String>(Arrays.asList(opcodes));

	public Set<String> getOpcodesImplemented() {
		return opcodeSet;
	}
}
