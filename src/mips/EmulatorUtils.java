package mips;

import oram.SecureArray;
import circuits.arithmetic.IntegerLib;
import flexsc.Mode;
import flexsc.Party;

public class EmulatorUtils {
	
	public static<T> boolean checkMatchBooleanArray(T[] array, IntegerLib<T> lib, int matchVal) throws Exception{
		if(lib.getEnv().m == Mode.REAL || lib.getEnv().m == Mode.OPT)
			return true;
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
		}
		return match;
	}
	
	public static<T> void printBooleanArray(String s, T[] array, IntegerLib<T> lib){
		printBooleanArray(s, array, lib, true);
	}
	public static<T> void printBooleanArray(String s, T[] array, IntegerLib<T> lib, boolean smart){
		if((lib.getEnv().m == Mode.REAL || lib.getEnv().m == Mode.OPT ) && smart)
			return;
		String output = s+":";
		boolean[] temp = lib.getEnv().outputToAlice(array);

		for (int i = array.length -1 ; i >= 0;  i--){
					output += temp[i] ? "1" : "0"; 
		}
		if(lib.getEnv().getParty() == Party.Alice)
			System.out.println(output);
		
	}
	public static<T> void printRegisters(SecureArray<T> reg, IntegerLib<T> lib){
		if(lib.getEnv().m == Mode.REAL || lib.getEnv().m == Mode.OPT)
			return;
		String output = "";
		T[] temp; 

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
	
	public static<T> void printOramBank(SecureArray<T> oramBank, IntegerLib<T> lib, int numItems){
		if(lib.getEnv().m == Mode.REAL || lib.getEnv().m == Mode.OPT)
			return;
		String output = "";
		T[] temp; 
		
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
}
