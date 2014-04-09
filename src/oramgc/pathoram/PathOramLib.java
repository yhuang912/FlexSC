package oramgc.pathoram;

import java.security.SecureRandom;
import java.util.Arrays;

import oramgc.Block;
import oramgc.BucketLib;
import test.Utils;
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
		return incrementByOne(leadingZeros(xored)); 
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path, Signal[] iden) throws Exception {
		Signal[] deep = deepestLevel(pos, path);
		Signal e = eq(iden, zeros(lengthOfIden));
		return mux(deep, zeros(deep.length), e);
	}
	
	public Block readAndRemove(Block[] path, Signal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	public Block toBlock(Signal[] b){
		return new Block(Arrays.copyOfRange(b, 0, lengthOfIden), 
				Arrays.copyOfRange(b, lengthOfIden, lengthOfIden+lengthOfPos),
				Arrays.copyOfRange(b, lengthOfIden+lengthOfPos, lengthOfIden+lengthOfPos+lengthOfData));
	}
	
	public Signal[] toSignals(Block b) {
		Signal[] res = new Signal[lengthOfIden+lengthOfPos+b.data.length];
		System.arraycopy(b.iden, 0, res, 0, lengthOfIden);
		System.arraycopy(b.pos, 0, res, lengthOfIden, lengthOfPos);
		System.arraycopy(b.data, 0, res, lengthOfIden+lengthOfPos,lengthOfData);
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
			path[i] = toBlock(data[i]);
		
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
							Arrays.copyOfRange(blockInSignal[i], 0, lengthOfIden));
		
		
		sortWithPayload(deepestLevel, blockInSignal, SIGNAL_ZERO);
		
		blockInSignal = pushDownHelp(deepestLevel, blockInSignal);
		dissemble(blockInSignal, path, stash);
		return deepestLevel;
	}
	public Signal[][] pushDownHelp(Signal[][] deepestLevel, Signal[][] blockInSignal) throws Exception {
		int width = deepestLevel[0].length;

		int index = 0;
		for(int i = logN; i > 0 ; --i)
			for(int j = 0; j < capacity; ++j) {
				deepestLevel[index] = min(deepestLevel[index], toSignals(i, width));
				++index;
			}
		
		Signal[][] key = keyAssign(deepestLevel);
			
		Signal[][] extended = new Signal[blockInSignal.length+capacity*logN][blockInSignal[0].length];
		System.arraycopy(blockInSignal, 0, extended, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i)
			extended[blockInSignal.length+i] = zeros(blockInSignal[0].length);
		
		Signal[][] extendedKey = new Signal[blockInSignal.length+capacity*logN][];
		System.arraycopy(key, 0, extendedKey, 0, blockInSignal.length);
		for(int i = 0; i < capacity*logN; ++i)
			extendedKey[blockInSignal.length+i] = toSignals(i+capacity, width+logCapacity);

		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		
		for(int i = 0; i < extendedKey.length-1; ++i) {
			Signal eqSignal = eq(extendedKey[i], extendedKey[i+1]);
			Signal iIsDummy = eq(Arrays.copyOfRange(extended[i], 0, lengthOfIden), zeros(lengthOfIden));
			extendedKey[i] = mux(extendedKey[i], zeros(width+2), and(eqSignal, iIsDummy));
			extendedKey[i+1] = mux(extendedKey[i+1], zeros(width+2), and(eqSignal, not(iIsDummy)));
		}
		
		sortWithPayload(extendedKey, extended, SIGNAL_ZERO);
		//for(int i = 0; i < blockInSignal.length; ++i)
		//	blockInSignal[i] = mux(zeros(blockInSignal[0].length), blockInSignal[i], SIGNAL_ONE);
		
		return Arrays.copyOfRange(extended, 0, blockInSignal.length);
		//deepestLevel = extendedKey;
		//blockInSignal = extended;
		//deepestLevel = Arrays.copyOfRange(extendedKey, 0, deepestLevel.length);
		//blockInSignal = Arrays.copyOfRange(extended, 0, blockInSignal.length);
		//return extendedKey;
	}
	public Signal[][] keyAssign(Signal[][] x) throws Exception {
		int width = x[0].length;
		Signal[] level = toSignals(4, width);
		Signal[] c = toSignals(0, width);
		Signal[][] b = new Signal[x.length][width];
		for(int i = 0; i < x.length; ++i) {
			Signal cIs4 = eq(c, toSignals(4, width));
			Signal xiSmallerThanLevel = not(geq(x[i], level));
			Signal condition = or(cIs4, xiSmallerThanLevel);
			level = mux(level, sub(level, toSignals(1, width)), condition);
			c = mux(add(c, toSignals(1, width)), toSignals(1, width), condition);
			level = min(level, x[i]);
			b[i] = level;
		}
		
		Signal[][] bb = new Signal[x.length][];
		for(int i = 0; i < bb.length; ++i){
			bb[i] = leftPublicShift(padSignal(b[i], width+2), 2);
		}
		c = toSignals(0, width+2);
		for(int i  = 1; i < x.length; ++i) {
			Signal same = eq(b[i], b[i-1]);
			c = mux(toSignals(0,width+2), add(c, toSignals(1, width+2)), same);
			bb[i] = add(bb[i], c);
		}
		return bb;
	}
	
	public static void main(String [ ] args) throws Exception {
		int[] a = new int[]{4,4,4,4,4,3,3,3,3,1,1,1,0,0,0,0};
		int c = 0; int[]b = new int[a.length];
		b[0] = a[0];
		int level = 4;
		for(int i = 0; i < a.length; ++i){
			if(c==4 || a[i]<level) {
				level--;
				c=1;
				}
			else c++;
			level = level < a[i] ? level: a[i];
			b[i] = level;
		}
		int[] bb = new int[a.length];
		for(int i = 0; i < b.length;++i)
			bb[i] = b[i]*4;
		c=0;
		for(int i  = 1; i < a.length; ++i) {
			if(b[i-1] == b[i]){
				++c;
			}
			else{
				c=0;
			}
			bb[i]+=c;
		}
		int[]bbb = new int[2*a.length];
		for(int i = 0; i < a.length; ++i){
			bbb[i] = bb[i];
			bbb[i+a.length] = i;
		}
		Arrays.sort(bbb);
		
		
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(b));
		System.out.println(Arrays.toString(bb));
		System.out.println(Arrays.toString(bbb));
	}

}