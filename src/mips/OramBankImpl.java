package mips;
import com.appcomsci.mips.memory.OramBank;

import oram.SecureArray;

public class OramBankImpl implements OramBank {
	private int minIdx=-1;
	private int maxIdx;
	private boolean isAssociative;
	private SecureArray<?> bank;
	public OramBankImpl(SecureArray<?> bankInp){
		bank = bankInp;
	}
	public int getLengthOfIden(){ return bank.lengthOfIden; }
	public SecureArray<?> getArray() { return bank; } 
	
}
