package mips;
import com.appcomsci.mips.memory.OramBank;

import oram.SecureArray;

public class TrivialOramBank implements OramBank {
	private int minIdx=0;
	private int maxIdx;
	private boolean isAssociative;
	private SecureArray bank;
	public TrivialOramBank(SecureArray bankInp){
		bank = bankInp;
	}
	
}
