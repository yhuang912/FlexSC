/**
 * 
 */
package com.appcomsci.mips.cpu;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.IOException;
import java.util.List;

import com.appcomsci.mips.memory.MemSetBuilder;
import com.appcomsci.mips.memory.MemSetBuilderException;
import com.appcomsci.mips.memory.MemorySet;
import com.appcomsci.mips.memory.MipsProgram;
import com.appcomsci.sfe.common.Configuration;

/**
 * Generate multiple CPUs
 * @author Allen McIntosh
 *
 */
public class CpuFactory extends MipsProgram {

	/**
	 * 
	 */
	public CpuFactory(Configuration config, String binaryFileName) {
		super(config, binaryFileName);
	}
	
	public CpuFactory(String args[]) throws IOException, CmdLineParser.OptionException {
		super(args);
	}
	
	public void build(List<MemorySet<Boolean>>sets) {
		
	}
	
	protected void printUsage() {
		printUsageStatic();
	}
	
	private static void printUsageStatic() {
		System.err.println("Usage!");
	}
	
	public static void main(String args[]) throws IOException, OptionException, IllegalArgumentException, MemSetBuilderException {
		CpuFactory factory = new CpuFactory(args);
		MemSetBuilder<Boolean> b = null;
		try {
			b = new MemSetBuilder<Boolean>(args);
		} catch(CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsageStatic();
			System.exit(2);
		}
		List<MemorySet<Boolean>> sets = b.build();
		factory.build(sets);
	}
}
