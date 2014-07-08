package PrivateOram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import test.Utils;
import flexsc.Mode;
import flexsc.Party;

public class RecursiveCircuitOram<T> {
	TrivialPrivateOram<T> baseOram;
	public ArrayList<CircuitOram<T>> clients = new ArrayList<>();
	int initialLengthOfIden;
	int recurFactor;
	int cutoff;
	int capacity;

	SecureRandom rng = new SecureRandom();
	protected InputStream is;
	protected OutputStream os;
	Party p;
	public RecursiveCircuitOram(InputStream is, OutputStream os, int N, int dataSize, int cutoff, int recurFactor, 
			int capacity, Mode m, int sp, Party p) throws Exception {
		this.p = p;
		this.is = is;
		this.os = os;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		CircuitOram<T>  oram = new CircuitOram<T>(is, os, N, dataSize, p, capacity, m, sp);
		clients.add(oram);
		int newDataSize = oram.lengthOfPos, newN = (1<<oram.lengthOfIden);
		while(newN > cutoff) {
			newDataSize = oram.lengthOfPos * recurFactor;
			newN = (1<<oram.lengthOfIden)  / recurFactor;
			oram = new CircuitOram<T>(is, os, newN, newDataSize, p, capacity, m, sp);
			clients.add(oram);
		}
		CircuitOram<T> last = clients.get(clients.size()-1);
		baseOram = new TrivialPrivateOram<T>(is, os, (1<<last.lengthOfIden), last.lengthOfPos, m, p);
		System.out.println(clients.size());
	}

	public T[] read(T[] iden) throws Exception {
		T[][] poses = travelToDeep(iden, 1);
		CircuitOram<T> currentOram = clients.get(0);

		boolean[] oldPos = clients.get(clients.size()-1).env.outputToAlice(poses[0]);
		
		syncBooleans(oldPos);
		if(p == Party.Alice)
			System.out.println("read " + Utils.toInt(oldPos));// + " "+Utils.toInt(poses[1]));
		T[] res = currentOram.read(iden, oldPos, poses[1]);
		return res;
	}
	
	public void write(T[] iden, T[] data) throws Exception {
		T[][] poses = travelToDeep(iden, 1);
		CircuitOram<T> currentOram = clients.get(0);

		boolean[] oldPos = clients.get(clients.size()-1).env.outputToAlice(poses[0]);
		syncBooleans(oldPos);
		if(p == Party.Alice)
			System.out.println("write " + Utils.toInt(oldPos));// + " "+Utils.toInt(poses[1]));
		currentOram.write(iden, oldPos, poses[1], data);
	}
	
	public T[][] travelToDeep(T[] iden, int level) throws Exception {
		if(level == clients.size()) {
			T[] baseMap = baseOram.readAndRemove(subIdentifier(iden, baseOram));
			T[] ithPos = baseOram.lib.rightPublicShift(iden, baseOram.lengthOfIden);//iden>>baseOram.lengthOfIden;
			T[] pos = extract(baseMap, ithPos, clients.get(level-1).lengthOfPos);

			T[] newPos = randBools(clients.get(level-1).lengthOfPos);
			put(baseMap, ithPos, newPos);
			baseOram.putBack(subIdentifier(iden, baseOram), baseMap);
			os.flush();
			T[][] result = baseOram.env.newTArray(2, 0);
			result[0] = pos;
			result[1] = newPos;
			return result;
		}
		else {
			CircuitOram<T> currentOram = clients.get(level);

			T[][] poses = travelToDeep(subIdentifier(iden, currentOram), level+1);
			//System.out.println(" tr "+level+" "+iden+" "+Utils.toInt(poses[0]) + " "+Utils.toInt(poses[1]));
			//			sendBooleans(poses[0]);
			boolean[] oldPos = clients.get(clients.size()-1).env.outputToAlice(poses[0]);
			syncBooleans(oldPos);

			T[] data = currentOram.readAndRemove(subIdentifier(iden, currentOram), oldPos);
			T[] ithPos = currentOram.lib.rightPublicShift(iden, currentOram.lengthOfIden);//iden>>currentOram.lengthOfIden;//iden/(1<<currentOram.lengthOfIden);

			T[] pos = extract(data, ithPos, clients.get(level-1).lengthOfPos);
			T[] tmpNewPos = randBools(clients.get(level-1).lengthOfPos);
			put(data, ithPos, tmpNewPos);
			currentOram.putBack(subIdentifier(iden, currentOram), poses[1], data);
			T[][] result = currentOram.env.newTArray(2, 0);
			result[0] = pos;
			result[1] = tmpNewPos;
			return result;
		}
	}
	
	public T[] subIdentifier(T[] iden, OramParty<T> o) {
		//		int a = iden & ((1<<o.lengthOfIden)-1);//(iden % (1<<o.lengthOfIden)) ;
		return o.lib.padSignal(iden, o.lengthOfIden);
	}
	
	public T[] extract(T[] array, T[] ithPos, int length) throws Exception {
		int numberOfEntry = array.length/length;
		T[] result = Arrays.copyOfRange(array, 0, length);
		for(int i = 1; i < numberOfEntry; ++i) {
			T hit = baseOram.lib.eq(baseOram.lib.toSignals(i, ithPos.length), ithPos);
			result = baseOram.lib.mux(result, Arrays.copyOfRange(array, i*length, (i+1)*length), hit);
		}
		return result;
	}
	
	public void put(T[] array, T[] ithPos, T[] content) throws Exception {
		int numberOfEntry = array.length/content.length;
		for(int i = 0; i < numberOfEntry; ++i) {
			T hit = baseOram.lib.eq(baseOram.lib.toSignals(i, ithPos.length), ithPos);
			T[] tmp = baseOram.lib.mux(Arrays.copyOfRange(array, i*content.length, (i+1)*content.length), content, hit);
			System.arraycopy(tmp, 0, array, i*content.length, content.length);
		}
	}

	public T[] randBools(int length) throws Exception {
		boolean[] res = new boolean[length];
		for(int i = 0; i < length; ++i)
			res[i] = rng.nextBoolean();
		T[] alice = baseOram.env.inputOfAlice(res); 
		T[] bob = baseOram.env.inputOfBob(res);
		return baseOram.lib.xor(alice, bob);
//		return baseOram.env.inputOfAlice(res);
	}

	void syncBooleans(boolean[] pos) throws IOException {
		if(p == Party.Alice){
			//send pos to server
			os.write(new byte[]{(byte) pos.length});
			byte[] tmp = new byte[pos.length];
			for(int i = 0; i < pos.length; ++i)
				tmp[i] = (byte) (pos[i] ? 1 : 0);
			os.write(tmp);
			os.flush();
		}
		else {
			byte[] l = new byte[1];
			is.read(l);
			byte tmp[] = new byte[l[0]];
			is.read(tmp);
			pos = new boolean[l[0]];
			for(int k = 0; k < tmp.length; ++k) {
				pos[k] = ((tmp[k] - 1) == 0);
			}
		}
	}
}
