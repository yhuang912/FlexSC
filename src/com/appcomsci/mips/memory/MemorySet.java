/**
 * 
 */
package com.appcomsci.mips.memory;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import mips.OramBank;

import com.appcomsci.mips.binary.DataSegment;

/**
 * A set of memory addresses that might be executed at a particular
 * execution step.
 * @author Allen McIntosh
 *
 */
public class MemorySet<T> {
	/**
	 * The addresses
	 */
	private TreeSet<Long> addresses;
	/**
	 * The number of the execution step
	 */
	private final int executionStep;
	/**
	 * The next execution step, or null if we ran off the end of the world
	 */
	private MemorySet<T> nextMemorySet;
	/** Does this use memory?
	 */
	private boolean usesMemory;
	/**
	 ** Oram Bank for storing the instructions securely
	 */
	private OramBank<T> oramBank = null;
	/**
	 * Build a memory set consisting of the current addresses of a list of threads.
	 * @param executionStep The number of the execution step
	 * @param threads The list of threads.
	 */
	public MemorySet(final int executionStep, List<ThreadState>threads) {
		this.executionStep = executionStep;
		addresses = new TreeSet<Long>();
		for(ThreadState t:threads) {
			addresses.add(t.getCurrentAddress());
		}
	}
	/**
	 * Initialize memory set to be every address in the data segment
	 * @param executionStep The number of the execution step
	 * @param seg The data segment
	 */
	public MemorySet(final int executionStep, DataSegment seg) {
		this.executionStep = executionStep;
		addresses = new TreeSet<Long>();
		for(int i = 0; i < seg.getDataLength(); i++)
			addresses.add(seg.getStartAddress() + 4*i);
	}
	/**
	 * Get the set of addresses associated with this step
	 * @return The set of addresses.
	 */
	public TreeSet<Long> getAddresses() {
		return addresses;
	}
	
	/**
	 * Get a map from addresses in this set to the data at those addresses
	 * @param dseg A DataSegment containing the data
	 * @return The map.
	 */
	public TreeMap<Long,boolean[]> getAddressMap(DataSegment dseg) {
		TreeMap<Long, boolean[]> rslt = new TreeMap<Long, boolean[]>();
		for(Long addr:addresses) {
			rslt.put(addr, dseg.getDatumAsBoolean(addr));
		}
		return rslt;
	}
	
	/**
	 * Determine if any instruction at this step uses memory.
	 * Cache the value for later use.
	 * @param dseg The program instructions.
	 */
	public void setUsesMemory(DataSegment dseg) {
		for(Long addr:addresses) {
			long instr = dseg.getDatum(addr);
			MipsInstructionSet.Operation op = MipsInstructionSet.Operation.valueOf(instr);
			switch(op.getType()) {
			case MW:
			case MR:
				usesMemory = true;
				return;
			default:
				break;
			}
		}
		usesMemory = false;
	}
	
	/**
	 * Do the instructions in this set reference memory?
	 * Note:  The value of this method is cached.  It must be set initially
	 * by calling setUsesMemory().
	 * @return True if memory is read or written, false otherwise.
	 */
	public boolean isUsesMemory() {
		return usesMemory;
	}
	/**
	 * Get the number of possible addresses in this execution step
	 * @return The number of possible addresses in this execution step
	 */
	public int size() {
		return addresses.size();
	}
	
	/**
	 * Is this memory set spinning at the spin address and nothing more?
	 * @return True if all threads are spinning, false otherwise.
	 */
	public boolean isAllSpinning() {
		if(addresses.size() != 1) return false;
		return addresses.first() == MipsInstructionSet.getSpinAddress();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Memory Set " + executionStep + ":");
		if(nextMemorySet != null) {
			sb.append(" Next: " + nextMemorySet.getExecutionStep());
		}
		sb.append(" [" + addresses.size() + "] ");
		for(Long l:addresses) {
			sb.append(" " + Long.toHexString(l));
		}
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if(o == null)
			return false;
		if(!(o instanceof MemorySet<?>))
			return false;
		MemorySet<T> that = (MemorySet<T>) o;
		return this.getAddresses().equals(that.getAddresses());
	}
	
	/**
	 * Compute a hash code for this MemorySet, using only the addresses
	 */
	public int hashCode() {
		// Use dumb GCC LCRNG to smash bits
		int rslt = 1103515245+12345;
		for(Long a:addresses) {
			rslt = rslt ^ (int)(a*1103515245+12345);
		}
		return rslt;
	}
	/**
	 * @return the executionStep
	 */
	public int getExecutionStep() {
		return executionStep;
	}
	/**
	 * @return the nextMemorySet
	 */
	public MemorySet<T> getNextMemorySet() {
		return nextMemorySet;
	}
	/**
	 * @param nextMemorySet the nextMemorySet to set
	 */
	public void setNextMemorySet(MemorySet<T> nextMemorySet) {
		this.nextMemorySet = nextMemorySet;
	}
	/**
	 * @return the oramBank
	 */
	public OramBank<T> getOramBank() {
		return oramBank;
	}
	/**
	 * @param oramBank the oramBank to set
	 */
	public void setOramBank(OramBank<T> oramBank) {
		this.oramBank = oramBank;
	}

}