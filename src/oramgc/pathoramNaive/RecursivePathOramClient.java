package oramgc.pathoramNaive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import oramgc.OramParty;
import flexsc.*;
import oramgc.trivialoram.TrivialOramClient;
import test.Utils;

public class RecursivePathOramClient<T> {
	TrivialOramClient<T> baseOram;
	ArrayList<PathOramClient<T>> clients = new ArrayList<>();
	int initialLengthOfIden;
	int recurFactor;
	int cutoff;
	int capacity;

	SecureRandom rng = new SecureRandom();
	protected InputStream is;
	protected OutputStream os;
	public RecursivePathOramClient(InputStream is, OutputStream os, int N, int dataSize, int cutoff, int recurFactor, int capacity, Mode m) throws Exception {
		this.is = is;
		this.os = os;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		PathOramClient<T>  oram = new PathOramClient<T>(is, os, N, dataSize, Party.Alice, m);
		clients.add(oram);
		int newDataSize = oram.lengthOfPos, newN = (1<<oram.lengthOfIden);
		while(newN > cutoff) {
			newDataSize = oram.lengthOfPos * recurFactor;
			newN = (1<<oram.lengthOfIden)  / recurFactor;
			oram = new PathOramClient<T>(is, os, newN, newDataSize, Party.Alice, m);
			clients.add(oram);
		}
		PathOramClient<T> last = clients.get(clients.size()-1);
		baseOram = new TrivialOramClient<T>(is, os, (1<<last.lengthOfIden), last.lengthOfPos, m);
	}
	
	public boolean[] read(int iden) throws Exception {
		boolean[][] poses = travelToDeep(iden, 1);
		PathOramClient<T> currentOram = clients.get(0);
		
		sendBooleans(poses[0]);
		System.out.println("read " + iden + " " + Utils.toInt(poses[0]) + " "+Utils.toInt(poses[1]));
		boolean[] res = currentOram.read(iden, poses[0], poses[1]);
		return res;
	}
	
	public void write(int iden, boolean[] data) throws Exception {
		boolean[][] poses = travelToDeep(iden, 1);
		PathOramClient<T> currentOram = clients.get(0);
		
		sendBooleans(poses[0]);
		System.out.println("write " + iden + " " + Utils.toInt(poses[0]) + " "+Utils.toInt(poses[1]));
		currentOram.write(iden, poses[0], poses[1], data);
	}
	
	public boolean[][] travelToDeep(int iden, int level) throws Exception {
		if(level == clients.size()) {
			boolean[] baseMap = baseOram.readAndRemove(subIdentifier(iden, baseOram));
			int ithPos = iden/(1<<baseOram.lengthOfIden);
			int start = ithPos*clients.get(level-1).lengthOfPos;
			int end = (ithPos+1)*clients.get(level-1).lengthOfPos;
			boolean[] pos = extract(baseMap, start, end);
			
			boolean[] newPos = randBools(end-start);
			put(baseMap, start, newPos);
			baseOram.putBack(subIdentifier(iden, baseOram), baseMap);
			return new boolean[][]{pos, newPos};
		}
		else {
			PathOramClient<T> currentOram = clients.get(level);
			
			boolean[][] poses = travelToDeep(subIdentifier(iden, currentOram), level+1);
			//System.out.println(" tr "+level+" "+iden+" "+Utils.toInt(poses[0]) + " "+Utils.toInt(poses[1]));
			
			sendBooleans(poses[0]);
			boolean[] data = currentOram.readAndRemove(subIdentifier(iden, currentOram), poses[0]);
			
			int ithPos = iden/(1<<currentOram.lengthOfIden);
			int start = ithPos*clients.get(level-1).lengthOfPos;
			int end = (ithPos+1)*clients.get(level-1).lengthOfPos;
			
			boolean[] pos = extract(data, start, end);
			boolean[] tmpNewPos = randBools(end-start);
			put(data, start, tmpNewPos);
			currentOram.putBack(subIdentifier(iden, currentOram), poses[1], data);
			return new boolean[][]{pos, tmpNewPos};
		}
	}
	
	public int subIdentifier(int iden, OramParty<T> o) {
		int a = (iden % (1<<o.lengthOfIden)) ;
		return a;
	}
	
	public boolean[] extract(boolean[] array, int s, int e) {
		return Arrays.copyOfRange(array, s, e);
	}
	
	public void put(boolean[] array, int s, boolean[] content) {
		System.arraycopy(content, 0, array, s, content.length);
	}
	
	public boolean[] randBools(int length) {
		boolean[] res = new boolean[length];
		for(int i = 0; i < length; ++i)
			res[i] = rng.nextBoolean();
		return res;
	}

	void sendBooleans(boolean[] pos) throws IOException {
		//send pos to server
		os.write(new byte[]{(byte) pos.length});
		byte[] tmp = new byte[pos.length];
		for(int i = 0; i < pos.length; ++i)
			tmp[i] = (byte) (pos[i] ? 1 : 0);
		os.write(tmp);
	}
}
