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
import java.util.TreeMap;
//import gc.Boolean;

public class TestCPUSC {

	static final int REGISTER_SIZE = 32;
	static final int MEM_SIZE = 72;// 2K words
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.REAL;
	static final int Alice_input = 8388608;
	static final int Bob_input = 13;
	int[] mem;
	Configuration config;
	private static String binaryFileName;	// should not be static FIXME
	 
	 
	
	public TestCPUSC(Configuration config) {
		this.config = config;
		mem = new int[MEM_SIZE];
		// http://www.mrc.uidaho.edu/mrc/people/jff/digital/MIPSir.html
		// 001001ssssstttttddddd00000100001 for ADDU s t -> d
		mem[0] = 0b00000000001000000001000000100001;
	}

	
	public boolean testTerminate(SecureArray<GCSignal> reg, GCSignal[] ins, IntegerLib<GCSignal> lib) {
		GCSignal eq = lib.eq(ins, lib.toSignals(0b00000011111000000000000000001000, 32));
		GCSignal eq2 = lib.eq(reg.trivialOram.read(31), lib.toSignals(0, 32));
		eq = lib.and(eq, eq2);
		boolean[] a = lib.declassifyToBoth(new GCSignal[]{eq});
		//System.out.print(Arrays.toString(a));
		return a[0]; 
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
		printBooleanArray(pc, lib);
		
	}
	public SecureArray<GCSignal> getRegister(CompEnv<GCSignal> env)
			throws Exception {

		// inital registers are all 0's. no need to set value.
		SecureArray<GCSignal> oram = new SecureArray<GCSignal>(env,
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

	public SecureArray<GCSignal> getMemory(CompEnv<GCSignal> env)
			throws Exception {
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		// inital registers are all 0's. no need to set value.
		SecureArray<GCSignal> oram = new SecureArray<GCSignal>(env, MEM_SIZE,
				WORD_SIZE);
		for (int i = 0; i < 2; ++i) {
			// index can be evaluated publicly.
			GCSignal[] index = lib.toSignals(i, oram.lengthOfIden);

			GCSignal[] data;
			if (env.getParty() == Party.Alice) {
				data = env.inputOfAlice(Utils.fromInt(mem[i], WORD_SIZE));
			} else {
				data = env.inputOfAlice(new boolean[WORD_SIZE]);
			}
			oram.write(index, data);
		}
		return oram;
	}
	public SecureArray<GCSignal> getInstructionsGen(CompEnv<GCSignal> env, DataSegment instData)
			throws Exception {
		boolean[][] instructions = null; 
		System.out.println("entering getInstructions");
		int numInst = instData.getDataLength();
		/*MemSetBuilder b = new MemSetBuilder(config, binaryFileName);
	    List<MemorySet> sets = b.build();
	    for(MemorySet s:sets) {
	        TreeMap<Long,boolean[]> m = s.getAddressMap(instData);
	        System.out.println(m.size());
	        // m now contains the map for this step.
	        // You could cache this back in s I guess.
	    }*/
		instructions = instData.getDataAsBoolean(); 
		
		//once we split the instruction from memory, remove the + MEMORY_SIZE
		SecureArray<GCSignal> inst = new SecureArray<GCSignal>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] data; 
		GCSignal[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(instructions[i]);
			/*if (env.getParty() == Party.Alice)
				data = env.inputOfAlice(instructions[i]);
			else 
				data = env.inputOfAlice(new boolean[WORD_SIZE]);*/
			inst.write(index, data);
			//System.out.println("Wrote instruction number "+i);
		}		
		System.out.println("exiting getInstructions");
		return inst;
	}							

	public SecureArray<GCSignal> getInstructionsEva(CompEnv<GCSignal> env, int numInst)
			throws Exception {
		SecureArray<GCSignal> inst = new SecureArray<GCSignal>(env, numInst + MEM_SIZE, WORD_SIZE);
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] data; 
		GCSignal[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(new boolean[WORD_SIZE]);
			inst.write(index, data);
		}			
		return inst;
	}		
	
	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead. 
	public SecureArray<GCSignal> getMemoryGen(CompEnv<GCSignal> env, DataSegment memData, SecureArray<GCSignal> memBank, int numInst) throws Exception{
		System.out.println("entering getMemoryGen");
		boolean memory[][] = memData.getDataAsBoolean();	
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		//remove numInst when we separate instructions from memory. 
		for (int i = numInst; i < numInst + memData.getDataLength(); i++){
			memBank.write(lib.toSignals(i, memBank.lengthOfIden), env.inputOfAlice(memory[i-numInst]));
		}
		System.out.println("exiting getMemoryGen");
		return memBank;
	 
	}
	
	//Change API to remove memBank and numInst.  Instantiate  memBank inside instead.
	public SecureArray<GCSignal> getMemoryEva(CompEnv<GCSignal> env, SecureArray<GCSignal> memBank, int numInst, int dataLen)
			throws Exception {
		//after separating instruction, instantiate memBank 
	        //instead of passing it in. 
		//SecureArray<GCSignal> memBank = new SecureArray<GCSignal>(env, MEMORY_SIZE, WORD_SIZE);
		IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
		GCSignal[] data; 
		GCSignal[] index;

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
				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Alice, is, os);
				testInstruction(env);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);
				SecureArray<GCSignal> reg = getRegister(env);
				Reader rdr = new Reader(new File(getBinaryFileName()), config);
				SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				DataSegment memData = rdr.getData();
				SecureArray<GCSignal> instructionBank = getInstructionsGen(env, inst);
				//instantiate new secure array for memory once we separate instructions from memory.
				instructionBank = getMemoryGen(env, memData, instructionBank, inst.getDataLength() );
				// is this cast ok?  Or should we modify the mem circuit? 
				int pcOffset = (int) ent.getAddress();
				System.out.println("pcoffset: " + pcOffset);
				int dataOffset = (int) rdr.getDataAddress();
				
				//Xiao's two lines
				
				//SecureArray<GCSignal> memory = getMemory(env);
				
				//could this cast cause problems when msb is 1?
				GCSignal[] pc = lib.toSignals(pcOffset, WORD_SIZE);
				GCSignal[] newInst = lib.toSignals(0, WORD_SIZE);
				boolean testHalt;
				int count = 0; 
				printOramBank(instructionBank, lib, 60);
				while (true) {
					//change instructionBank to memBank once we separate 
					 
					System.out.println("count: " + count);
					count++;
					newInst = mem.func(reg, instructionBank, pc, newInst, pcOffset, dataOffset);
					
					testHalt = testTerminate(reg, newInst, lib);
					
					if (testHalt)
						break;
									
					System.out.println("newInst");
					printBooleanArray(newInst, lib);
					//if (checkMatchBooleanArray(newInst, lib, 0b10001111110000110000000000101000))
						//newInst = env.inputOfAlice(Utils.fromInt(0b10000011110000110000000000101001, 32));
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
				//GCSignal[] reg2 = reg.read(lib.toSignals(2, reg.lengthOfIden));
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
				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Bob, is, os);
				testInstruction(env);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				CPU cpu = new CPU(env);
				MEM mem = new MEM(env);

				SecureArray<GCSignal> reg = getRegister(env);
//might be better to have bob send the number of instructions to alice.  That's the only reason 
				//we currently read the file at all. 
				Reader rdr = new Reader(new File(getBinaryFileName()), config);
				//SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				int numInst = inst.getDataLength();
				DataSegment memData = rdr.getData();
				int dataLen = memData.getDataLength();
				
				SecureArray<GCSignal> instructionBank = getInstructionsEva(env, numInst);
				//instantiate new secure array for memory once we separate instructions from memory.
				instructionBank = getMemoryEva(env, instructionBank, numInst, dataLen);
				
				//SecureArray<GCSignal> memory = getMemory(env);
				GCSignal[] newInst = lib.toSignals(0,WORD_SIZE);                           
				GCSignal[] pc = lib.toSignals(0, WORD_SIZE);
				Boolean halt;
				boolean testHalt;
				printOramBank(instructionBank, lib, 60);
				
				
				int count = 0;
				while (true){
					newInst = mem.func(reg, instructionBank, pc, newInst, 0, 0);
					testHalt = testTerminate(reg, newInst, lib);

					os.flush();
					if (testHalt)
						break; 
					
					printBooleanArray(newInst, lib);
					//if (checkMatchBooleanArray(newInst, lib, 0))
						//newInst = env.inputOfAlice(new boolean[32]);
						
					//int andCount = ((CVCompEnv)env).numOfAnds;
					pc = cpu.function(reg, newInst, pc);
					//System.out.println( "CPU circuit size:" + (((CVCompEnv)env).numOfAnds-andCount));
					printRegisters(reg, lib);
					printBooleanArray(pc, lib);
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
		setBinaryFileName(rest[0]);
	}
	
	private static boolean checkMatchBooleanArray(GCSignal[] array, IntegerLib<GCSignal> lib, int matchVal) throws Exception{
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
	private static void printBooleanArray(GCSignal[] array, IntegerLib<GCSignal> lib){
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
		TestCPUSC test = new TestCPUSC(config);
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
