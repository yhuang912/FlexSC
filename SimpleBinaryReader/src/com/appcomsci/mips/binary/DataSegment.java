/**
 * 
 */
package com.appcomsci.mips.binary;

import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * This class describes the contents of a chunk of memory.
 * @author Allen McIntosh
 *
 */
public class DataSegment {
	private final long startAddress;
	private final long data[];
	DataSegment(final long address, final long data[]) {
		this.startAddress = address;
		if(data != null)
			this.data = (long[])data.clone();
		else
			this.data = null;
	}
	/** The start address of this chunk of memory */
	public long getStartAddress() {
		return startAddress;
	}
	/** Data length, in words */
	public int getDataLength() {
		if(data == null) return 0;
		return data.length;
	}
	/** The contents of this chunk of memory */
	public long[] getData() {
		return data;
	}
	/** The contents of a word of memory at a specific address
	 * 
	 * @param address The address
	 * @return The data as that address
	 * @throws UnsupportedOperationException if the address is not a multiple of 4
	 * @throws NoSuchElementException if the address is out of range
	 */
	public long getDatum(long address) {
		long index = address-startAddress;
		if((index & 0x3) != 0) {
			throw new UnsupportedOperationException("Address not a multiple of 4: " + Long.toHexString(address));
		}
		index = index / 4;
		if(index < 0 || index >= data.length)
			throw new NoSuchElementException("No such address " + Long.toHexString(address));
		return data[(int) index];
	}
	/** The contents of this chunk of memory, as big integers */
	public BigInteger[] getDataAsBigIntegers() {
		if(data == null)
			return null;
		BigInteger rslt[] = new BigInteger[data.length];
		for(int i = 0; i < data.length; i++) {
			long x = data[i];
			byte t[] = new byte[4];
			t[3] = (byte) (x & 0xff); x >>= 8;
			t[2] = (byte) (x & 0xff); x >>= 8;
			t[1] = (byte) (x & 0xff); x >>= 8;
			t[0] = (byte) x;
			rslt[i]= new BigInteger(1, t);
		}
		return rslt;
	}
	
	/** Contents of this chunk of memory as booleans.  Little endian.
	 * 
	 * @return An array of arrays of booleans
	 */
	public boolean[][] getDataAsBoolean() {
		if(data == null)
			return null;
		boolean rslt[][] = new boolean[data.length][32];
		for(int i = 0; i < data.length; i++) {
			long x = data[i];
			long mask = 0x00000001;
			boolean t[] = rslt[i];
			// Let compiler decide how much to unroll this loop
			for(int j = 0; j < 32; j++) {
				t[j] = (x&mask) != 0;
				mask <<= 1;
			}
		}
		return rslt;
	}
	/*
	public void addInstructions(SectionData sec) {
		addInstructions(sec, sec.getNumBytes()>>2);
	}
	public void addInstructions(SectionData sec, int numInstructions) {
		long tmp[] = sec.getInstructions();
		if(data == null) {
			// Short path through for first time
			startAddress = sec.getStartAddress();
			data = new long[numInstructions];
			for(int i = 0; i < numInstructions; i++) {
				data[i] = tmp[i];
			}
			this.numWords = numInstructions;
			return;
		}
		if(sec.getStartAddress() >= startAddress) {
			// May need to add to end
			int startOffset = (int)((sec.getStartAddress() - startAddress)>>2);
			long endAddress = startAddress + this.numWords<<2;
			long newEndAddress = sec.getStartAddress() + numInstructions<<2;
			if(newEndAddress > endAddress) {
				int n = (int)((newEndAddress - startAddress)>>2);
				long x[] = new long[n];
				for(int i = 0; i < this.numWords; i++) {
					x[i] = data[i];
				}
				data = x;
			}
			for(int i = 0; i < numInstructions; i++) {
				data[startOffset+i] = tmp[i];
			}
		} else {
			// Definitely need to add to start
			int numToAdd = (int)((startAddress - sec.getStartAddress())>>2);
			long x[] = new long[this.numWords + numToAdd];
			for(int i = 0; i < this.numWords; i++) {
				x[i+numToAdd] = data[i];
			}
			data = x;
			for(int i = 0; i < numInstructions; i++) {
				x[i] = tmp[i];
			}
			startAddress = sec.getStartAddress();
		}
	}
	*/
}
