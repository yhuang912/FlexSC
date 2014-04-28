package oramgc.pathoram;

import java.security.SecureRandom;
import java.util.Arrays;

import oramgc.Block;
import oramgc.BucketLib;
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
		
		return padSignal(
				leadingZeros(padSignal(xored, xored.length+1))
				, lengthOfPos+1);
		
	}
		
	public T[] deepestLevel(T[] pos, T[] path, T isDummy) throws Exception {
		T[] depth = deepestLevel(pos, path);
		return mux(depth, zeros(depth.length), isDummy);
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
	
	public T[][] pushDown(Block<T>[] path, Block<T>[] stash, boolean[] pos) throws Exception {
		assert(path.length == capacity * logN):"length of path not correct";
		
		T[][] blockInSignal = assemble(path, stash); //transform every block to signal array and merge them.
		T[][] deepestLevel = env.newTArray(blockInSignal.length, 0);//new T[blockInSignal.length][];
		
		T[] posInSignal = env.newTArray(lengthOfPos);
		for(int i = 0; i < pos.length; ++i)
			posInSignal[i] = pos[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		
		for(int i = 0; i < blockInSignal.length; ++i)
			deepestLevel[i] = deepestLevel(
						Arrays.copyOfRange(blockInSignal[i], lengthOfIden, lengthOfIden + lengthOfPos),
						posInSignal,
						blockInSignal[i][blockInSignal[i].length-1] );
		
		sortWithPayload(deepestLevel, blockInSignal, SIGNAL_ZERO);
		blockInSignal = pushDownHelp(deepestLevel, blockInSignal);
		dissemble(blockInSignal, path, stash);
		return deepestLevel;
	}
	
	public T[][] pushDownHelp(T[][] deepestLevel, T[][] blockInSignal) throws Exception {
		int width = deepestLevel[0].length;
		
		T[][] key = keyAssign(deepestLevel);
		
		T[][] extended = env.newTArray(blockInSignal.length+capacity*logN, blockInSignal[0].length);//new T[][];
		System.arraycopy(blockInSignal, 0, extended, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i){
			extended[blockInSignal.length+i] = zeros(blockInSignal[0].length);
			extended[blockInSignal.length+i][extended[blockInSignal.length+i].length-1] = SIGNAL_ONE;
		}
		
		T[][] extendedKey = env.newTArray(blockInSignal.length+capacity*logN, 0);
		System.arraycopy(key, 0, extendedKey, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i)
			extendedKey[blockInSignal.length+i] = toSignals(i+capacity, width+logCapacity);

		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		
		
		for(int i = 0; i < extendedKey.length-1; ++i) {
			T eqSignal = eq(extendedKey[i], extendedKey[i+1]);
			T iIsDummy = extended[i][extended[i].length-1];//eq(Arrays.copyOfRange(extended[i], 0, lengthOfIden), zeros(lengthOfIden));
			extendedKey[i] = mux(extendedKey[i], zeros(width+2), and(eqSignal, iIsDummy));
			extendedKey[i+1] = mux(extendedKey[i+1], zeros(width+2), and(eqSignal, not(iIsDummy)));
		}
		
		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		deepestLevel = Arrays.copyOfRange(extendedKey, 0, deepestLevel.length);
		return Arrays.copyOfRange(extended, 0, blockInSignal.length);
	}
	
	public T[][] keyAssign(T[][] x) throws Exception {
		int width = x[0].length;
		
		T[] level = toSignals(logN, width);
		T[] c = toSignals(0, width);
		T[][] b = env.newTArray(x.length, 0);
		for(int i = 0; i < x.length; ++i) {
			T cIs4 = eq(c, toSignals(4, width));
			T xiSmallerThanLevel = not(geq(x[i], level));
			T condition = or(cIs4, xiSmallerThanLevel);
			level = mux(level, sub(level, toSignals(1, width)), condition);
			c = mux(add(c, toSignals(1, width)), toSignals(1, width), condition);
			level = min(level, x[i]);
			level = mux(level, zeros(width), level[width-1]);
			b[i] = level;
		}
		
		int newWidth = width + 2;
		T[][] bb = env.newTArray(x.length, 0); //new T[][];
		for(int i = 0; i < bb.length; ++i){
			bb[i] = leftPublicShift(padSignal(b[i], newWidth), 2);
		}
		c = toSignals(0, newWidth);
		for(int i  = 1; i < x.length; ++i) {
			T same = eq(b[i], b[i-1]);
			c = mux(toSignals(0, newWidth), add(c, toSignals(1, newWidth)), same);
			c = mux(c, toSignals(0, newWidth), eq(b[i], toSignals(0, b[i].length)));
			bb[i] = add(bb[i], c);
		}
		return bb;
	}

}