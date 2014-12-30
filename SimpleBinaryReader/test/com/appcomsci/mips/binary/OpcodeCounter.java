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
public class OpcodeCounter {
	private Configuration config;
	/**
	 * Constructor: Pick up configuration from command line arguments, with
	 * a properties file as backup.
	 * @param args
	 * @throws IOException
	 */
	public OpcodeCounter(String args[]) throws IOException, CmdLineParser.OptionException {
		// Create configuration object.  This reads the properties file.
		config = new Configuration();
		
		// Now parse command line arguments
		
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option.StringOption oE = new CmdLineParser.Option.StringOption('e', "entry.point");
		CmdLineParser.Option.StringOption oL = new CmdLineParser.Option.StringOption('l', "function.load.list");
		CmdLineParser.Option.StringOption oB = new CmdLineParser.Option.StringOption('b', "binary.reader.path");
		parser.addOption(oE);
		parser.addOption(oL);
		parser.addOption(oB);

		parser.parse(args);
		
		// Pick off file name, which should be remaining arg
		// (and currently only arg)
		// If no file name, will get from properties file.
		// This is probably an error.

		String rest[] = parser.getRemainingArgs();
		if(rest.length > 1 || rest.length == 0) {
			printUsage();
			System.exit(2);
		}
		if(rest.length > 0) {
			config.setBinaryFileName(rest[0]);
		}
		
		// Finally, pick off options
		
		Object o;
		if((o = parser.getOptionValue(oE)) != null)
			config.setEntryPoint((String)o);
		if((o = parser.getOptionValue(oL)) != null)
			config.setFunctionLoadList((String) o);
		if((o = parser.getOptionValue(oB)) != null)
			config.setBinaryFileName((String)o);
	}
	
	private static void printUsage() {
		System.err.println("Usage!");
	}
	
	/**
	 * Change the entry point used
	 * @param entryPoint The name of the new entry point
	 */
	public void setEntryPoint(String entryPoint) {
		synchronized(config) { // Probably unnecessary
			config.setEntryPoint(entryPoint);
		}
	}
	
	public String getEntryPoint() {
		return config.getEntryPoint();
	}
	
	/**
	 * Change the list of functions to be loaded.
	 * @param loadList The new (comma-separated) list of functions to be loaded.
	 */
	public void setFunctionLoadList(String loadList) {
		synchronized(config) {
			config.setFunctionLoadList(loadList);
		}
	}
	
	public List<String> getFunctionLoadList() {
		return config.getFunctionLoadList();
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
		
		rdr = new Reader(new File(config.getBinaryFileName()), config);
		ent = rdr.getSymbolTableEntry(config.getEntryPoint());	
		inst = rdr.getInstructions(config.getFunctionLoadList());
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
			printUsage();
			System.exit(2);
		}
		Collection<OpCount> opList =	c.countOps();
		for(OpCount oc:opList) {
			MipsInstructionSet.Operation opData = MipsInstructionSet.Operation.valueOf(oc.op);
			if(opData != null)
				System.out.println(opData.toString() + " [0x" + Long.toHexString(opData.getValue()) + "] " + oc.count);
			else
				System.out.println("0x" + Long.toHexString(oc.op) + " " + oc.count);
		}
	}

}
