/**
 * This class reads a MIPS binary and builds the sets of
 * instructions that might be executed at each program step.
 * 
 * @author Allen McIntosh
 */
package com.appcomsci.mips.memory;

import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BEQ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BGEZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BGEZAL;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BGTZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BLEZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BLTZ;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BLTZAL;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_BNE;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_FUNCT;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_J;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_JAL;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_JALR;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_JR;
import static com.appcomsci.mips.memory.MipsInstructionSet.OP_REGIMM;
import static com.appcomsci.mips.memory.MipsInstructionSet.RETURN_REG;
import static com.appcomsci.mips.memory.MipsInstructionSet.SPIN_ADDRESS;
import static com.appcomsci.mips.memory.MipsInstructionSet.NOP;
import static com.appcomsci.mips.memory.MipsInstructionSet.getFunct;
import static com.appcomsci.mips.memory.MipsInstructionSet.getInstrIndex;
import static com.appcomsci.mips.memory.MipsInstructionSet.getOffset;
import static com.appcomsci.mips.memory.MipsInstructionSet.getOp;
import static com.appcomsci.mips.memory.MipsInstructionSet.getRegImmCode;
import static com.appcomsci.mips.memory.MipsInstructionSet.getSrcReg;
import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.sfe.common.Configuration;

/**
 * Create sets of memory locations that can be executed at each
 * program step
 * 
 * @author Allen McIntosh
 *
 */
public class MemSetBuilder {
	private boolean honorDelaySlots = false;
	private Configuration config;
	
	/**
	 * Contructor: Pick up configuration from a user-supplied config object
	 * @param config The configuration object used to initialize this builder.
	 * @param honorDelaySlots If true, execute instructions in delay slots before
	 *		taking branches.
	 */
	public MemSetBuilder(Configuration config, boolean honorDelaySlots) {
		this.config = config; // Should we clone this?
		this.honorDelaySlots = honorDelaySlots;
	}
	
	/**
	 * Constructor: Pick up configuration from command line arguments, with
	 * a properties file as backup.
	 * @param args
	 * @throws IOException
	 */
	public MemSetBuilder(String args[]) throws IOException, CmdLineParser.OptionException {
		// Create configuration object.  This reads the properties file.
		config = new Configuration();
		
		// Now parse command line arguments
		
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option.StringOption oE = new CmdLineParser.Option.StringOption('e', "entry.point");
		CmdLineParser.Option.StringOption oL = new CmdLineParser.Option.StringOption('l', "function.load.list");
		CmdLineParser.Option.StringOption oB = new CmdLineParser.Option.StringOption('b', "binary.reader.path");
		CmdLineParser.Option.IntegerOption oM = new CmdLineParser.Option.IntegerOption('m', "max.program.steps");
		CmdLineParser.Option.BooleanOption oD = new CmdLineParser.Option.BooleanOption('d', "honor.delay.slots");
		parser.addOption(oE);
		parser.addOption(oL);
		parser.addOption(oB);
		parser.addOption(oM);
		parser.addOption(oD);

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
		if((o = parser.getOptionValue(oM)) != null)
			config.setMaxProgramSteps((Integer)o);
		if((o = parser.getOptionValue(oD)) != null)
			honorDelaySlots = (Boolean) o;
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
	
	/**
	 * Change the maximum number of program steps.
	 * @param maxProgramSteps The new maximum number of program steps.
	 */
	public void setMaxProgramSteps(int maxProgramSteps) {
		synchronized(config) { // Probably unnecessary
			config.setMaxProgramSteps(maxProgramSteps);
		}
	}
	
	public int getMaxProgramSteps() {
		return config.getMaxProgramSteps();
	}
	
	/**
	 * Generate a list of instructions that might be executed in any program step.
	 * 
	 * @return An array of MemorySet objects.  The first element of this array [accessible
	 * via get(0)] is the address of the first instruction at the entry point.  Subsequent
	 * elements are the addresses of subsequent instructions in the program.  They may be accessed by running
	 * down the returned ArrayList in order, or by following the NextMemorySet properties.
	 * 
	 * The analysis attempts to trace possible execution paths.  Each time a conditional branch
	 * is encountered, a new thread representing a possible execution path is started.  If a
	 * thread successfully reaches the end of the routine being analyzed, it is assumed to return to
	 * the address MipsInstructionSet.SPIN_ADDRESS (currently 0) where it is assumed to spin
	 * forever.
	 * 
	 * Eventually, one of the following happens:
	 * 1) The analysis terminates.  (This will only happen in simple programs with no
	 * loops).  The final MemorySet object will contain only SPIN_ADDRESS and will be
	 * self-referential.
	 * 2) The analysis hits the maximum number of program steps.  The NextMemorySet pointer
	 * of the final object in the list will be null.
	 * 3) The analysis hits a JR or JALR instruction where the target cannot be determined.  The
	 * final MemorySet object in the list will be a MemorySet object containing all possible
	 * addresses, and the NextMemorySet pointer will be self-referential.
	 * 4) The analysis hits a state that it has already encountered.  The NextMemorySet pointer
	 * of the final state will point back to this previous state.
	 * 
	 * 
	 * @throws FileNotFoundException If the binary doesn't exist
	 * @throws IllegalArgumentException ??
	 * @throws IOException If the binary (or properties file) can't be read.
	 * @throws MemSetBuilderException For some impossible conditions in the set builder
	 */
	public List<MemorySet> build()
			throws FileNotFoundException, IllegalArgumentException, IOException, MemSetBuilderException {
		
		// This is the array to be returned.
		List<MemorySet> execSets = new ArrayList<MemorySet>();
		
		// The previous set, for forward chaining
		MemorySet prevSet = null;
		
		// A hash map, for detecting recurring states.
		// Each value in the hash map is a bucket of MemorySets
		Map<MemorySet, ArrayList<MemorySet>> memSetMap = new HashMap<MemorySet, ArrayList<MemorySet>>();

		// Grab things from the config properties.  Synchronize so that we see something
		// consistent.  If the caller wishes to change more than one property in a
		// multi-threaded environment, they should likewise synchronize on the
		// config object.
		
		int maxSteps;
		Reader rdr;
		SymbolTableEntry ent;
		DataSegment inst;
		synchronized(config) {
			maxSteps = config.getMaxProgramSteps();
			rdr = new Reader(new File(config.getBinaryFileName()), config);
			ent = rdr.getSymbolTableEntry(config.getEntryPoint());	
			inst = rdr.getInstructions(config.getFunctionLoadList());
		}
		
		// The list of currently "executing" threads
		
		LinkedList<ThreadState> threads = new LinkedList<ThreadState>();
		
		// Initially, one thread starting at the entry address
		ThreadState initial = new ThreadState(ent.getAddress());
		threads.add(initial);
		
		executionLoop: for(int executionStep = 0; executionStep < maxSteps; executionStep++) {
			if(threads.size() == 0) {
				throw new MemSetBuilderException("No execution threads after " + executionStep + " iterations");
			}
			
			// Create a memory set that contains the current step number, and
			// the address of all currently running threads.
			
			MemorySet currentSet = new MemorySet(executionStep, threads);
			
			// Have we seen this set before?
			
			ArrayList<MemorySet> bucket = memSetMap.get(currentSet);
			if(bucket == null) { // Definitely not
				bucket = new ArrayList<MemorySet>();
				memSetMap.put(currentSet, bucket);
			} else {
				// Maybe.  Does bucket contain a set equal to currentSet?
				for(MemorySet s:bucket) {
					if(currentSet.equals(s)) {
						// Found an equivalent set.
						if(prevSet == null) {
							throw new MemSetBuilderException("Found an equivalent set with no previous set");
						} else {
							// So stop tracing here by pointing to the previous set.
							prevSet.setNextMemorySet(s);
						}
						break executionLoop;
					}
				}
			}
			bucket.add(currentSet);
			execSets.add(executionStep, currentSet);
			if(prevSet != null)
				prevSet.setNextMemorySet(currentSet);
			
			// Quit if the set of possible addresses is the universe.  (Should probably make
			// this half the universe or something and make the next set equal to the universe)
			if(currentSet.size() >= inst.getDataLength()) {
				currentSet.setNextMemorySet(currentSet);
				break;
			}
			// Quit if everything is spinning
			if(currentSet.isAllSpinning()) {
				currentSet.setNextMemorySet(currentSet);
				break;
			}
			
			prevSet = currentSet;
			
			LinkedList<ThreadState> newThreads = new LinkedList<ThreadState>();
			ListIterator<ThreadState> thI = threads.listIterator(0);
			
			// Advance each thread one instruction.
			// Invariant:  At bottom of loop body the thread is ready to execute the next
			// instruction
			
//System.err.println("Step: " + executionStep);
			while(thI.hasNext()) {
				ThreadState th = thI.next();
//System.err.println("  Thread " + th.getId() + " A: " + Long.toHexString(th.getCurrentAddress()) + " D: " +
//Long.toHexString(th.getCurrentAddress() == SPIN_ADDRESS ? 0 : inst.getDatum(th.getCurrentAddress())));

				if(th.isDelayed()) {
					// If a delay slot, just pop it off and continue
					th.advance();
				} else {
					// Get the current address, and the instr if it's not the spin.
					long addr = th.getCurrentAddress();
					long instr = NOP;
					if(addr != SPIN_ADDRESS)
						instr = inst.getDatum(th.getCurrentAddress());
					
					// Now we get down to the tedious work of simulating individual
					// instructions
					
					switch(getOp(instr)) {
					case OP_FUNCT:
						switch(getFunct(instr)) {
							// Flying leap, or maybe return
						case OP_JR:
							if(getSrcReg(instr) == RETURN_REG) {
								// Assume this is a return
								if(honorDelaySlots) {
									th.doDelay();
								} else {
//System.err.println("Popping " + Long.toHexString(th.getCurrentAddress()));
									th.popAddress();
//System.err.println("New address " + Long.toHexString(th.getCurrentAddress()));
								}
							} else {
								// Flying leap
								currentSet = new MemorySet(executionStep+1, inst);
								execSets.add(executionStep+1, currentSet);
								prevSet.setNextMemorySet(currentSet);
								currentSet.setNextMemorySet(currentSet);
								break executionLoop;
							}
							break;
							// Flying leap with link
						case OP_JALR:
							currentSet = new MemorySet(executionStep+1, inst);
							execSets.add(executionStep+1, currentSet);
							prevSet.setNextMemorySet(currentSet);
							currentSet.setNextMemorySet(currentSet);
							break executionLoop;
						default:
							th.advance();
						}
						break;
					case OP_REGIMM:
						switch(getRegImmCode(instr)) {
						// Conditional branches
						case OP_BLTZ:
						case OP_BGEZ:
							{
								long targetAddress = th.getCurrentAddress() + (getOffset(instr)<<2) + 4;
								ThreadState newThread = new ThreadState(th);
								newThreads.add(newThread);
								if(honorDelaySlots) {
									newThread.doDelay(targetAddress);
								} else {
									// Replace current address with branch target
									newThread.popAddress();
									newThread.pushAddress(targetAddress);
								}
								th.advance();
							}
							break;
						// Branches with link
						case OP_BLTZAL:
						case OP_BGEZAL:
							{
								long targetAddress = th.getCurrentAddress() + (getOffset(instr)<<2) + 4;
								ThreadState newThread = new ThreadState(th);
								newThreads.add(newThread);
								if(honorDelaySlots) {
									newThread.doCall(targetAddress);
								} else {
									// Push branch target
									newThread.pushAddress(targetAddress);
								}
								th.advance();
							}
							break;
						default:
							th.advance();
							break;
						}
						break;
					case OP_J:
						{
							// Jump away, no link.  The target address can be computed.
							long targetAddress = th.getCurrentAddress()+4;
							targetAddress &= (long)(~MipsInstructionSet.INSTR_INDEX_MASK)<<2;
							targetAddress |= getInstrIndex(instr)<<2;
							if(honorDelaySlots) {
								th.doDelay(targetAddress);
							} else {
								// Replace current address with branch target
								th.popAddress();
								th.pushAddress(targetAddress);
							}
						}
						break;
					case OP_JAL:
						{
							// Jump with link.  The target address can be computed.
							long targetAddress = th.getCurrentAddress()+4;
							targetAddress &= (long)(~MipsInstructionSet.INSTR_INDEX_MASK)<<2;
							targetAddress |= getInstrIndex(instr)<<2;
							th.advance();
							if(honorDelaySlots) {
								th.doCall(targetAddress);
							} else {
								// Push branch target
								th.pushAddress(targetAddress);
//System.err.println("Call push " + Long.toHexString(targetAddress));
							}
						}
						break;
						// Conditional branches.
					case OP_BEQ:
					case OP_BNE:
					case OP_BLEZ:
					case OP_BGTZ:
						{
							long targetAddress = th.getCurrentAddress() + (getOffset(instr)<<2) + 4;
							ThreadState newThread = new ThreadState(th);
							newThreads.add(newThread);
							if(honorDelaySlots) {
								newThread.doDelay(targetAddress);
							} else {
								newThread.popAddress();
								newThread.pushAddress(targetAddress);
							}
							th.advance();
						}
						break;
					default:
						th.advance();
					}
				}
			}
			if(newThreads.size() > 0)
				threads.addAll(newThreads);
			
			// Prune duplicate threads.  Note that we can't do this by maintaining
			// "threads" as a Set:  Part way through the above Thread loop the
			// threads are in an inconsistent state.  A pair might appear to be
			// executing at the same address when in fact one has been advanced and the
			// other has not.
			Map<Long, ArrayList<ThreadState>> pruneMap = new HashMap<Long, ArrayList<ThreadState>>();
			thI = threads.listIterator(0);
			pruneLoop: while(thI.hasNext()) {
				ThreadState th = thI.next();
				// Catch delay slots on the next instruction cycle
				if(th.isDelayed())
					continue;
				
				// Is there another thread at this address, and does
				// it have the same call state?
				ArrayList<ThreadState> pruneBucket = pruneMap.get(th.getCurrentAddress());
				if(pruneBucket == null) {
					// Empty bucket.  Create one.
					pruneBucket = new ArrayList<ThreadState>();
					pruneMap.put(th.getCurrentAddress(), pruneBucket);
				} else {
					// Search bucket
					for(ThreadState tMap:pruneBucket) {
						if(th.equals(tMap)) {
							// Same address, same state.
							// Remove thread from linked list and do not
							// add to hash bucket
							thI.remove();
							continue pruneLoop;
						}
					}
				}
				pruneBucket.add(th);
			}
			
		}
		return execSets;
		
	}
	public static void main(String args[]) throws IOException, MemSetBuilderException {
		MemSetBuilder b = null;
		try {
			b = new MemSetBuilder(args);
		} catch(CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		List<MemorySet> sets = b.build();
		for(MemorySet m:sets) {
			System.err.println(m.toString());
		}
	}

	/**
	 * @return the honorDelaySlots
	 */
	public boolean isHonorDelaySlots() {
		return honorDelaySlots;
	}

	/**
	 * @param honorDelaySlots the honorDelaySlots to set
	 */
	public void setHonorDelaySlots(boolean honorDelaySlots) {
		this.honorDelaySlots = honorDelaySlots;
	}
}