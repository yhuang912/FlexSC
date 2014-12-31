/**
 * 
 */
package com.appcomsci.mips.binary;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.appcomsci.mips.memory.MipsInstructionSet;
import com.appcomsci.mips.memory.MipsProgram;
import com.appcomsci.sfe.common.Configuration;

import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BGEZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BLTZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_FUNCT;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_REGIMM;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_MASK;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_SHIFT;
import static com.appcomsci.mips.memory.MipsInstructionSet.FUNCT_MASK;
import static com.appcomsci.mips.memory.MipsInstructionSet.FUNCT_SHIFT;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_REGIMM_CODE_MASK;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_REGIMM_CODE_SHIFT;
import static com.appcomsci.mips.memory.MipsInstructionSet.getFunct;
import static com.appcomsci.mips.memory.MipsInstructionSet.getOp;
import static com.appcomsci.mips.memory.MipsInstructionSet.getRegImmCode;

/**
 * @author Allen McIntosh
 *
 */
public class OpcodeCounter extends MipsProgram {
	/**
	 * Constructor: Pick up configuration from command line arguments, with
	 * a properties file as backup.
	 * @param args
	 * @throws IOException
	 */
	public OpcodeCounter(String args[]) throws IOException, CmdLineParser.OptionException {
		super(args);
	}
	
	protected void printUsage() {
		printUsageStatic();
	}
	
	private static void printUsageStatic() {
		System.err.println("Usage!");
	}
	
	public class OpCount {
		Long op = 0L;
		int count = 0;
	}
	
	public Collection<OpCount> countOps() throws FileNotFoundException, IllegalArgumentException, IOException {
		Reader rdr;
		SymbolTableEntry ent;
		DataSegment inst;
		
		TreeMap<Long, OpCount> countTable = new TreeMap<Long, OpCount>();
		
		rdr = new Reader(new File(getBinaryFileName()), getConfiguration());
		ent = rdr.getSymbolTableEntry(getEntryPoint());	
		inst = rdr.getInstructions(getFunctionLoadList());
		for(long instr:inst.getData()) {
			long op = getOp(instr);
			long bits = instr & (OP_MASK << OP_SHIFT);
			if(op == OP_FUNCT) {
				bits |= instr & (FUNCT_MASK << FUNCT_SHIFT);
			} else if(op == OP_REGIMM) {
				bits |= instr & (OP_REGIMM_CODE_MASK << OP_REGIMM_CODE_SHIFT);
			}
			OpCount x = countTable.get(bits);
			if(x == null) {
				x = new OpCount();
				x.op = bits;
				countTable.put(bits, x);
			}
			x.count += 1;
		}
		return countTable.values();
	}
	
	public static void main(String args[]) throws IOException {
		OpcodeCounter c = null;
		try {
			c = new OpcodeCounter(args);
		} catch(CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsageStatic();
			System.exit(2);
		}
		Collection<OpCount> opList =	c.countOps();
		for(OpCount oc:opList) {
			MipsInstructionSet.Operation opData = MipsInstructionSet.Operation.valueOf(oc.op);
			if(opData != null)
				System.out.printf("%s [0x%08x] %d\n", opData.toString(), opData.getValue(), oc.count);
			else
				System.out.printf("[0x%08x] %d\n", oc.op, oc.count);
		}
	}

}
