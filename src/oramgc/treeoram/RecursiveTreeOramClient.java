package oramgc.treeoram;

import java.util.ArrayList;

import network.Client;
import oramgc.OramParty.BlockInBinary;
import oramgc.OramParty.Party;
import oramgc.trivialoram.TrivialOramClient;

public class RecursiveTreeOramClient extends Client {
	TrivialOramClient baseOram;
	ArrayList<TreeOramClient> clients = new ArrayList<>();
	final int recurFactor = 2;//this is optimal
	final int cutoff = 1<<10;
	final int capacity = 10;
	public RecursiveTreeOramClient(int N, int dataSize) throws Exception {
		TreeOramClient  oram = new TreeOramClient(is, os, N, dataSize, Party.CLIENT, capacity);
		clients.add(oram);
		int newDataSize = 0, newN = N;
		while(newN >= cutoff) {
			newDataSize = oram.lengthOfIden*recurFactor;
			newN = oram.N/recurFactor;
			oram = new TreeOramClient(is, os, newN, newDataSize, Party.CLIENT, capacity);
			clients.add(oram);
		}
		baseOram = new TrivialOramClient(is, os, newN, newDataSize, newN);
	}
	
	public boolean[] read(boolean[] iden) throws Exception {
		BlockInBinary base = baseOram.readAndRemove(iden);
		
		return null;
	}

}
