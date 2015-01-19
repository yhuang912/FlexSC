package mips;
import com.appcomsci.mips.memory.OramBank;

import oram.SecureArray;

public class OramBankImpl implements OramBank {
	private int minIdx=-1;
	private int maxIdx;
	private boolean isAssociative;
	private SecureArray<?> bank;
	/**
	 ** stores the maximum address value in this timestep.  We probably won't need this after we have
	 * working associative arrays.    
	 */
	private long maxAddress;
	/**
	 ** stores the minimum address value in this timestep.  We probably won't need this after we have
	 * working associative arrays.    
	 */
	private long minAddress;
	public OramBankImpl(SecureArray<?> bankInp){
		bank = bankInp;
	}
	public int getLengthOfIden(){ return bank.lengthOfIden; }
	public SecureArray<?> getArray() { return bank; } 
	/**
	 * @return the span of the address range.  This is the necessary size of the 
	 * SecureArray, if we do not have access to an oblivious associative map.
	 */
	public int getBankSize(){
		if (minAddress < 0) return 0; 
		else return (int)(maxAddress - minAddress)/4 + 1;
	}
	/**
	 * @param val is the value to set minAddress to. 
	 */
	public void setMinAddress(long val){
		minAddress = val;
	}
	/**
	 * @param val is the value to set maxAddress to. 
	 */
	public void setMaxAddress(long val){
		maxAddress = val;
	}
	/**
	 * @return minAddress value stored in this bank.  
	 */
	public long getMinAddress(){
		return minAddress; 
	}
}
