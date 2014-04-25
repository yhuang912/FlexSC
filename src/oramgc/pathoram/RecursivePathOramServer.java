package oramgc.pathoram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import oramgc.OramParty.Party;
import oramgc.trivialoram.TrivialOramServer;

public class RecursivePathOramServer {
	TrivialOramServer baseOram;
	ArrayList<PathOramServer> servers = new ArrayList<>();
	int recurFactor;
	int cutoff;
	int capacity;
	int initialLengthOfIden;
	protected InputStream is;
	protected OutputStream os;
	public RecursivePathOramServer(InputStream is, OutputStream os, int N, int dataSize, int cutoff, int recurFactor, int capacity) throws Exception {
		this.is = is;
		this.os = os;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		PathOramServer  oram = new PathOramServer(is, os, N, dataSize, Party.SERVER);
		servers.add(oram);
		int newDataSize = oram.lengthOfPos, newN = (1<<oram.lengthOfIden);
		while(newN > cutoff) {
			newDataSize = oram.lengthOfPos * recurFactor;
			newN = (1<<oram.lengthOfIden ) / recurFactor;
			oram = new PathOramServer(is, os, newN, newDataSize, Party.SERVER);
			servers.add(oram);
		}
		PathOramServer last = servers.get(servers.size()-1);
		baseOram = new TrivialOramServer(is, os, (1<<last.lengthOfIden), last.lengthOfPos);
	}
	
	public void access() throws Exception {
		travelToDeep(1);
		PathOramServer currentOram = servers.get(0);
		boolean[] pos = getBooleans();
		//System.out.println("server"+Utils.toInt(pos));
		currentOram.access(pos);
	}
	
	public void travelToDeep(int level) throws Exception {
		if(level == servers.size()) {
			baseOram.readAndRemove();
			baseOram.putBack();
		}
		else {
			travelToDeep(level+1);
			
			PathOramServer currentOram = servers.get(level);
			boolean[] pos = getBooleans();
			currentOram.readAndRemove(pos);
			
			currentOram.putBack();
		}
	}
	
	public boolean[] getBooleans() throws IOException {
		byte[] l = new byte[1];
		is.read(l);
		byte tmp[] = new byte[l[0]];
		is.read(tmp);
		boolean[] res = new boolean[l[0]];
		for(int k = 0; k < tmp.length; ++k) {
			res[k] = ((tmp[k] - 1) == 0);
		}
		return res;
	}
	
}
