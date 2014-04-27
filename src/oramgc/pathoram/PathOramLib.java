package oramgc.pathoram;

import java.security.SecureRandom;
import java.util.Arrays;

import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.GCSignal;

public class PathOramLib extends BucketLib {

	int logN;
	final int logCapacity = 2;
	final int capacity = 4;
	SecureRandom rng = new SecureRandom();
	public PathOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN,
			CompEnv<GCSignal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
	}
	
	public GCSignal[] deepestLevel(GCSignal[] pos, GCSignal[] path) throws Exception {
		GCSignal[] xored = xor(pos, path);
		
		return padSignal(
				leadingZeros(padSignal(xored, xored.length+1))
				, lengthOfPos+1);
		
	}
		
	public GCSignal[] deepestLevel(GCSignal[] pos, GCSignal[] path, GCSignal isDummy) throws Exception {
		GCSignal[] depth = deepestLevel(pos, path);
		return mux(depth, zeros(depth.length), isDummy);
	}	
	
	public Block readAndRemove(Block[] path, GCSignal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	public Block toBlock(GCSignal[] b){
		return new Block(Arrays.copyOfRange(b, 0, lengthOfIden), 
				Arrays.copyOfRange(b, lengthOfIden, lengthOfIden+lengthOfPos),
				Arrays.copyOfRange(b, lengthOfIden+lengthOfPos, lengthOfIden+lengthOfPos+lengthOfData),
				b[b.length-1]
				);
	}
	
	public GCSignal[] toSignals(Block b) {
		GCSignal[] res = new GCSignal[lengthOfIden+lengthOfPos+b.data.length+1];
		System.arraycopy(b.iden, 0, res, 0, lengthOfIden);
		System.arraycopy(b.pos, 0, res, lengthOfIden, lengthOfPos);
		System.arraycopy(b.data, 0, res, lengthOfIden+lengthOfPos,lengthOfData);
		res[res.length-1] = b.isDummy;
		return res;
	}
	
	public GCSignal[][] assemble(Block[] path, Block[] stash) {
		GCSignal[][] res = new GCSignal[path.length+stash.length][];
		for(int i = 0; i < path.length; ++i)
			res[i] = toSignals(path[i]);
		
		for(int i = 0; i < stash.length; ++i)
			res[i+path.length] = toSignals(stash[i]);
		return res;
	}
	
	public void dissemble(GCSignal[][] data, Block[] path, Block[] stash){
		for(int i = 0; i < path.length; ++i)
			path[i] = toBlock(data[path.length-i-1]);//here we sort them in reverse order, so we put them in reverse order.
		
		for(int i = 0; i < stash.length; ++i)
			stash[i] = toBlock(data[i+path.length]);
	}
	
	public GCSignal[][] pushDown(Block[] path, Block[] stash, boolean[] pos) throws Exception {
		assert(path.length == capacity * logN):"length of path not correct";
		
		GCSignal[][] blockInSignal = assemble(path, stash); //transform every block to signal array and merge them.
		GCSignal[][] deepestLevel = new GCSignal[blockInSignal.length][];
		
		GCSignal[] posInSignal = new GCSignal[lengthOfPos];
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
	
	public GCSignal[][] pushDownHelp(GCSignal[][] deepestLevel, GCSignal[][] blockInSignal) throws Exception {
		int width = deepestLevel[0].length;
		
		GCSignal[][] key = keyAssign(deepestLevel);
		
		GCSignal[][] extended = new GCSignal[blockInSignal.length+capacity*logN][blockInSignal[0].length];
		System.arraycopy(blockInSignal, 0, extended, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i){
			extended[blockInSignal.length+i] = zeros(blockInSignal[0].length);
			extended[blockInSignal.length+i][extended[blockInSignal.length+i].length-1] = SIGNAL_ONE;
		}
		
		GCSignal[][] extendedKey = new GCSignal[blockInSignal.length+capacity*logN][];
		System.arraycopy(key, 0, extendedKey, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i)
			extendedKey[blockInSignal.length+i] = toSignals(i+capacity, width+logCapacity);

		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		
		
		for(int i = 0; i < extendedKey.length-1; ++i) {
			GCSignal eqSignal = eq(extendedKey[i], extendedKey[i+1]);
			GCSignal iIsDummy = extended[i][extended[i].length-1];//eq(Arrays.copyOfRange(extended[i], 0, lengthOfIden), zeros(lengthOfIden));
			extendedKey[i] = mux(extendedKey[i], zeros(width+2), and(eqSignal, iIsDummy));
			extendedKey[i+1] = mux(extendedKey[i+1], zeros(width+2), and(eqSignal, not(iIsDummy)));
		}
		
		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		deepestLevel = Arrays.copyOfRange(extendedKey, 0, deepestLevel.length);
		return Arrays.copyOfRange(extended, 0, blockInSignal.length);
	}
	
	public GCSignal[][] keyAssign(GCSignal[][] x) throws Exception {
		int width = x[0].length;
		
		GCSignal[] level = toSignals(logN, width);
		GCSignal[] c = toSignals(0, width);
		GCSignal[][] b = new GCSignal[x.length][];
		for(int i = 0; i < x.length; ++i) {
			GCSignal cIs4 = eq(c, toSignals(4, width));
			GCSignal xiSmallerThanLevel = not(geq(x[i], level));
			GCSignal condition = or(cIs4, xiSmallerThanLevel);
			level = mux(level, sub(level, toSignals(1, width)), condition);
			c = mux(add(c, toSignals(1, width)), toSignals(1, width), condition);
			level = min(level, x[i]);
			level = mux(level, zeros(width), level[width-1]);
			b[i] = level;
		}
		
		int newWidth = width + 2;
		GCSignal[][] bb = new GCSignal[x.length][];
		for(int i = 0; i < bb.length; ++i){
			bb[i] = leftPublicShift(padSignal(b[i], newWidth), 2);
		}
		c = toSignals(0, newWidth);
		for(int i  = 1; i < x.length; ++i) {
			GCSignal same = eq(b[i], b[i-1]);
			c = mux(toSignals(0, newWidth), add(c, toSignals(1, newWidth)), same);
			c = mux(c, toSignals(0, newWidth), eq(b[i], toSignals(0, b[i].length)));
			bb[i] = add(bb[i], c);
		}
		return bb;
	}

}