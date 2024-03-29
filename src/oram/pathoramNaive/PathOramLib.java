package oram.pathoramNaive;

import java.security.SecureRandom;
import java.util.Arrays;
import oram.Block;
import oram.BucketLib;
import flexsc.CompEnv;

public class PathOramLib<T> extends BucketLib<T> {

	int logN;
	final int logCapacity = 2;
	final int capacity = 4;
	SecureRandom rng = new SecureRandom();
	public PathOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN,
			CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
	}
	
	public T[] deepestLevel(T[] pos, T[] path) throws Exception {
		T[] xored = xor(pos, path);
		T[] result = xored;
		for(int i = result.length-2; i>=0; --i) {
			result[i] = or(result[i], result[i+1]);
		}	
		return padSignal(result, result.length+1);
	}
		
	public T[] deepestLevel(T[] pos, T[] path, T isDummy) throws Exception {
		T[] depth = deepestLevel(pos, path);
		return mux(depth, ones(depth.length), isDummy);
	}
	
	public Block<T> readAndRemove(Block<T>[] path, T[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	public Block<T> toBlock(T[] b){
		return new Block<T>(Arrays.copyOfRange(b, 0, lengthOfIden), 
				Arrays.copyOfRange(b, lengthOfIden, lengthOfIden+lengthOfPos),
				Arrays.copyOfRange(b, lengthOfIden+lengthOfPos, lengthOfIden+lengthOfPos+lengthOfData),
				b[b.length-1]
				);
	}
	
	public T[] toSignals(Block<T> b) {
		T[] res = env.newTArray(lengthOfIden+lengthOfPos+b.data.length+1);
		System.arraycopy(b.iden, 0, res, 0, lengthOfIden);
		System.arraycopy(b.pos, 0, res, lengthOfIden, lengthOfPos);
		System.arraycopy(b.data, 0, res, lengthOfIden+lengthOfPos,lengthOfData);
		res[res.length-1] = b.isDummy;
		return res;
	}
	
	public T[][] assemble(Block<T>[] path, Block<T>[] stash) {
		T[][] res = env.newTArray(path.length+stash.length, 0);//new T[path.length+stash.length][];
		for(int i = 0; i < path.length; ++i)
			res[i] = toSignals(path[i]);
		
		for(int i = 0; i < stash.length; ++i)
			res[i+path.length] = toSignals(stash[i]);
		return res;
	}
	
	public void dissemble(T[][] data, Block<T>[] path, Block<T>[] stash){
		for(int i = 0; i < path.length; ++i)
			path[i] = toBlock(data[path.length-i-1]);//here we sort them in reverse order, so we put them in reverse order.
		
		for(int i = 0; i < stash.length; ++i)
			stash[i] = toBlock(data[i+path.length]);
	}
	
	public void pushDown(Block<T>[] path, Block<T>[] stash, boolean[] pos) throws Exception {
		assert(path.length == capacity * logN):"length of path not correct";
		
		T[][] blockInSignal = assemble(path, stash); //transform every block to signal array and merge them.
		T[][] newPath = env.newTArray(path.length, 0);//new Signal[blockInSignal.length][];
		for(int i = 0; i < newPath.length; ++i){
			newPath[i] = zeros(blockInSignal[0].length);
			newPath[i][newPath[i].length-1] = SIGNAL_ONE;
		}
		
		T[] posInSignal = env.newTArray(lengthOfPos);//new Signal[lengthOfPos];
		for(int i = 0; i < pos.length; ++i)
			posInSignal[i] = pos[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		
		T[][] canPushm = env.newTArray(blockInSignal.length, 0);
		for(int k = 0; k < blockInSignal.length; ++k) {
			canPushm[k] = deepestLevel(
					Arrays.copyOfRange(blockInSignal[k], lengthOfIden, lengthOfIden + lengthOfPos),
					posInSignal);//,blockInSignal[k][blockInSignal[k].length-1]);			
		}

		for(int k = 0; k < blockInSignal.length; ++k) {
			T pushed = SIGNAL_ZERO;
			for(int i = logN; i >=1; --i) {
				T canPush = not(canPushm[k][canPushm[k].length-i]);//geq(depth[k], toSignals(i-1,depth.length));
				canPush = and(canPush, not(blockInSignal[k][blockInSignal[k].length-1] ));
				T toPush = and(canPush, not(pushed));
				for(int j = 0; j < capacity; ++j){
					
				}
			}
		}
		int cnt = newPath.length-1;
		for(int i = logN; i >=1; --i)
			for(int j = 0; j < capacity; ++j){
				T pushed = SIGNAL_ZERO;
				for(int k = 0; k < blockInSignal.length; ++k) {
					T canPush = not(canPushm[k][canPushm[k].length-i]);//geq(depth[k], toSignals(i-1,depth.length));
					canPush = and(canPush, not(blockInSignal[k][blockInSignal[k].length-1] ));
					T toPush = and(canPush, not(pushed));
					//canPushm[k] = mux(canPushm[k], ones(canPushm[k].length), toPush);
					newPath[cnt] = mux(newPath[cnt], blockInSignal[k], toPush);
					blockInSignal[k][blockInSignal[k].length-1] = mux(blockInSignal[k][blockInSignal[k].length-1], SIGNAL_ONE, toPush);
					pushed  = or(pushed, canPush);	
				}
				cnt--;
			}
			
		T[][] newStash = env.newTArray(stash.length, 0);//new Signal[blockInSignal.length][];
		for(int i = 0; i < newStash.length; ++i){
			newStash[i] = zeros(blockInSignal[0].length);
			newStash[i][newStash[i].length-1] = SIGNAL_ONE;
		}
		Block<T>[] StashBlocks = new Block[stash.length];
		for(int i = 0; i < stash.length; ++i)
			StashBlocks[i] = toBlock(newStash[i]);
		
		for(int i = 0; i < blockInSignal.length; ++i)
			add(StashBlocks, toBlock(blockInSignal[i]));
		
		//dissemble(blockInSignal, path, stash);
		for(int i = 0; i < path.length; ++i)
			path[i] = toBlock(newPath[i]);
		
		for(int i = 0; i < stash.length; ++i)
			stash[i] = StashBlocks[i];
	}

}