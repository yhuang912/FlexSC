package ods.opriorityqueue;

import ods.ObliviousDataStructure;
import oram.BucketLib;
import PrivateOram.CircuitOram;
import flexsc.CompEnv;

public class OPriorityQueue<T> extends ObliviousDataStructure<T> {
	CircuitOram<T> oram;
	BucketLib<T> lib;
	T[] root;
	int totalLevel;
	CompEnv<T> env;
	class PQBlock{
		T[] leftpos;
		T[] rightpos;
		T[] data;
		T[] toTArray() {
			T[] res = env.newTArray(leftpos.length + rightpos.length + data.length);
			System.arraycopy(leftpos, 0, res, 0, leftpos.length);
			System.arraycopy(rightpos, 0, res, leftpos.length, leftpos.length + rightpos.length);
			System.arraycopy(data, 0, res, leftpos.length + rightpos.length, leftpos.length + rightpos.length + data.length);
			return res;
		}
	}
	public OPriorityQueue(CompEnv<T> env, int N, int dataSize) throws Exception {
		super(env);
		this.env = env;
		oram = new CircuitOram<T>(env, N, dataSize,capacity, sp);
		oram = new CircuitOram<T>(env, N, dataSize + 2*oram.lengthOfPos, capacity, sp);
		totalLevel = oram.lengthOfIden;
		lib = oram.lib;
		initialize();	
	}
	
	public void initialize(){
		root = buildTree(0);
	}
	
	public T[] buildTree(int level) {
		if(level == totalLevel){
//			T[] handle = oram.write
		}
		else{
			
		}
		return null;
	}

}
