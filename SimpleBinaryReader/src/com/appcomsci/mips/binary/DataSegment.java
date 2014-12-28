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
	
	public boolean[][] getDataAsBoolean() {
		if(data == null)
			return null;
		boolean rslt[][] = new boolean[data.length][32];
		for(int i = 0; i < data.length; i++) {
			long x = data[i];
			byte t[] = new byte[4];
			t[3] = (byte) (x & 0xff); x >>= 8;
			t[2] = (byte) (x & 0xff); x >>= 8;
			t[1] = (byte) (x & 0xff); x >>= 8;
			t[0] = (byte) x;
			
			rslt[i][7] = ((t[3] & 0x80) == 0) ? false : true;
			rslt[i][15] = ((t[2] & 0x80) == 0) ? false : true;
			rslt[i][23] = ((t[1] & 0x80) == 0) ? false : true;
			rslt[i][31] = ((t[0] & 0x80) == 0) ? false : true;
			
			rslt[i][6] = ((t[3] & 0x40) == 0) ? false : true;
			rslt[i][14] = ((t[2] & 0x40) == 0) ? false : true;
			rslt[i][22] = ((t[1] & 0x40) == 0) ? false : true;
			rslt[i][30] = ((t[0] & 0x40) == 0) ? false : true;
			
			rslt[i][5] = ((t[3] & 0x20) == 0) ? false : true;
			rslt[i][13] = ((t[2] & 0x20) == 0) ? false : true;
			rslt[i][21] = ((t[1] & 0x20) == 0) ? false : true;
			rslt[i][29] = ((t[0] & 0x20) == 0) ? false : true;
			
			rslt[i][4] = ((t[3] & 0x10) == 0) ? false : true;
			rslt[i][12] = ((t[2] & 0x10) == 0) ? false : true;
			rslt[i][20] = ((t[1] & 0x10) == 0) ? false : true;
			rslt[i][28] = ((t[0] & 0x10) == 0) ? false : true;
			
			rslt[i][3] = ((t[3] & 0x08) == 0) ? false : true;
			rslt[i][11] = ((t[2] & 0x08) == 0) ? false : true;
			rslt[i][19] = ((t[1] & 0x08) == 0) ? false : true;
			rslt[i][27] = ((t[0] & 0x08) == 0) ? false : true;
			
			rslt[i][2] = ((t[3] & 0x04) == 0) ? false : true;
			rslt[i][10] = ((t[2] & 0x04) == 0) ? false : true;
			rslt[i][18] = ((t[1] & 0x04) == 0) ? false : true;
			rslt[i][26] = ((t[0] & 0x04) == 0) ? false : true;
			
			rslt[i][1] = ((t[3] & 0x02) == 0) ? false : true;
			rslt[i][9] = ((t[2] & 0x02) == 0) ? false : true;
			rslt[i][17] = ((t[1] & 0x02) == 0) ? false : true;
			rslt[i][25] = ((t[0] & 0x02) == 0) ? false : true;
			
			rslt[i][0] = ((t[3] & 0x01) == 0) ? false : true;
			rslt[i][8] = ((t[2] & 0x01) == 0) ? false : true;
			rslt[i][16] = ((t[1] & 0x01) == 0) ? false : true;
			rslt[i][24] = ((t[0] & 0x01) == 0) ? false : true;
						
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
