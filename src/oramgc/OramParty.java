package oramgc;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import test.Utils;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;

public abstract class OramParty {

	public class BlockInBinary {
		public boolean[] iden;
		public boolean[] pos;
		public boolean[] data;
		public BlockInBinary(boolean[] iden, boolean[] pos, boolean[] data) {
			this.iden = iden;
			this.pos = pos;
			this.data = data;
		}
	}
	int N, dataSize;
	protected int logN;
	public int lengthOfIden;
	public int lengthOfPos;
	public int lengthOfData;
	protected InputStream is;
	protected OutputStream os;
	public GCGen gen;
	public GCEva eva;
	protected SecureRandom rng = new SecureRandom();
	public enum Party { SERVER, CLIENT };
	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p) throws Exception {
		this.is = is;
		this.os = os;
		this.N = N;
		this.dataSize = dataSize;
		int a = 1;logN=1;
		while(a < N){
			a*=2;
			++logN;
		}
		lengthOfData = dataSize;
		lengthOfIden = logN;
		lengthOfPos = logN;
		if(p == Party.SERVER)
			eva = new GCEva(is, os);
		else
			gen = new GCGen(is, os);
	}

	public Block inputBlockOfServer(BlockInBinary b) throws Exception {
		if(eva != null) {
			Signal[] iden = eva.inputOfEva(b.iden);
			Signal[] pos = eva.inputOfEva(b.pos);
			Signal[] data = eva.inputOfEva(b.data);
			return new Block(iden, pos, data);
		}
		else {
			Signal[] iden = gen.inputOfEva(new boolean[lengthOfIden]);
			Signal[] pos = gen.inputOfEva(new boolean[lengthOfPos]);
			Signal[] data = gen.inputOfEva(new boolean[lengthOfData]);
			return new Block(iden, pos, data);
		}
	}

	public Block inputBlockOfClient(BlockInBinary b) throws Exception {
		if(eva != null) {
			Signal[] iden = eva.inputOfGen(new boolean[lengthOfIden]);
			Signal[] pos = eva.inputOfGen(new boolean[lengthOfPos]);
			Signal[] data = eva.inputOfGen(new boolean[lengthOfData]);
			return new Block(iden, pos, data);
		}
		else {
			Signal[] iden = gen.inputOfGen(b.iden);
			Signal[] pos = gen.inputOfGen(b.pos);
			Signal[] data = gen.inputOfGen(b.data);
			return new Block(iden, pos, data);
		}
	}

	public Block[] inputBucketOfServer(BlockInBinary[] b) throws Exception {
		Block[] result = new Block[b.length];
		for(int i = 0; i < b.length; ++i)
			result[i] = inputBlockOfServer(b[i]);
		return result;
	}

	public Block[] inputBucketOfClient(BlockInBinary[] b) throws Exception {
		Block[] result = new Block[b.length];
		for(int i = 0; i < b.length; ++i)
			result[i] = inputBlockOfClient(b[i]);
		return result;
	}


	public BlockInBinary outputBlock(Block b) throws Exception {
		if(eva != null){
			boolean[] iden = eva.outputToGen(b.iden);
			boolean[] pos = eva.outputToGen(b.pos);
			boolean[] data = eva.outputToGen(b.data);
			return new BlockInBinary(iden, pos, data);
		}
		else {
			boolean[] iden = gen.outputToGen(b.iden);
			boolean[] pos = gen.outputToGen(b.pos);
			boolean[] data = gen.outputToGen(b.data);
			return new BlockInBinary(iden, pos, data);				
		}
	}

	public BlockInBinary[] outputBucket(Block[] b) throws Exception {
		BlockInBinary[] result = new BlockInBinary[b.length];
		for(int i = 0; i < b.length; ++i)
			result[i] = outputBlock(b[i]);
		return result;
	}

	public BlockInBinary getDummyBlock(){
		boolean[] iden = new boolean[lengthOfIden];
		boolean[] pos = new boolean[lengthOfPos];
		boolean[] data = new boolean[lengthOfData];
		for(int i = 0; i < lengthOfIden; ++i)
			iden[i] = false;
		for(int i = 0; i < lengthOfPos; ++i)
			pos[i] = false;
		for(int i = 0; i < lengthOfData; ++i)
			data[i] = false;
		return new BlockInBinary(iden, pos, data);
	}

	public BlockInBinary randomBlock() {
		BlockInBinary result = getDummyBlock();
		for(int i = 0; i < lengthOfIden; ++i)
			result.iden[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfPos; ++i)
			result.pos[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfData; ++i)
			result.data[i] = rng.nextBoolean();
		
		return result;
	}

	public BlockInBinary[] randomBucket(int length) {
		BlockInBinary[] result = new BlockInBinary[length];
		for(int i = 0; i < length; ++i)
			result[i] = randomBlock();
		return result;
	}

}
