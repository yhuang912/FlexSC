/**
 * 
 */
package com.appcomsci.mips.cpu;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.OptionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import backend.flexsc.Config;
import backend.flexsc.FlexSCCodeGenerator;

import com.appcomsci.mips.memory.MemSetBuilder;
import com.appcomsci.mips.memory.MemSetBuilderException;
import com.appcomsci.mips.memory.MemorySet;
import com.appcomsci.mips.memory.MipsInstructionSet;
import com.appcomsci.mips.memory.MipsProgram;
import com.appcomsci.sfe.common.Configuration;

import compiledlib.dov.CpuImpl;
import flexsc.CpuFcn;

/**
 * Generate multiple CPUs
 * @author Allen McIntosh
 *
 */
public class CpuFactory extends MipsProgram {

	private static final String fileSeparator = System.getProperty("file.separator");
	
	private String classDirectory;
	/**
	 * @throws Exception 
	 * 
	 */
	public CpuFactory(Configuration config, String binaryFileName) throws Exception {
		super(config, binaryFileName);
		init();
	}
	
	public CpuFactory(String args[]) throws Exception {
		super(args);
		init();
	}
	
	private void init() throws Exception {
		classDirectory = getConfiguration().getOutputDirectory();
		if(classDirectory == null)
			throw new Exception("No output directory given");
		classDirectory += fileSeparator + getConfiguration().getPackageName().replace(".", fileSeparator);
		Files.createDirectories(FileSystems.getDefault().getPath(classDirectory));
	}

	public void build(List<MemorySet<Boolean>>sets) throws Exception {
		String packageName = getConfiguration().getPackageName();
		String classNameRoot = getConfiguration().getClassNameRoot();
		Set<Set<MipsInstructionSet.Operation>>cpuSets = new HashSet<Set<MipsInstructionSet.Operation>>();
		for(MemorySet<Boolean>ms:sets) {
			if(Utils.findCpu(ms, null, packageName, classNameRoot, true) == null) {
				Set<MipsInstructionSet.Operation>instructions = Utils.makeInstructionSet(ms);
				cpuSets.add(instructions);
			}
		}
		if(cpuSets.size() == 0) {
			System.out.println("Found all required CPUs.  No CPUs to build.");
			return;
		}
		CpuBuilder cb = new CpuBuilder();
		for(Set<MipsInstructionSet.Operation>instructions : cpuSets) {
			TreeSet<String>opNames = Utils.toStringSet(instructions);
			int hash = Utils.consistentHash(opNames);
			String hashPart = "_" + String.format("%08x", hash);
			String className = classNameRoot + hashPart;
			String wrapperClassName = className + "Impl";
			String fullWrapperClassName = packageName + "." + wrapperClassName;
			
			System.out.print("Building cpu " + className);
			System.out.println(Utils.consistentHashString(opNames));
			File cppFile = new File(classDirectory + fileSeparator + className + ".cpp");
			if(cppFile.exists()) {
				System.err.println("Will not overwrite " + cppFile.getPath());
				// throw new Exception("File " + cppFile.getPath() + " already exists");
				continue;
			}
			File cpuFile = new File(classDirectory + fileSeparator + className + ".java");
			if(cpuFile.exists()) {
				System.err.println("Will not overwrite " + cpuFile.getPath());
				// throw new Exception("File " + cpuFIle.getPath() + " already exists");
				continue;
			}
				
			File wrapperFile = new File(classDirectory + fileSeparator + wrapperClassName + ".java");
			if(wrapperFile.exists()) {
				System.err.println("Will not overwrite " + wrapperFile.getPath());
				// throw new Exception("File " + wrapperFile.getPath() + " already exists");
				continue;
			}
			cb.buildCpu(instructions, packageName, className, cppFile);
			cb.buildWrapper(instructions, packageName, className, wrapperFile);
			FlexSCCodeGenerator compiler = new FlexSCCodeGenerator(cppFile.getAbsolutePath());
			Config cfg = new Config();
			cfg.path = classDirectory;
			cfg.packageName = packageName;
			compiler.FlexSCCodeGen(cfg, true, false);
		}
	}
	
	protected void printUsage() {
		printUsageStatic();
	}
	
	private static void printUsageStatic() {
		System.err.println("Usage!");
	}
	
	public static void main(String args[]) throws Exception {
		CpuFactory factory = new CpuFactory(args);
		MemSetBuilder<Boolean> b = new MemSetBuilder<Boolean>(factory.getConfiguration(), factory.getBinaryFileName());
		List<MemorySet<Boolean>> sets = b.build();
		factory.build(sets);
	}
}
