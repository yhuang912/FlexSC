package mips;
import com.appcomsci.mips.memory.OramBank;

import oram.SecureArray;

public class OramBankImpl implements OramBank {
	private int minIdx=0;
	private int maxIdx;
	private boolean isAssociative;
	private SecureArray bank;
	public OramBankImpl(SecureArray bankInp){
		bank = bankInp;
	}
	
}
