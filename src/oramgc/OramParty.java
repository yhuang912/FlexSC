package oramgc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import cv.CVCompEnv;
import test.Utils;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCEva;
import gc.GCGen;

public abstract class OramParty<T> {

	public static class BlockInBinary {
		public boolean[] iden;
		public boolean[] pos;
		public boolean[] data;
		public boolean isDummy;
		public BlockInBinary(boolean[] iden, boolean[] pos, boolean[] data, boolean isDummy) {
			this.iden = iden;
			this.pos = pos;
			this.data = data;
			this.isDummy = isDummy;
		}
	}
	public int N;
	int dataSize;
	public int logN;
	public int lengthOfIden;
	public int lengthOfPos;
	public int lengthOfData;
	protected InputStream is;
	protected OutputStream os;
	public CompEnv<T> gen;
	public CompEnv<T> eva;
	public Party role;
	protected SecureRandom rng = new SecureRandom();
	public BucketLib<T> lib;
	public Mode mode;
	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p, Mode m) throws Exception {
		this.is = is;
		this.os = os;
		
		this.dataSize = dataSize;
		int a = 1;logN=1;
		while(a < N){
			a*=2;
			++logN;
		}
		//--logN;
		this.N = 1<<logN;
		lengthOfData = dataSize;
		lengthOfIden = logN-1;
		lengthOfPos = logN-1;
		role = p;
		System.out.println(this.N+" "+this.logN+" "+lengthOfIden+" "+lengthOfPos);
		mode = m;
		if(p == Party.Bob){
			if(m == Mode.REAL)
				eva = (CompEnv<T>) new GCEva(is, os);
			else
				eva = (CompEnv<T>) new CVCompEnv(is,os, p);
			
			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, eva);
		}
		else{
			if(m == Mode.REAL)
				gen = (CompEnv<T>) new GCGen(is, os);
			else
				gen = (CompEnv<T>) new CVCompEnv(is, os, p);
			
			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, gen);	
		}
	}
	
	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p, int lengthOfPos, Mode m) throws Exception {
		this.is = is;
		this.os = os;
		
		this.dataSize = dataSize;
		int a = 1;logN=1;
		while(a < N){
			a*=2;
			++logN;
		}
		--logN;
		this.N = 1<<logN;
		lengthOfData = dataSize;
		lengthOfIden = logN;
		this.lengthOfPos = lengthOfPos;
		role = p;
		System.out.println(this.N+" "+this.logN+" "+lengthOfIden+" "+lengthOfPos);
		if(p == Party.Bob){
			if(m == Mode.REAL)
				eva = (CompEnv<T>) new GCEva(is, os);
			else
				eva = (CompEnv<T>) new CVCompEnv(is, os, p);
			
			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, eva);
		}
		else{
			if(m == Mode.REAL)
				gen = (CompEnv<T>) new GCGen(is, os);
			else
				gen = (CompEnv<T>) new CVCompEnv(is, os, p);
			
			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, gen);	
		}
	}
	

	public Block<T>[][] prepareBlocks(BlockInBinary[] clientBlock, BlockInBinary[] serverBlock, BlockInBinary[] randomBlock) throws Exception {
		Block<T>[] s = inputBucketOfServer(serverBlock);
		Block<T>[] c = inputBucketOfClient(clientBlock);
		Block<T>[] r = inputBucketOfServer(randomBlock);
		
		Block<T>[] xor = lib.xor(s, c);
		return new Block[][]{xor, r};
	}

	public Block<T>[] prepareBlock(BlockInBinary clientBlock, BlockInBinary serverBlock, BlockInBinary randomBlock) throws Exception {
		Block<T> s = inputBlockOfServer(serverBlock);
		Block<T> c = inputBlockOfClient(clientBlock);
		Block<T> r = inputBlockOfServer(randomBlock);
		
		Block<T> xor = lib.xor(s, c);
		return new Block[]{xor, r};
	}
	
	public BlockInBinary prepareBlockInBinary(Block<T> blocks, Block<T> randomBlock) throws Exception {
		Block<T> res = lib.xor(blocks, randomBlock);
		BlockInBinary clientBlockInBinary = outputBlock(res);
		
		if(role == Party.Bob) {
			return null;
		}
		else{
			return clientBlockInBinary;	
		}
	}
	
	public BlockInBinary[] prepareBlockInBinaries(Block<T>[] blocks, Block<T>[] randomBlock) throws Exception {
		Block<T>[] res = lib.xor(blocks, randomBlock);
		BlockInBinary[] clientBlockInBinary = outputBucket(res);
		
		if(role == Party.Bob) {
			return null;
		}
		else{
			return clientBlockInBinary;	
		}
	}
	
	public Block<T> inputBlockOfServer(BlockInBinary b) throws Exception {
		if(role == Party.Bob) {
			T[] iden = (T[]) eva.inputOfEva(b.iden);
			T[] pos = (T[]) eva.inputOfEva(b.pos);
			T[] data = (T[]) eva.inputOfEva(b.data);
			T isDummy = (T) eva.inputOfEva(b.isDummy);
			return new Block<T>(iden, pos, data, isDummy);
		}
		else {
			T[] iden = (T[]) gen.inputOfEva(new boolean[lengthOfIden]);
			T[] pos = (T[]) gen.inputOfEva(new boolean[lengthOfPos]);
			T[] data = (T[]) gen.inputOfEva(new boolean[lengthOfData]);
			T isDummy = (T) gen.inputOfEva(false);
			return new Block<T>(iden, pos, data, isDummy);
		}
	}

	public Block<T> inputBlockOfClient(BlockInBinary b) throws Exception {
		if(eva != null) {
			T[] iden = (T[]) eva.inputOfGen(new boolean[lengthOfIden]);
			T[] pos = (T[]) eva.inputOfGen(new boolean[lengthOfPos]);
			T[] data = (T[]) eva.inputOfGen(new boolean[lengthOfData]);
			T isDummy = (T) eva.inputOfGen(false);
			return new Block<T>(iden, pos, data, isDummy);
		}
		else {
			T[] iden = (T[]) gen.inputOfGen(b.iden);
			T[] pos = (T[]) gen.inputOfGen(b.pos);
			T[] data = (T[]) gen.inputOfGen(b.data);
			T isDummy = (T) gen.inputOfGen(b.isDummy);
			return new Block<T>(iden, pos, data, isDummy);
		}
	}

	public Block<T>[] inputBucketOfServer(BlockInBinary[] b) throws Exception {
		Block<T>[] result = lib.newBlockArray(b.length);
		for(int i = 0; i < b.length; ++i)
			result[i] = inputBlockOfServer(b[i]);
		return result;
	}

	public Block<T>[] inputBucketOfClient(BlockInBinary[] b) throws Exception {
		Block<T>[] result = lib.newBlockArray(b.length);
		for(int i = 0; i < b.length; ++i)
			result[i] = inputBlockOfClient(b[i]);
		return result;
	}


	public BlockInBinary outputBlock(Block<T> b) throws Exception {
		if(eva != null){
			boolean[] iden = eva.outputToGen(b.iden);
			boolean[] pos = eva.outputToGen(b.pos);
			boolean[] data = eva.outputToGen(b.data);
			boolean isDummy = eva.outputToGen(b.isDummy);
			return new BlockInBinary(iden, pos, data, isDummy);
		}
		else {
			boolean[] iden = gen.outputToGen(b.iden);
			boolean[] pos = gen.outputToGen(b.pos);
			boolean[] data = gen.outputToGen(b.data);
			boolean isDummy = gen.outputToGen(b.isDummy);
			return new BlockInBinary(iden, pos, data, isDummy);				
		}
	}

	public BlockInBinary[] outputBucket(Block<T>[] b) throws Exception {
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
			iden[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfPos; ++i)
			pos[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfData; ++i)
			data[i] = rng.nextBoolean();
		return new BlockInBinary(iden, pos, data, true);
	}
	
	public BlockInBinary getDummyBlock2(){
		boolean[] iden = new boolean[lengthOfIden];
		boolean[] pos = new boolean[lengthOfPos];
		boolean[] data = new boolean[lengthOfData];
		for(int i = 0; i < lengthOfIden; ++i)
			iden[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfPos; ++i)
			pos[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfData; ++i)
			data[i] = rng.nextBoolean();
		return new BlockInBinary(iden, pos, data, false);
	}


	public BlockInBinary randomBlock() {
		BlockInBinary result = getDummyBlock();
		for(int i = 0; i < lengthOfIden; ++i)
			result.iden[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfPos; ++i)
			result.pos[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfData; ++i)
			result.data[i] = rng.nextBoolean();
		result.isDummy = rng.nextBoolean();
		return result;
	}

	public BlockInBinary[] randomBucket(int length) {
		BlockInBinary[] result = new BlockInBinary[length];
		for(int i = 0; i < length; ++i)
			result[i] = randomBlock();
		return result;
	}
	
	public void debug(Block<T> b) throws Exception{
		if(eva != null)
			outputBlock(b);
		else {
			System.out.print("DEBUG: ");
			System.out.print(Utils.toInt(outputBlock(b).data));
			System.out.println(" ");
		}
	}
	
	public void debug(Block<T>[] b) throws Exception{
		if(eva!= null)
			outputBucket(b);
		else{
			System.out.print("DEBUG: ");
			BlockInBinary[] bib = outputBucket(b);
			int[] idens = new int[b.length];
			for(int i = 0; i < idens.length; ++i)
				idens[i] = Utils.toInt(bib[i].data);
			System.out.print(Arrays.toString(idens));
			System.out.println(" ");
		}
		
	}

}
