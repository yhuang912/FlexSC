package mips;
import oram.SecureArray;

public class OramBank {
	private int minIdx=0;
	private int maxIdx;
	private boolean isAssociative;
	public SecureArray bank;
	public OramBank(SecureArray bankInp){
		bank = bankInp;
	}
	
}
