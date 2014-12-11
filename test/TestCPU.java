//package test;
import compiledlib.dov.*;

import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.sfe.common.Configuration;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
//import gc.Boolean;

public class TestCPU {

	static final int REGISTER_SIZE = 32;
	static final int MEM_SIZE = 72;// 2K words
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.VERIFY;
	static final int Alice_input = 10;
	static final int Bob_input = 3;
	int[] mem;
	Configuration config;
	 
	 
	
	public TestCPU(Configuration config) {
		this.config = config;
		mem = new int[MEM_SIZE];
		// http://www.mrc.uidaho.edu/mrc/people/jff/digital/MIPSir.html
		// 001001ssssstttttddddd00000100001 for ADDU s t -> d
		mem[0] = 0b00000000001000000001000000100001;
	}

	
	public boolean testTerminate(SecureArray<Boolean> reg, Boolean[] ins, IntegerLib<Boolean> lib) {
		Boolean eq = lib.eq(ins, lib.toSignals(0b00000011111000000000000000001000, 32));
		Boolean eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
		eq = lib.and(eq, eq2);
		return lib.declassifyToBoth(new Boolean[]{eq})[0]; 
	}
	public SecureArray<Boolean> getRegister(CompEnv<Boolean> env)
			throws Exception {

		// inital registers are all 0's. no need to set value.
		SecureArray<Boolean> oram = new SecureArray<Boolean>(env,
				REGISTER_SIZE, WORD_SIZE);

		// for testing purpose.
		// reg[4]=5 reg[5] = 6;
		oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(Alice_input, WORD_SIZE)));
		oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(Bob_input, WORD_SIZE)));
		env.flush();
		return oram;
	}

	public SecureArray<Boolean> getMemory(CompEnv<Boolean> env)
			throws Exception {
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		// inital registers are all 0's. no need to set value.
		SecureArray<Boolean> oram = new SecureArray<Boolean>(env, MEM_SIZE,
				WORD_SIZE);
		for (int i = 0; i < 2; ++i) {
			// index can be evaluated publicly.
			Boolean[] index = lib.toSignals(i, oram.lengthOfIden);

			Boolean[] data;
			if (env.getParty() == Party.Alice) {
				data = env.inputOfAlice(Utils.fromInt(mem[i], WORD_SIZE));
			} else {
				data = env.inputOfAlice(new boolean[WORD_SIZE]);
			}
			oram.write(index, data);
		}
		return oram;
	}
	public SecureArray<Boolean> getInstructionsGen(CompEnv<Boolean> env, DataSegment instData)
			throws Exception {
		boolean[][] instructions = null; 
		System.out.println("entering getInstructions");
		int numInst = instData.getDataLength();
		instructions = instData.getDataAsBoolean(); 
		
		//once we split the instruction from memory, remove the + MEMORY_SIZE
		SecureArray<Boolean> inst = new SecureArray<Boolean>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		Boolean[] data; 
		Boolean[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(instructions[i]);
			inst.write(index, data);
		}		
		System.out.println("exiting getInstructions");
		return inst;
	}							

	public SecureArray<Boolean> getInstructionsEva(CompEnv<Boolean> env, int numInst)
			throws Exception {
		SecureArray<Boolean> inst = new SecureArray<Boolean>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		Boolean[] data; 
		Boolean[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(new boolean[WORD_SIZE]);
			inst.write(index, data);
		}			
		return inst;
	}		
	
	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
	public SecureArray<Boolean> getMemoryGen(CompEnv<Boolean> env, DataSegment memData, SecureArray<Boolean> memBank, int numInst) throws Exception{
		System.out.println("entering getMemoryGen");
		boolean memory[][] = memData.getDataAsBoolean();	
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		//remove numInst when we separate instructions from memory. 
		for (int i = numInst; i < numInst + memData.getDataLength(); i++){
			memBank.write(lib.toSignals(i, memBank.lengthOfIden), env.inputOfAlice(memory[i-numInst]));
		}
		System.out.println("exiting getMemoryGen");
		return memBank;
	 
	}
	
	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead.
	public SecureArray<Boolean> getMemoryEva(CompEnv<Boolean> env, SecureArray<Boolean> memBank, int numInst, int dataLen)
			throws Exception {
		//after separating instruction, instantiate memBank 
	        //instead of passing it in. 
		//SecureArray<Boolean> memBank = new SecureArray<Boolean>(env, MEMORY_SIZE, WORD_SIZE);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		Boolean[] data; 
		Boolean[] index;

		for (int i = numInst; i < numInst+ dataLen ; i++){
			index = lib.toSignals(i, memBank.lengthOfIden);
			data = env.inputOfAlice(new boolean[WORD_SIZE]);
			memBank.write(index, data);
		}			
		return memBank;
	}	
	
	class GenRunnable extends network.Server implements Runnable {
		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);
				SecureArray<Boolean> reg = getRegister(env);
				Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
				SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				DataSegment memData = rdr.getData();
				SecureArray<Boolean> instructionBank = getInstructionsGen(env, inst);
				//instantiate new secure array for memory once we separate instructions from memory.
				instructionBank = getMemoryGen(env, memData, instructionBank, inst.getDataLength() );
				// is this cast ok?  Or should we modify the mem circuit? 
				int pcOffset = (int) ent.getAddress();
				System.out.println("pcoffset: " + pcOffset);
				int dataOffset = (int) rdr.getDataAddress();
				
				//Xiao's two lines
				
				//SecureArray<Boolean> memory = getMemory(env);
				
				//could this cast cause problems when msb is 1?
				Boolean[] pc = lib.toSignals(pcOffset, WORD_SIZE);
				Boolean[] newInst = lib.toSignals(0, WORD_SIZE);
				boolean testHalt;
				int count = 0; 
				printOramBank(instructionBank, lib, 60);
				while (true) {
					//change instructionBank to memBank once we separate 
					 
					System.out.println("count: " + count);
					count++;
					newInst = mem.func(reg, instructionBank, pc, newInst, pcOffset, dataOffset);
					testHalt = testTerminate(reg, newInst, lib);
					
					//System.out.println("Alice:"+count+" "+testHalt);
					if (testHalt)
						break;
										
					/*if (count ==16) {
						System.out.println("Too far :(");
						break;
					}*/
					System.out.println("newInst");
					printBooleanArray(newInst, lib);
					pc = cpu.function(reg, newInst, pc);
					printRegisters(reg, lib);
					System.out.println("PC: ");
					printBooleanArray(pc, lib);
					//lib.leftPublicShift(x, s)
					///=Xiao's code====
					//Boolean res = lib.eq(pc, lib.toSignals(100, pc.length));
					//boolean resb = env.outputToAlice(res);
					//====
				}

				//Xiao's reading of register value after computation. 
				//Boolean[] reg2 = reg.read(lib.toSignals(2, reg.lengthOfIden));
				//os.flush();
				//System.out.println(Utils.toInt(env.outputToAlice(reg2)));
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		public double andgates;
		public double encs;

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);

				SecureArray<Boolean> reg = getRegister(env);
//might be better to have bob send the number of instructions to alice.  That's the only reason 
				//we currently read the file at all. 
				Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
				//SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				int numInst = inst.getDataLength();
				DataSegment memData = rdr.getData();
				int dataLen = memData.getDataLength();
				
				SecureArray<Boolean> instructionBank = getInstructionsEva(env, numInst);
				//instantiate new secure array for memory once we separate instructions from memory.
				instructionBank = getMemoryEva(env, instructionBank, numInst, dataLen);
				
				//SecureArray<Boolean> memory = getMemory(env);
				Boolean[] newInst = lib.toSignals(0,WORD_SIZE);                           
				Boolean[] pc = lib.toSignals(0, WORD_SIZE);
				Boolean halt;
				boolean testHalt;
				printOramBank(instructionBank, lib, 60);
				
				if (m == Mode.COUNT) {
					Statistics sta = ((PMCompEnv) env).statistic;
					sta.flush();
				}
				int count = 0;
				while (true){
					newInst = mem.func(reg, instructionBank, pc, newInst, 0, 0);
					testHalt = testTerminate(reg, newInst, lib);

					os.flush();
					if (testHalt)
						break; 
					
					printBooleanArray(newInst, lib);
					pc = cpu.function(reg, newInst, pc);
					printRegisters(reg, lib);
					printBooleanArray(pc, lib);
				}
				
				if (m == Mode.COUNT) {
					Statistics sta = ((PMCompEnv) env).statistic;
					sta.finalize();
					andgates = sta.andGate;
					encs = sta.NumEncAlice;
				}
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	private static void process_cmdline_args(String[] args, Configuration config) {
		CmdLineParser parser = new CmdLineParser();

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		
		// Pick off file name, which should be remaining arg
		// (and currently only arg)
		// If no file name, will get from properties file.
		// This is probably an error, but allow it in the interest
		// of backwards compatibility.

		String rest[] = parser.getRemainingArgs();
		if(rest.length > 1) {
			printUsage();
			System.exit(2);
		}
		if(rest.length > 0) {
			config.setBinaryFileName(rest[0]);
		}
	}
	
	private static void printBooleanArray(Boolean[] array, IntegerLib<Boolean> lib){
		String output = "";
		
		for (int i = array.length -1 ; i >= 0;  i--){
			boolean[] temp = lib.getEnv().outputToAlice(array);
					output += temp[i] ? "1" : "0"; 
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println(output);
		
	}
	private static void printRegisters(SecureArray<Boolean> reg, IntegerLib<Boolean> lib){
		String output = "";
		Boolean[] temp; 

		for (int i = 0 ; i < 32; i++){
			output += "|reg" + i + ": ";
			temp = reg.read(lib.toSignals(i, reg.lengthOfIden));
			boolean[] tmp = lib.getEnv().outputToAlice(temp);
			//if (lib.getEnv().getParty() == Party.Alice)
				//System.out.println(Utils.toInt(tmp));
			for (int j = 31 ; j >= 0 ; j--){
				output += (tmp[j] ? "1" : "0");
			}	
			if (i % 3 == 0)
				output += "\n";
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println(output);
	}
	
	private static void printOramBank(SecureArray<Boolean> oramBank, IntegerLib<Boolean> lib, int numItems){
		String output = "";
		Boolean[] temp; 

		for (int i = 0 ; i < numItems; i++){
			output += "item number " + String.valueOf(i) +": ";
			temp = oramBank.read(lib.toSignals(i, oramBank.lengthOfIden));
			boolean[] tmp = lib.getEnv().outputToAlice(temp);
			//if (lib.getEnv().getParty() == Party.Alice)
				//System.out.println(Utils.toInt(tmp));
			for (int j = tmp.length-1 ; j >= 0 ; j--){
				output += (tmp[j] ? "1" : "0");
			}	
			output += "\n";
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println(output);
	}
	
	private static void printUsage() {
		System.out.println("Usage: java RunACSEmulatorServer [binary file]");
	}
	static public void main(String args[]) throws Exception {
	//	args = new String[1];
	//	args[0] = "/home/wangxiao/FlexSC/test/add";
		Configuration config = new Configuration();
		TestCPU test = new TestCPU(config);
		process_cmdline_args(args, config);
		GenRunnable gen = test.new GenRunnable();
		EvaRunnable env = test.new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		if (m == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {
		}

	}
}
