package mips;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import gc.Boolean;

import oram.SecureMap;
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
import static com.appcomsci.mips.cpu.Utils.consistentHashString;
import static com.appcomsci.mips.cpu.Utils.consistentHash;
import static com.appcomsci.mips.cpu.Utils.makeInstructionSet;
import static com.appcomsci.mips.cpu.Utils.toStringSet;


import compiledlib.dov.CPU;
import compiledlib.dov.CpuImpl;
import compiledlib.dov.MEM;
import mips.EmulatorUtils;
import flexsc.CompEnv;
import flexsc.CpuFcn;
// NEW import flexsc.CpuFcn;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

public class MipsEmulatorImpl<ET> implements MipsEmulator {
	static final boolean muteLoadInstructions = true;
	static final int THRESHOLD = 1024;
	static final int RECURSE_THRESHOLD = 512;
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final int REGISTER_SIZE = 32;
	
	/*
	 * XXInputIsRef indicates whether that user's inputs will fit into the two registers allocated to them.  
	 * I suppose it is possible they have 3 and 1 input values: that case isn't currently handled.
	 * If a user's value does not fit into the register space, the address is placed there (loadInputToRegisters).
	 * If the value does fit, and they only have one value, the second input value must be < 0, or it will 
	 * also be loaded.  
	 */
	static final boolean aliceInputIsRef = true;
	static final boolean bobInputIsRef = false;
	static final int Alice_input = 6;
	static final int Bob_input = 3;
	static final int Alice_input2 = -1;
	static final int Bob_input2 = 4;
	static final int stackFrameSize = 300/4;
	static final int aliceInputSize = 25;
	static final int bobInputSize = 0;
	static final int stackSize = stackFrameSize + aliceInputSize + bobInputSize + 8;
	
	static final int mainStackSize = 136;
	static final int[][] aliceLongInput = {{0,3,2,10,11},
		{3,0,1,18,6},
		{2,1,0,1,6},
		{10,18,1,0,16},
		{11,6,6,16,0},
		};

	
	
	// Should we blither about missing CPUs?
	static final boolean blither = true;
	
	protected LocalConfiguration config;
	
	private MipsEmulatorImpl(LocalConfiguration config) throws Exception {
		this.config = config;
	}

	public void testInstruction (CompEnv<ET> env) throws Exception {

		SecureArray<ET> reg = new SecureArray<ET>(env, REGISTER_SIZE, WORD_SIZE);
		//int inst = 		0b00000000000000110001011011000010; //SRL
		int inst = 		0b00000000100100111001100000100101; //OR
		int rsCont = 	0b00000000000000000000000000000101;
		int rtCont = 	0b00000000000000000000000000011001;
		//int rdCont = 	0b00000000000000000000000000000000;
		ET[] rs = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		ET[] rt = env.inputOfAlice(Utils.fromInt(19, reg.lengthOfIden));
		//Boolean[] rd = env.inputOfAlice(Utils.fromInt(4, reg.lengthOfIden));
		ET[] rsContent = env.inputOfAlice(Utils.fromInt(rsCont, WORD_SIZE));
		ET[] rtContent = env.inputOfAlice(Utils.fromInt(rtCont, WORD_SIZE));
		//Boolean[] rdContent = env.inputOfAlice(Utils.fromInt(rdCont, WORD_SIZE));
		reg.write(rs, rsContent);
		reg.write(rt, rtContent);
		//reg.write(rd, rdContent);
		env.flush();

		CPU<ET> cpu = new CPU<ET>(env);
		IntegerLib<ET> lib = new IntegerLib<ET>(env);
		ET[] pc; 
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
	
	private static class LocalConfiguration extends Configuration {
		
		private String binaryFileName;
		private Mode mode = Mode.VERIFY;

		
		public static final String MODE_PROPERTY = "mode";
		public static final String DEFAULT_MODE = "VERIFY";
		
		protected LocalConfiguration() throws IOException {
			super();
			String tmp = null;
			try {
				tmp = getProperties().getProperty(MODE_PROPERTY, DEFAULT_MODE);
				mode = Mode.valueOf(tmp);
			} catch(Exception e) {
				System.err.println("No such mode: " + tmp);
			}
		}
		
		@SuppressWarnings("unused")
		protected LocalConfiguration(LocalConfiguration that) throws IOException {
			super(that);
			this.setMode(that.getMode());
			this.setBinaryFileName(that.getBinaryFileName());
		}
		
		public void setBinaryFileName(String fileName) {
			binaryFileName = fileName;
		}

		public String getBinaryFileName() {
			return binaryFileName;
		}

		/**
		 * @return the mode
		 */
		public Mode getMode() {
			return mode;
		}

		/**
		 * @param mode the mode to set
		 */
		public void setMode(Mode mode) {
			this.mode = mode;
		}
	}
	

	private class MipsParty<T> {
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
			CpuFcn<T> defaultCpu = new CpuImpl<T>(env);
			MEM<T> mem = new MEM<T>(env);
			reg = loadInputsToRegister(env, this.dataOffset);
			
			loadCpus(sets, env);

			SecureMap<T> singleInstructionBank = null;

			if (!config.isMultipleBanks()){
				singleInstructionBank = loadInstructionsSingleBank(env, instData);				
			}
			loadInstructionsMultiBanks(env, singleInstructionBank, sets);
			SecureArray<T> memBank = getMemory(env, memData);

			T[] pc = lib.toSignals(pcOffset, WORD_SIZE);
			T[] newInst = lib.toSignals(0, WORD_SIZE);
			boolean testHalt;
			int count = 0; 
			//if (!config.isMultipleBanks())
				//EmulatorUtils.printOramBank(singleInstructionBank, lib, 60);
			long startTime = System.nanoTime();
			MemorySet<T> currentSet = sets.get(0);
			SecureMap<T> currentBank;
			dataOffset -= (stackSize*4);
			while (true) {
				currentBank = currentSet.getOramBank().getMap();
				EmulatorUtils.print("count: " + count, lib, false);
				count++;
				System.out.println("execution step: " + currentSet.getExecutionStep());
				//if (config.isMultipleBanks())
					//currentSet.getOramBank().getMap().print();
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
				CpuFcn<T> cpu = currentSet.getCpu();
				if(cpu == null)
					pc = defaultCpu.function(reg, newInst, pc, null);
				else
					pc = cpu.function(reg, newInst, pc, null);

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
		
		private void loadCpus(List<MemorySet<T>> sets, CompEnv<T>env) {
			if(!config.isMultipleBanks()) {
				System.out.println("Not loading CPUs for single bank execution");
				return;
			}
			System.out.println("Entering loadCpus");
			// Uses arcane knowledge. FIXME
			String packageName = CPU.class.getPackage().getName();
			String classNameRoot = "Cpu";
			for(MemorySet<T>s:sets) {
				CpuFcn<T> cpu = s.findCpu(env, packageName, classNameRoot, true);
				if(cpu == null && blither) {
					System.err.println("Could not find cpu for: [" +
							consistentHash(toStringSet(makeInstructionSet(s))) +
							"] " + consistentHashString(toStringSet(makeInstructionSet(s)))
							);
				}
			}
			System.out.println("Exiting loadCpus");
		}

		private boolean testTerminate(SecureArray<T> reg, T[] ins, IntegerLib<T> lib) {
			// Look for branch to here.  There are several ways to code this.
			// Gcc and cousins use BEQ $0,$0,-1
			// 0x1000ffff = 0b000100 00000 00000 1111111111111111
			T eq = lib.eq(ins, lib.toSignals(0x1000ffff, 32));
			// Look for jr $31 where $31 contains zero
			// 0x03e00008 = 0b000000 11111 0000000000 00000 001000
			T eq1 = lib.eq(ins, lib.toSignals(0x03e00008, 32));
			T eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
			eq1 = lib.and(eq1, eq2);
			eq = lib.or(eq,  eq1);
			T[] res = lib.getEnv().newTArray(1);
			res[0] = eq1;
			return lib.declassifyToBoth(res)[0]; 
		}

		private SecureArray<T> loadInputsToRegister(CompEnv<T> env, int dataOffset)
				throws Exception {
			int aliceReg = 4; 
			int bobReg = 5;
			// inital registers are all 0's. no need to set value.
			SecureArray<T> oram = new SecureArray<T>(env,
					REGISTER_SIZE, WORD_SIZE);
			for(int i = 0; i < REGISTER_SIZE; ++i)
				oram.write(env.inputOfAlice(Utils.fromInt(i, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(0, WORD_SIZE)));
			if (!aliceInputIsRef){
				oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(Alice_input, WORD_SIZE)));
				if (Alice_input2 >= 0){
					bobReg = 6;
					oram.write(env.inputOfAlice(Utils.fromInt(5, oram.lengthOfIden)),
							env.inputOfAlice(Utils.fromInt(Alice_input2, WORD_SIZE)));
				}
			}
			else { 
				oram.write(env.inputOfAlice(Utils.fromInt(aliceReg, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(dataOffset - (4*aliceInputSize), WORD_SIZE)));
			}
			if (!bobInputIsRef){
				oram.write(env.inputOfAlice(Utils.fromInt(bobReg, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(Bob_input, WORD_SIZE)));
				if (Bob_input2 >= 0)
					oram.write(env.inputOfAlice(Utils.fromInt(bobReg+1, oram.lengthOfIden)),
							env.inputOfAlice(Utils.fromInt(Bob_input2, WORD_SIZE)));
			}
			else { 
				oram.write(env.inputOfAlice(Utils.fromInt(bobReg, oram.lengthOfIden)),
						env.inputOfAlice(Utils.fromInt(dataOffset - (4*(aliceInputSize + bobInputSize)), WORD_SIZE)));
			}
			env.flush();
			int stackPointer = dataOffset - (4*aliceInputSize) - 32;
			oram.write(env.inputOfAlice(Utils.fromInt(29, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			
			oram.write(env.inputOfAlice(Utils.fromInt(30, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			//global pointer? 
			oram.write(env.inputOfAlice(Utils.fromInt(28, oram.lengthOfIden)),
					env.inputOfAlice(Utils.fromInt(stackPointer, WORD_SIZE)));
			
			
			
			return oram;
		}

		private SecureMap<T> loadInstructionsSingleBank(CompEnv<T> env, DataSegment instData)
				throws Exception {
			TreeMap<Long, boolean[]> instructions = null; 
			System.out.println("entering getInstructions, SingleBank");
			int numInst = instData.getDataLength();
			instructions = instData.getDataAsBooleanMap(); 

			//once we split the instruction from memory, remove the + MEMORY_SIZE
			SecureMap<T> instBank = new SecureMap<T>(env, numInst, WORD_SIZE, THRESHOLD);
			IntegerLib<T> lib = new IntegerLib<T>(env);
			T[] data; 
			T[] index;

			if (env.getParty() == Party.Alice)
				instBank.init(instructions, 32, 32);
			else
				instBank.init(numInst, 32, 32);
//			for (int i = 0; i < numInst; i++){
//				index = lib.toSignals(i, instBank.lengthOfIden);
//				if (env.getParty() == Party.Alice)
//					data = env.inputOfAlice(instructions[i]);
//				else 
//					data = env.inputOfAlice(new boolean[WORD_SIZE]);
//				instBank.write(index, data);
//				//System.out.println("Wrote instruction number "+i);
//			}		
			System.out.println("exiting getInstructions");
			return instBank;
		}			

		private void loadInstructionsMultiBanks(CompEnv<T> env, SecureMap<T> singleBank, List<MemorySet<T>> sets) throws Exception {
			System.out.println("entering loadInstructions");
			IntegerLib<T> lib = new IntegerLib<T>(env);
			T[] data; 
			T[] index;
			SecureMap<T> instructionBank;

			for(MemorySet<T> s:sets) {
				int i = s.getExecutionStep();

				EmulatorUtils.print("step: " + i + " size: " + s.size(), lib);

				TreeMap<Long,boolean[]> m = s.getAddressMap();	  
				long maxAddr = m.lastEntry().getKey();
				if (maxAddr == 0)
					break;
				//long minAddr = m.firstEntry().getKey();
					// do we still need this?
				long minAddr;
				if (s.size() == 1)
					minAddr = maxAddr;
				else minAddr = m.ceilingKey((long)1);
				
				if (!config.isMultipleBanks())
					instructionBank = singleBank;
				else {
					instructionBank = new SecureMap<T>(env, s.size(), WORD_SIZE);
					int count = 0;
					if (config.getMode() == Mode.VERIFY){
						for( Map.Entry<Long, boolean[]> entry : m.entrySet()) {
							if (env.getParty() == Party.Alice) {
								EmulatorUtils.print("count: " + count + " key: " + entry.getKey() +
										" (0x" + Long.toHexString(entry.getKey()) + ")" +
										" value: " , lib, muteLoadInstructions);
								String output = "";
								for (int j = 31 ; j >= 0;  j--){
									if (entry.getValue()[j])
										output += "1";
									else 
										output += "0";
								}
								EmulatorUtils.print(output, lib, muteLoadInstructions);
							}
							count++;
						}
					}

					if (env.getParty() == Party.Alice){
						instructionBank.init(m, 32, 32);
					}
					else 
						instructionBank.init(m.size(), 32, 32);
					
					if (!muteLoadInstructions)
						instructionBank.print();
					//						if (entry.getKey() > 0){
						//							index = lib.toSignals((int)((entry.getKey() - minAddr)/4), instructionBank.lengthOfIden);
						//							if (env.getParty() == Party.Alice){
						//								data = env.inputOfAlice(entry.getValue());
						//							}
						//							else	 { 
						//								data = env.inputOfAlice(new boolean[WORD_SIZE]); 
						//
						//							}
						//							// once the indices are correct, write here. 
						//							instructionBank.write(index, data);
						//						}
						
					

				}
				OramBank<T> bank = new OramBank<T>(instructionBank);
				bank.setMaxAddress(maxAddr);
				bank.setMinAddress(minAddr);
				s.setOramBank(bank);
			}		
			System.out.println("exiting getInstructions");
		}

		//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
		public SecureArray<T> getMemory(CompEnv<T> env, DataSegment memData) throws Exception{
			System.out.println("entering getMemoryGen");
			boolean memory[][] = memData.getDataAsBoolean();	
			IntegerLib<T> lib = new IntegerLib<T>(env);
			int dataLen = memData.getDataLength();
			int memSize = stackSize + dataLen;
			SecureArray<T> memBank = new SecureArray<T>(env, memSize, WORD_SIZE, THRESHOLD, RECURSE_THRESHOLD, 4);
			
			T[] index; 
			T[] data;
			for (int i = 0; i < dataLen; i++){
				index = lib.toSignals(i + stackSize, memBank.lengthOfIden);
				if (env.getParty() == Party.Alice)
					data = env.inputOfAlice(memory[i]);
				else 
					data = env.inputOfAlice(new boolean[WORD_SIZE]);
				memBank.write(index, data);	
			}
			if (aliceInputIsRef){
				for (int i = 0; i < aliceLongInput.length; i++){
					for (int j = 0; j < aliceLongInput[0].length; j++){
						index = lib.toSignals(stackSize - aliceInputSize + (i * aliceLongInput[0].length)+j, memBank.lengthOfIden);
						if (env.getParty() == Party.Alice)
							data = env.inputOfAlice(Utils.fromInt(aliceLongInput[i][j], WORD_SIZE));
						else 
							data = env.inputOfAlice(new boolean[WORD_SIZE]);
						memBank.write(index, data);						
					}
				}
			}
			EmulatorUtils.printOramBank(memBank, lib, stackSize + dataLen);
			System.out.println("exiting getMemoryGen");
			
			return memBank;
		}
	}

	private static void process_cmdline_args(String[] args, LocalConfiguration config) {
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
		if(rest.length != 1) {
			printUsage();
			System.exit(2);
		}
		config.setBinaryFileName(rest[0]);
	}

	private static void printUsage() {
		System.out.println("Usage: java RunACSEmulatorServer [binary file]");
	}

	static public void main(String args[]) throws Exception {
		// Problem:  We need to parse args and config in order to determine
		// mode (VERIFY or REAL), but we need to stash the filename in the emulator
		// Solution: Subclass Configuration and stash the filename there.  We
		// use this class to store the mode also
		LocalConfiguration config = new LocalConfiguration();
		process_cmdline_args(args, config);
		MipsEmulator emu = null;
		switch(config.getMode()) {
		case VERIFY:
			emu = new MipsEmulatorImpl<Boolean>(config);
			break;
		case REAL:
			emu = new MipsEmulatorImpl<GCSignal>(config);
			break;
		default:
			System.err.println("Help!  What do I do about " +  config.getMode() + "?");
			System.exit(1);
		}
		emu.emulate();
	}
	
	public void emulate() throws Exception {
		Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
		SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
		
		DataSegment instData = rdr.getInstructions(config.getFunctionLoadList());
		DataSegment memData = rdr.getData();

		// is this cast ok?  Or should we modify the mem circuit? 
		int pcOffset = (int) ent.getAddress();
		int dataOffset = (int) rdr.getDataAddress();
		MemSetBuilder<ET> b = new MemSetBuilder<ET>(config, config.getBinaryFileName());
		//List<MemorySet>sets = b.build();
		System.err.println("mode is " + config.getMode());
		GenRunnable<ET> gen = new GenRunnable<ET>(b.build(), instData, memData, pcOffset, dataOffset);
		EvaRunnable<ET> env = new EvaRunnable<ET>(b.build(), instData, memData, pcOffset, dataOffset);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join(); 

	}

	private class GenRunnable<T> extends network.Server implements Runnable {
		MipsParty<T> mips;

		public GenRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				listen(54321);
				// @SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(config.getMode(), Party.Alice, is, os);
				mips.mainloop(env);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	private class EvaRunnable<T> extends network.Client implements Runnable {
		MipsParty<T> mips;

		public EvaRunnable(List<MemorySet<T>> sets,	DataSegment instData, DataSegment memData, int pcOffset,int dataOffset ){
			mips = new MipsParty<T>(sets, instData, memData, pcOffset, dataOffset);
		}

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(config.getMode(), Party.Bob, is, os);
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
