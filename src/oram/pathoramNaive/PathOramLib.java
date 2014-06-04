package oram.pathoramNaive;

import java.security.SecureRandom;
import java.util.Arrays;
import oram.Block;
import oram.BucketLib;
import flexsc.CompEnv;

public class PathOramLib<T> extends BucketLib<T> {
	int logN;
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

	public void pushDown(Block<T>[][] path, Block<T>[] stash, boolean[] pos) throws Exception {
		Block<T>[] temp = newBlockArray(path.length*path[0].length+stash.length);
		int cnt = 0;
		for(int i = 0; i < path.length; ++i)
			for(int j = 0; j < path[i].length; ++j)
				temp[cnt++] = path[i][j];
		for(int i = 0; i < stash.length; ++i)
			temp[cnt++] = stash[i];
		
		Block<T>[][] newPath = newBlockMatrix(path.length,0);
		T[][] canPushTemp = env.newTArray(temp.length, 0);
		for(int i = 0; i < path.length; ++i) {
			newPath[i] = newBlockArray(path[i].length);

		}

		T[] posInSignal = env.newTArray(lengthOfPos);//new Signal[lengthOfPos];
		for(int i = 0; i < pos.length; ++i)
			posInSignal[i] = pos[i] ? SIGNAL_ONE : SIGNAL_ZERO;


		for(int i = 0; i < path.length; ++i) {
			for(int j = 0; j < path[i].length; ++j) {
				newPath[i][j] = dummyBlock;
			}			
		}
		for(int i = 0; i < temp.length; ++i) {
			canPushTemp[i] = deepestLevel( temp[i].pos, posInSignal);			
		}

		for(int i = newPath.length-1; i >= 0; --i)
			for(int j = 0; j < newPath[i].length; ++j) {
				T pushed = SIGNAL_ZERO;
				for(int k = 0; k < temp.length; ++k) {
					T canPush = not(canPushTemp[k][canPushTemp[k].length-i-1]);//geq(depth[k], toSignals(i-1,depth.length));
					canPush = and(canPush, not(temp[k].isDummy ));
					T toPush = and(canPush, not(pushed));

					newPath[i][j] = mux(newPath[i][j], temp[k], toPush);
					temp[k].isDummy = mux(temp[k].isDummy, SIGNAL_ONE, toPush);
					pushed  = or(pushed, canPush);	
				}
			}

		for(int i = 0; i < path.length; ++i)
			for(int j = 0; j < path[i].length; ++j)
				conditionalAdd(stash, path[i][j], not(path[i][j].isDummy));
		
		for(int i = 0; i < temp.length; ++i)
			add(stash, temp[i]);

		//dissemble(blockInSignal, path, stash);
		for(int i = 0; i < path.length; ++i)
			for(int j = 0; j < path[i].length; ++j)
				path[i][j] = newPath[i][j];
		
	}

}