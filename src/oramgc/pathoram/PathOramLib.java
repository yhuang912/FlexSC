package oramgc.pathoram;

import java.security.SecureRandom;
import java.util.Arrays;

import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.Signal;

public class PathOramLib extends BucketLib {

	int logN;
	final int logCapacity = 2;
	final int capacity = 4;
	SecureRandom rng = new SecureRandom();
	public PathOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN,
			CompEnv<Signal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path) throws Exception {
		Signal[] xored = xor(pos, path);
		
		return padSignal(
				leadingZeros(padSignal(xored, xored.length+1))
				, lengthOfPos+1);
		
	}
		
	public Signal[] deepestLevel(Signal[] pos, Signal[] path, Signal isDummy) throws Exception {
		Signal[] depth = deepestLevel(pos, path);
		return mux(depth, zeros(depth.length), isDummy);
	}	
	
	public Block readAndRemove(Block[] path, Signal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	public Block toBlock(Signal[] b){
		return new Block(Arrays.copyOfRange(b, 0, lengthOfIden), 
				Arrays.copyOfRange(b, lengthOfIden, lengthOfIden+lengthOfPos),
				Arrays.copyOfRange(b, lengthOfIden+lengthOfPos, lengthOfIden+lengthOfPos+lengthOfData),
				b[b.length-1]
				);
	}
	
	public Signal[] toSignals(Block b) {
		Signal[] res = new Signal[lengthOfIden+lengthOfPos+b.data.length+1];
		System.arraycopy(b.iden, 0, res, 0, lengthOfIden);
		System.arraycopy(b.pos, 0, res, lengthOfIden, lengthOfPos);
		System.arraycopy(b.data, 0, res, lengthOfIden+lengthOfPos,lengthOfData);
		res[res.length-1] = b.isDummy;
		return res;
	}
	
	public Signal[][] assemble(Block[] path, Block[] stash) {
		Signal[][] res = new Signal[path.length+stash.length][];
		for(int i = 0; i < path.length; ++i)
			res[i] = toSignals(path[i]);
		
		for(int i = 0; i < stash.length; ++i)
			res[i+path.length] = toSignals(stash[i]);
		return res;
	}
	
	public void dissemble(Signal[][] data, Block[] path, Block[] stash){
		for(int i = 0; i < path.length; ++i)
			path[i] = toBlock(data[path.length-i-1]);//here we sort them in reverse order, so we put them in reverse order.
		
		for(int i = 0; i < stash.length; ++i)
			stash[i] = toBlock(data[i+path.length]);
	}
	
	public Signal[][] pushDown(Block[] path, Block[] stash, boolean[] pos) throws Exception {
		assert(path.length == capacity * logN):"length of path not correct";
		
		Signal[][] blockInSignal = assemble(path, stash); //transform every block to signal array and merge them.
		Signal[][] deepestLevel = new Signal[blockInSignal.length][];
		
		Signal[] posInSignal = new Signal[lengthOfPos];
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
	
	public Signal[][] pushDownHelp(Signal[][] deepestLevel, Signal[][] blockInSignal) throws Exception {
		int width = deepestLevel[0].length;
		
		Signal[][] key = keyAssign(deepestLevel);
		
		Signal[][] extended = new Signal[blockInSignal.length+capacity*logN][blockInSignal[0].length];
		System.arraycopy(blockInSignal, 0, extended, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i){
			extended[blockInSignal.length+i] = zeros(blockInSignal[0].length);
			extended[blockInSignal.length+i][extended[blockInSignal.length+i].length-1] = SIGNAL_ONE;
		}
		
		Signal[][] extendedKey = new Signal[blockInSignal.length+capacity*logN][];
		System.arraycopy(key, 0, extendedKey, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i)
			extendedKey[blockInSignal.length+i] = toSignals(i+capacity, width+logCapacity);

		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		
		
		for(int i = 0; i < extendedKey.length-1; ++i) {
			Signal eqSignal = eq(extendedKey[i], extendedKey[i+1]);
			Signal iIsDummy = extended[i][extended[i].length-1];//eq(Arrays.copyOfRange(extended[i], 0, lengthOfIden), zeros(lengthOfIden));
			extendedKey[i] = mux(extendedKey[i], zeros(width+2), and(eqSignal, iIsDummy));
			extendedKey[i+1] = mux(extendedKey[i+1], zeros(width+2), and(eqSignal, not(iIsDummy)));
		}
		
		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		deepestLevel = Arrays.copyOfRange(extendedKey, 0, deepestLevel.length);
		return Arrays.copyOfRange(extended, 0, blockInSignal.length);
	}
	
	public Signal[][] keyAssign(Signal[][] x) throws Exception {
		int width = x[0].length;
		
		Signal[] level = toSignals(logN, width);
		Signal[] c = toSignals(0, width);
		Signal[][] b = new Signal[x.length][];
		for(int i = 0; i < x.length; ++i) {
			Signal cIs4 = eq(c, toSignals(4, width));
			Signal xiSmallerThanLevel = not(geq(x[i], level));
			Signal condition = or(cIs4, xiSmallerThanLevel);
			level = mux(level, sub(level, toSignals(1, width)), condition);
			c = mux(add(c, toSignals(1, width)), toSignals(1, width), condition);
			level = min(level, x[i]);
			level = mux(level, zeros(width), level[width-1]);
			b[i] = level;
		}
		
		int newWidth = width + 2;
		Signal[][] bb = new Signal[x.length][];
		for(int i = 0; i < bb.length; ++i){
			bb[i] = leftPublicShift(padSignal(b[i], newWidth), 2);
		}
		c = toSignals(0, newWidth);
		for(int i  = 1; i < x.length; ++i) {
			Signal same = eq(b[i], b[i-1]);
			c = mux(toSignals(0, newWidth), add(c, toSignals(1, newWidth)), same);
			c = mux(c, toSignals(0, newWidth), eq(b[i], toSignals(0, b[i].length)));
			bb[i] = add(bb[i], c);
		}
		return bb;
	}

}