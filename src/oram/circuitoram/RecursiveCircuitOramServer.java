package oram.circuitoram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import oram.trivialoram.TrivialOramServer;
import flexsc.Mode;
import flexsc.Party;

public class RecursiveCircuitOramServer<T> {
	TrivialOramServer<T> baseOram;
	public ArrayList<CircuitOramServer<T>> servers = new ArrayList<>();
	int recurFactor;
	int cutoff;
	int capacity;
	int initialLengthOfIden;
	protected InputStream is;
	protected OutputStream os;
	public RecursiveCircuitOramServer(InputStream is, OutputStream os, int N, int dataSize, int cutoff, int recurFactor, 
			int capacity, Mode m, int sp) throws Exception {
		this.is = is;
		this.os = os;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		System.out.print(capacity);
		CircuitOramServer<T>  oram = new CircuitOramServer<T>(is, os, N, dataSize, Party.Bob, capacity,  m, sp);
		servers.add(oram);
		int newDataSize = oram.lengthOfPos, newN = (1<<oram.lengthOfIden);
		while(newN > cutoff) {
			newDataSize = oram.lengthOfPos * recurFactor;
			newN = (1<<oram.lengthOfIden ) / recurFactor;
			oram = new CircuitOramServer<T>(is, os, newN, newDataSize, Party.Bob, capacity,  m, sp);
			servers.add(oram);
		}
		CircuitOramServer<T> last = servers.get(servers.size()-1);
		baseOram = new TrivialOramServer<T>(is, os, (1<<last.lengthOfIden), last.lengthOfPos, m);
	}
	
	public void access() throws Exception {
		travelToDeep(1);
		CircuitOramServer<T> currentOram = servers.get(0);
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
			
			CircuitOramServer<T> currentOram = servers.get(level);
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