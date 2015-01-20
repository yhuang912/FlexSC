package mips;

import compiledlib.dov.*;
import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.mips.memory.MemSetBuilder;
import com.appcomsci.mips.memory.MemorySet;
import com.appcomsci.sfe.common.Configuration;

import flexsc.CVCompEnv;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.*;
import jargs.gnu.CmdLineParser;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import gc.GCSignal;

public class MipsEmulatorSC {

	static final int REGISTER_SIZE = 32;
	static final int MEM_SIZE = 72;// 160 < threshold for func1
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.REAL;
	static final int Alice_input = 5;
	static final int Bob_input = 2;
	static final boolean MULTIPLE_BANKS = true;
	int[] mem;
	Configuration config;
	private static String binaryFileName;	// should not be static FIXME
	 
	List<MemorySet<GCSignal>> sets;
	DataSegment instData; 
	DataSegment memData;
	int pcOffset; 
	int dataOffset; 
	
	public MipsEmulatorSC(Configuration config) {
		this.config = config;
		mem = new int[MEM_SIZE];
		// http://www.mrc.uidaho.edu/mrc/people/jff/digital/MIPSir.html
	}

	
	public boolean testTerminate(SecureArray<GCSignal> reg, GCSignal[] ins, IntegerLib<GCSignal> lib) {
		GCSignal eq = lib.eq(ins, lib.toSignals(0b00000011111000000000000000001000, 32));
		GCSignal eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
		eq = lib.and(eq, eq2);
		return lib.declassifyToBoth(new GCSignal[]{eq})[0]; 
	}
	
	public void testInstruction (CompEnv<GCSignal> env) throws Exception {
		
		SecureArray<GCSignal> reg = new SecureArray<GCSignal>(env, REGISTER_SIZE, WORD_SIZE);
		//int inst = 		0b00000000000000110001011011000010; //SRL
		int inst = 		0b00000000100100111001100000100101; //OR
		int rsCont = 	0b00000000000000000000000000000101;
		int rtCont = 	0b00000000000000000000000000011001;
		//int rdCont = 	0b00000000000000000000000000000000;
		GCSignal[] rs = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		GCSignal[] rt = env.inputOfAlice(Utils.fromInt(19, reg.lengthOfIden));
		//GCSignal[] rd = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		GCSignal[] rsContent = env.inputOfAlice(Utils.fromInt(rsCont, WORD_SIZE));
		GCSignal[] rtContent = env.inputOfAlice(Utils.fromInt(rtCont, WORD_SIZE));
		//GCSignal[] rdContent = env.inputOfAlice(Utils.fromInt(rdCont, WORD_SIZE));
		reg.write(rs, rsContent);
		reg.write(rt, rtContent);
		//reg.write(rd, rdContent);
		env.flush();
		
		CPU cpu = new CPU(env);
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] pc; 
		pc = cpu.function(reg, env.inputOfAlice(Utils.fromInt(inst, 32)), env.inputOfAlice(Utils.fromInt(0,32)));
		
		String output = "";
		for (int i = 31 ; i >= 26;  i--){
			if ((inst & (1 << i)) != 0)
				output += "1";
			else 
				output += "0";
		}
		output += "|";
		for (int i = 25 ; i >= 21;  i--){
			if ((inst & (1 << i)) != 0)
				output += "1";
			else 
				output += "0";
		}
		output += "|";
		for (int i = 20 ; i >= 16;  i--){
			if ((inst & (1 << i)) != 0)
				output += "1";
			else 
				output += "0";
		}
		output += "|";
		for (int i = 15 ; i >= 11;  i--){
			if ((inst & (1 << i)) != 0)
				output += "1";
			else 
				output += "0";
		}
		output += "|";
		for (int i = 10 ; i >= 0;  i--){
			if ((inst & (1 << i)) != 0)
				output += "1";
			else 
				output += "0";
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println("testing instruction: " + output);
		printRegisters(reg, lib);	
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println("PC: ");
		printGCSignalArray(pc, lib);
		
	}
	public SecureArray<GCSignal> loadInputsToRegister(CompEnv<GCSignal> env)
			throws Exception {

		// inital registers are all 0's. no need to set value.
		SecureArray<GCSignal> oram = new SecureArray<GCSignal>(env,
				REGISTER_SIZE, WORD_SIZE);
		for(int i = 0; i < REGISTER_SIZE; ++i)
			oram.write(env.inputOfAlice(Utils.fromInt(i, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(0, WORD_SIZE)));
		// for testing purpose.
		// reg[4]=5 reg[5] = 6;
		oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(Alice_input, WORD_SIZE)));
		oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(Bob_input, WORD_SIZE)));
		env.flush();
		return oram;
	}

	
	public SecureArray<GCSignal> loadInstructionsSingleBank(CompEnv<GCSignal> env)
			throws Exception {
		boolean[][] instructions = null; 
		System.out.println("entering getInstructions");
		int numInst = this.instData.getDataLength();
		instructions = this.instData.getDataAsBoolean(); 
		
		//once we split the instruction from memory, remove the + MEMORY_SIZE
		SecureArray<GCSignal> instBank = new SecureArray<GCSignal>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] data; 
		GCSignal[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, instBank.lengthOfIden);
			if (env.getParty() == Party.Alice)
				data = env.inputOfAlice(instructions[i]);
			else 
				data = env.inputOfAlice(new boolean[WORD_SIZE]);
			instBank.write(index, data);
			//System.out.println("Wrote instruction number "+i);
		}		
		System.out.println("exiting getInstructions");
		return instBank;
	}			
	
	public List<MemorySet<GCSignal>> loadInstructionsMultiBanks(CompEnv<GCSignal> env, SecureArray<GCSignal> singleBank) throws Exception {
		System.out.println("entering loadInstructions");
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] data; 
		GCSignal[] index;
		SecureArray<GCSignal> instructionBank;

		for(MemorySet<GCSignal> s:this.sets) {
	        int i = s.getExecutionStep();
	        System.out.println(i);
	        TreeMap<Long,boolean[]> m = s.getAddressMap(this.instData);	  
	        long maxAddr = m.lastEntry().getKey();
	        if (maxAddr == 0)
	        	break;
	        //long minAddr = m.firstEntry().getKey();
	        long minAddr = m.ceilingKey((long)1);
			if (!MULTIPLE_BANKS)
				instructionBank = singleBank;
			else {
				instructionBank = new SecureArray<GCSignal>(env, (int)((maxAddr - minAddr)/4 + 1), WORD_SIZE);
				int count = 0;
				for( Map.Entry<Long, boolean[]> entry : m.entrySet()) {
					if (entry.getKey() > 0){
						index = lib.toSignals((int)((entry.getKey() - minAddr)/4), instructionBank.lengthOfIden);
						if (env.getParty() == Party.Alice){
							data = env.inputOfAlice(entry.getValue());
						}
						else	 { 
							data = env.inputOfAlice(new boolean[WORD_SIZE]); 

						}
						// once the indices are correct, write here. 
						instructionBank.write(index, data);
					}
					//printOramBank(instructionBank, lib, (int)((maxAddr - minAddr)/4 + 1));
					//System.out.println(maxAddr +" "+ minAddr);
					count++;
					//System.out.println(count);
				}	
			}
			OramBank<GCSignal> bank = new OramBank<GCSignal>(instructionBank);
			bank.setMaxAddress(maxAddr);
			bank.setMinAddress(minAddr);
			s.setOramBank(bank);
		}		
		System.out.println("exiting getInstructions");
		return sets;
	}
	
	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
	public SecureArray<GCSignal> getMemory(CompEnv<GCSignal> env) throws Exception{
		System.out.println("entering getMemoryGen");
		boolean memory[][] = memData.getDataAsBoolean();	
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		SecureArray<GCSignal> memBank = new SecureArray<GCSignal>(env, MEM_SIZE, WORD_SIZE);
		GCSignal[] index; 
		GCSignal[] data;
		for (int i = 0; i < memData.getDataLength(); i++){
			index = lib.toSignals(i, memBank.lengthOfIden);
			if (env.getParty() == Party.Alice)
				data = env.inputOfAlice(memory[i]);
			else 
				data = env.inputOfAlice(new boolean[WORD_SIZE]);
			memBank.write(index, data);	
		}
		System.out.println("exiting getMemoryGen");
		return memBank;
	 
	}
	
	class GenRunnable extends network.Server implements Runnable {
		
		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Alice, is, os);
				//testInstruction(env);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);
				SecureArray<GCSignal> reg = loadInputsToRegister(env);
				
				SecureArray<GCSignal> singleInstructionBank = null;
				
				if (!MULTIPLE_BANKS){
					singleInstructionBank = loadInstructionsSingleBank(env);					
				}
				loadInstructionsMultiBanks(env, singleInstructionBank);
				SecureArray<GCSignal> memBank = getMemory(env);
				
				GCSignal[] pc = lib.toSignals(pcOffset, WORD_SIZE);
				GCSignal[] newInst = lib.toSignals(0, WORD_SIZE);
				boolean testHalt;
				int count = 0; 
				long startTime = System.nanoTime();
				MemorySet<GCSignal> currentSet = sets.get(0);
				SecureArray<GCSignal> currentBank;
				while (true) {
					currentBank = currentSet.getOramBank().getArray();
//					System.out.println("count: " + count);
//					count++;
//					System.out.println("execution step: " + currentSet.getExecutionStep());
//					printOramBank(currentSet.getOramBank().getArray(), lib, currentSet.getOramBank().getBankSize());
					if (MULTIPLE_BANKS)
						pcOffset = (int) currentSet.getOramBank().getMinAddress();
					newInst = mem.getInst(currentBank, pc, pcOffset);
					//newInst = mem.getInst(singleInstructionBank, pc, pcOffset); 
					mem.func(reg, memBank, newInst, dataOffset);
					
					//System.out.println("newInst");
					//printGCSignalArray(newInst, lib);
					
					testHalt = testTerminate(reg, newInst, lib);
					if (testHalt)
						break;
									
					
					//if (checkMatchGCSignalArray(newInst, lib, 0b10001111110000110000000000101000))
						//newInst = env.inputOfAlice(Utils.fromInt(0b10000011110000110000000000101001, 32));
					pc = cpu.function(reg, newInst, pc);
					
					//printRegisters(reg, lib);
					//System.out.println("PC: ");
					//printGCSignalArray(pc, lib);
					//System.out.println(pcOffset);
					//System.out.println(currentSet.getOramBank().getMinAddress());
					
					currentSet = currentSet.getNextMemorySet();
				}
				float runTime =  ((float)(System.nanoTime() - startTime))/ 1000000000;
				System.out.println("Run time: " + runTime);
				System.out.println("Average time / instruction: " + runTime / count );
				GCSignal[] output = reg.read(lib.toSignals(2, reg.lengthOfIden));
				String outputStr = "";
				boolean[] tmp = lib.getEnv().outputToAlice(output);
				for (int j = 31 ; j >= 0 ; j--){
					outputStr += (tmp[j] ? "1" : "0");
				}	
				System.out.println("Output: " + outputStr);
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
				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Bob, is, os);
				//testInstruction(env);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);

				SecureArray<GCSignal> reg = loadInputsToRegister(env);
				SecureArray<GCSignal> singleInstructionBank = null; 
				if (!MULTIPLE_BANKS)
					singleInstructionBank = loadInstructionsSingleBank(env);
				loadInstructionsMultiBanks(env, singleInstructionBank);
				SecureArray<GCSignal> memBank = getMemory(env);
				//instantiate new secure array for memory once we separate instructions from memory.
					
				GCSignal[] newInst = lib.toSignals(0,WORD_SIZE);                           
				GCSignal[] pc = lib.toSignals(0, WORD_SIZE);
				boolean testHalt;
								
				if (m == Mode.COUNT) {
					//Statistics sta = ((PMCompEnv) env).statistic;
					//sta.flush();
				}
				
				MemorySet<GCSignal> currentSet = sets.get(0);
				SecureArray<GCSignal> currentBank;
				while (true){
					currentBank = currentSet.getOramBank().getArray();
					//printOramBank(currentSet.getOramBank().getArray(), lib, currentSet.getOramBank().getBankSize());
					newInst = mem.getInst(currentBank, pc, 0); 
					mem.func(reg, memBank, newInst, 0);
					testHalt = testTerminate(reg, newInst, lib);

					os.flush();
					if (testHalt)
						break; 
					
					//printGCSignalArray(newInst, lib);
					//if (checkMatchGCSignalArray(newInst, lib, 0))
						//newInst = env.inputOfAlice(new boolean[32]);
						
					//int andCount = ((CVCompEnv)env).numOfAnds;
					pc = cpu.function(reg, newInst, pc);
					//System.out.println( "CPU circuit size:" + (((CVCompEnv)env).numOfAnds-andCount));
					//printRegisters(reg, lib);
					//printGCSignalArray(pc, lib);
					currentSet = currentSet.getNextMemorySet();

				}
				
				if (m == Mode.COUNT) {
					//Statistics sta = ((PMCompEnv) env).statistic;
					//sta.finalize();
					//andgates = sta.andGate;
					//encs = sta.NumEncAlice;
				}
				GCSignal[] output = reg.read(lib.toSignals(2, reg.lengthOfIden));
				lib.getEnv().outputToAlice(output);
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
		setBinaryFileName(rest[0]);
	}
	
	private static boolean checkMatchGCSignalArray(GCSignal[] array, IntegerLib<GCSignal> lib, int matchVal) throws Exception{
		boolean[] temp = lib.getEnv().outputToAlice(array);
		boolean match = true;
		if (lib.getEnv().getParty() == Party.Alice){
			for (int i = 31; i >=0; i--){
				if (!temp[i] && ((matchVal & (1 << i)) != 0))
					match = false;
				else if (temp[i] && ((matchVal & (1 << i)) == 0))
					match = false;
			}
			//System.out.println("Alice Match = " + match);
			lib.getEnv().os.write(match ? 1 : 0);
		}
		else{
			match = (lib.getEnv().is.read() == 1);
			//System.out.println("Bob Match: " + match);
		}return match;
	}
	private static void printGCSignalArray(GCSignal[] array, IntegerLib<GCSignal> lib){
		String output = "";
		boolean[] temp = lib.getEnv().outputToAlice(array);

		for (int i = array.length -1 ; i >= 0;  i--){
					output += temp[i] ? "1" : "0"; 
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println(output);
		
	}
	private static void printRegisters(SecureArray<GCSignal> reg, IntegerLib<GCSignal> lib){
		String output = "";
		GCSignal[] temp; 

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
	
	private static void printOramBank(SecureArray<GCSignal> oramBank, IntegerLib<GCSignal> lib, int numItems){
		String output = "";
		GCSignal[] temp; 
		
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

        public static void setBinaryFileName(String fileName) {
                binaryFileName = fileName;
        }

        public static String getBinaryFileName() {
                return binaryFileName;
        }

	static public void main(String args[]) throws Exception {
		Configuration config = new Configuration();
		MipsEmulatorSC emulator = new MipsEmulatorSC(config);
		process_cmdline_args(args, config);
		Reader rdr = new Reader(new File(getBinaryFileName()), config);
		SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
		emulator.instData = rdr.getInstructions(config.getFunctionLoadList());
		emulator.memData = rdr.getData();
		// is this cast ok?  Or should we modify the mem circuit? 
		emulator.pcOffset = (int) ent.getAddress();
		System.out.println("pcoffset: " + emulator.pcOffset);
		emulator.dataOffset = (int) rdr.getDataAddress();
		MemSetBuilder<GCSignal> b = new MemSetBuilder<GCSignal>(config, binaryFileName);
	    emulator.sets = b.build();
		GenRunnable gen = emulator.new GenRunnable();
		EvaRunnable env = emulator.new EvaRunnable();
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
