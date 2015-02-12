package mips;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import gc.Boolean;



import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.mips.memory.MemSetBuilder;
import com.appcomsci.mips.memory.MemorySet;
import com.appcomsci.mips.memory.MipsInstructionSet;
import com.appcomsci.sfe.common.Configuration;

import compiledlib.dov.CPU;
import compiledlib.dov.MEM;
import mips.EmulatorUtils;
import flexsc.CompEnv;
// NEW import flexsc.CpuFcn;
import flexsc.Mode;
import flexsc.Party;

public class MipsEmulator {

	static final int REGISTER_SIZE = 32;
	static final int MEM_SIZE = 72;// 160 < threshold for func1
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.VERIFY;
	static final int Alice_input = 5;
	static final int Bob_input = 2;
	// static final boolean MULTIPLE_BANKS = true;
	static Configuration config;
	private static String binaryFileName;	// should not be static FIXME

	public MipsEmulator(Configuration config) {
		this.config = config;
	}

	public static <T> boolean testTerminate(SecureArray<T> reg, T[] ins, IntegerLib<T> lib) {
		T eq = lib.eq(ins, lib.toSignals(0b00000011111000000000000000001000, 32));
		T eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
		eq = lib.and(eq, eq2);
		T[] res = lib.getEnv().newTArray(1);
		res[0] = eq;
		return lib.declassifyToBoth(res)[0]; 
	}

	public static <T> void testInstruction (CompEnv<T> env) throws Exception {

		SecureArray<T> reg = new SecureArray<T>(env, REGISTER_SIZE, WORD_SIZE);
		//int inst = 		0b00000000000000110001011011000010; //SRL
		int inst = 		0b00000000100100111001100000100101; //OR
		int rsCont = 	0b00000000000000000000000000000101;
		int rtCont = 	0b00000000000000000000000000011001;
		//int rdCont = 	0b00000000000000000000000000000000;
		T[] rs = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		T[] rt = env.inputOfAlice(Utils.fromInt(19, reg.lengthOfIden));
		//Boolean[] rd = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		T[] rsContent = env.inputOfAlice(Utils.fromInt(rsCont, WORD_SIZE));
		T[] rtContent = env.inputOfAlice(Utils.fromInt(rtCont, WORD_SIZE));
		//Boolean[] rdContent = env.inputOfAlice(Utils.fromInt(rdCont, WORD_SIZE));
		reg.write(rs, rsContent);
		reg.write(rt, rtContent);
		//reg.write(rd, rdContent);
		env.flush();

		CPU<T> cpu = new CPU<T>(env);
		IntegerLib<T> lib = new IntegerLib<T>(env);
		T[] pc; 
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
		EmulatorUtils.printRegisters(reg, lib);	
		if(lib.getEnv().getParty() == Party.Alice)
			EmulatorUtils.printBooleanArray("PC", pc, lib);
	}

	public static <T> SecureArray<T> loadInputsToRegister(CompEnv<T> env)
			throws Exception {

		// inital registers are all 0's. no need to set value.
		SecureArray<T> oram = new SecureArray<T>(env,
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


	public static<T> SecureArray<T> loadInstructionsSingleBank(CompEnv<T> env, DataSegment instData)
			throws Exception {
		boolean[][] instructions = null; 
		System.out.println("entering getInstructions");
		int numInst = instData.getDataLength();
		instructions = instData.getDataAsBoolean(); 

		//once we split the instruction from memory, remove the + MEMORY_SIZE
		SecureArray<T> instBank = new SecureArray<T>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<T> lib = new IntegerLib<T>(env);
		T[] data; 
		T[] index;

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

	public static <T> void loadInstructionsMultiBanks(CompEnv<T> env, SecureArray<T> singleBank, List<MemorySet<T>> sets, DataSegment instData) throws Exception {
		System.out.println("entering loadInstructions");
		IntegerLib<T> lib = new IntegerLib<T>(env);
		T[] data; 
		T[] index;
		SecureArray<T> instructionBank;

		for(MemorySet<T> s:sets) {
			int i = s.getExecutionStep();

			EmulatorUtils.print("step: " + i + " size: " + s.size(), lib);

			TreeMap<Long,boolean[]> m = s.getAddressMap(instData);	  
			long maxAddr = m.lastEntry().getKey();
			if (maxAddr == 0)
				break;
			//long minAddr = m.firstEntry().getKey();
			long minAddr = m.ceilingKey((long)1);
			if (!config.isMultipleBanks())
				instructionBank = singleBank;
			else {
				instructionBank = new SecureArray<T>(env, (int)((maxAddr - minAddr)/4 + 1), WORD_SIZE);
				int count = 0;
				for( Map.Entry<Long, boolean[]> entry : m.entrySet()) {
					if (env.getParty() == Party.Alice) {
						EmulatorUtils.print("count: " + count + " key: " + entry.getKey() +
								" (0x" + Long.toHexString(entry.getKey()) + ")" +
								" value: " , lib);
						String output = "";
						for (int j = 31 ; j >= 0;  j--){
							if (entry.getValue()[j])
								output += "1";
							else 
								output += "0";
						}
						EmulatorUtils.print(output, lib);
					}

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
					EmulatorUtils.printOramBank(instructionBank, lib, (int)((maxAddr - minAddr)/4 + 1));
					count++;
				}	
			}
			OramBank<T> bank = new OramBank<T>(instructionBank);
			bank.setMaxAddress(maxAddr);
			bank.setMinAddress(minAddr);
			s.setOramBank(bank);
		}		
		System.out.println("exiting getInstructions");
	}

	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
	public static <T> SecureArray<T> getMemory(CompEnv<T> env, DataSegment memData) throws Exception{
		System.out.println("entering getMemoryGen");
		boolean memory[][] = memData.getDataAsBoolean();	
		IntegerLib<T> lib = new IntegerLib<T>(env);
		SecureArray<T> memBank = new SecureArray<T>(env, MEM_SIZE, WORD_SIZE);
		T[] index; 
		T[] data;
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


	static class MipsParty<T> {
		public double andgates;
		public double encs;
		List<MemorySet<T>> sets;
		DataSegment instData; 
		DataSegment memData;
		int pcOffset; 
		int dataOffset; 
		IntegerLib<T> lib;
		public MipsParty(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			this.sets = sets;
			this.instData = instData;
			this.memData = memData;
			this.pcOffset = pcOffset;
			this.dataOffset = dataOffset;
		}
		public SecureArray<T> reg;
		public void mainloop(CompEnv<T> env) throws Exception{
			//testInstruction(env);
			lib = new IntegerLib<T>(env);
			CPU<T> cpu = new CPU<T>(env);
// NEW			CpuFcn<T> cpu = new CPU<T>(env);
			MEM<T> mem = new MEM<T>(env);
			reg = loadInputsToRegister(env);

			SecureArray<T> singleInstructionBank = null;

			if (!config.isMultipleBanks()){
				singleInstructionBank = loadInstructionsSingleBank(env, instData);				
			}
			loadInstructionsMultiBanks(env, singleInstructionBank, sets, instData);
			SecureArray<T> memBank = getMemory(env, memData);

			T[] pc = lib.toSignals(pcOffset, WORD_SIZE);
			T[] newInst = lib.toSignals(0, WORD_SIZE);
			boolean testHalt;
			int count = 0; 
			if (!config.isMultipleBanks())
				EmulatorUtils.printOramBank(singleInstructionBank, lib, 60);
			long startTime = System.nanoTime();
			MemorySet<T> currentSet = sets.get(0);
			SecureArray<T> currentBank;
			while (true) {
				currentBank = currentSet.getOramBank().getArray();
				EmulatorUtils.print("count: " + count, lib, false);
				count++;
				//				System.out.println("execution step: " + currentSet.getExecutionStep());
				EmulatorUtils.printOramBank(currentSet.getOramBank().getArray(), lib, currentSet.getOramBank().getBankSize());
				if (config.isMultipleBanks())
					pcOffset = (int) currentSet.getOramBank().getMinAddress();
				newInst = mem.getInst(currentBank, pc, pcOffset);
				//newInst = mem.getInst(singleInstructionBank, pc, pcOffset); 
				mem.func(reg, memBank, newInst, dataOffset);


				testHalt = testTerminate(reg, newInst, lib);

				if (testHalt)
					break;

				EmulatorUtils.printBooleanArray("newInst", newInst, lib);


				//if (checkMatchBooleanArray(newInst, lib, 0b10001111110000110000000000101000))
				//newInst = env.inputOfAlice(Utils.fromInt(0b10000011110000110000000000101001, 32));
				pc = cpu.function(reg, newInst, pc);

				EmulatorUtils.printRegisters(reg, lib);

				EmulatorUtils.printBooleanArray("PC", pc, lib);
				EmulatorUtils.print(pcOffset+"", lib);
				EmulatorUtils.print(currentSet.getOramBank().getMinAddress()+"", lib);

				currentSet = currentSet.getNextMemorySet();
			}
			float runTime =  ((float)(System.nanoTime() - startTime))/ 1000000000;
			System.out.println("Run time: " + runTime);
			System.out.println("Average time / instruction: " + runTime / count );
			EmulatorUtils.printBooleanArray("Rsult", reg.read(lib.toSignals(2, 32)), lib, false);
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
		config = new Configuration();
		// not used MipsEmulator emulator = new MipsEmulator(config);
		process_cmdline_args(args, config);
		Reader rdr = new Reader(new File(getBinaryFileName()), config);
		SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
		
		DataSegment instData = rdr.getInstructions(config.getFunctionLoadList());
		DataSegment memData = rdr.getData();

		// is this cast ok?  Or should we modify the mem circuit? 
		int pcOffset = (int) ent.getAddress();
		int dataOffset = (int) rdr.getDataAddress();
		MemSetBuilder<Boolean> b = new MemSetBuilder<Boolean>(config, binaryFileName);
		//List<MemorySet>sets = b.build();
		GenRunnable<Boolean> gen = new GenRunnable<Boolean>(b.build(), instData, memData, pcOffset, dataOffset);
		EvaRunnable<Boolean> env = new EvaRunnable<Boolean>(b.build(), instData, memData, pcOffset, dataOffset);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join(); 

	}

	public static class GenRunnable<T> extends network.Server implements Runnable {
		MipsParty<T> mips;

		public GenRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Alice, is, os);
				mips.mainloop(env);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static class EvaRunnable<T> extends network.Client implements Runnable {
		MipsParty<T> mips;

		public EvaRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);
				mips.mainloop(env);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
