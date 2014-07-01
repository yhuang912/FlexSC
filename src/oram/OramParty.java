package oram;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import pm.PMCompEnv;
import rand.ISAACProvider;
import cv.CVCompEnv;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCEva;
import gc.GCGen;

public abstract class OramParty<T> {
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
	public Mode mode;

	static protected SecureRandom rng;
	static{
	Security.addProvider(new ISAACProvider ());
	try {
		rng = SecureRandom.getInstance ("ISAACRandom");
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public BucketLib<T> lib;
	boolean[] dummyArray;

	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p, Mode m) throws Exception {
		this.is = is;
		this.os = os;

		this.dataSize = dataSize;
		long a = 1;logN=1;
		while(a < N) {
			a*=2;
			++logN;
		}
		--logN;

		this.N = 1<<logN;
		lengthOfData = dataSize;
		lengthOfIden = logN;
		lengthOfPos = logN-1;
		role = p;
		mode = m;
		init();
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
		mode = m;
		init();

	}

	public void init() throws Exception {
		dummyArray = new boolean[lengthOfIden+lengthOfPos+lengthOfData+1];
		for(int i = 0; i < dummyArray.length; ++i)
			dummyArray[i] = false;

		if(role == Party.Bob){
			if(mode == Mode.REAL)
				eva = (CompEnv<T>) new GCEva(is, os);
			else if(mode == Mode.VERIFY)
				eva = (CompEnv<T>) new CVCompEnv(is,os, role);
			else if(mode == mode.COUNT)
				eva = (CompEnv<T>) new PMCompEnv(is,os, role);

			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, eva);
		}
		else{
			if(mode == Mode.REAL)
				gen = (CompEnv<T>) new GCGen(is, os);
			else if(mode == Mode.VERIFY)
				gen = (CompEnv<T>) new CVCompEnv(is, os, role);
			else if(mode == mode.COUNT)
				gen = (CompEnv<T>) new PMCompEnv(is,os, role);

			lib = new BucketLib<T>(lengthOfIden, lengthOfPos, lengthOfData, gen);	
		}
	}

	public Block<T>[][] prepareBlocks(PlainBlock[] clientBlock, PlainBlock[] serverBlock, PlainBlock[] randomBlock) throws Exception {
		Block<T>[] s = inputBucketOfServer(serverBlock);
		Block<T>[] c = inputBucketOfClient(clientBlock);
		Block<T>[] r = inputBucketOfServer(randomBlock);

		Block<T>[] xor = lib.xor(s, c);
		return new Block[][]{xor, r};
	}

	public Block<T>[] prepareBlock(PlainBlock clientBlock, PlainBlock serverBlock, PlainBlock randomBlock) throws Exception {
		Block<T> s = inputBlockOfServer(serverBlock);
		Block<T> c = inputBlockOfClient(clientBlock);
		Block<T> r = inputBlockOfServer(randomBlock);

		Block<T> xor = lib.xor(s, c);
		return new Block[]{xor, r};
	}


	public PlainBlock preparePlainBlock(Block<T> blocks, Block<T> randomBlock) throws Exception {
		PlainBlock result = outputBlock(lib.xor(blocks, randomBlock));
		if(role == Party.Bob)
			return null;
		else
			return result;	
	}

	public PlainBlock[] preparePlainBlocks(Block<T>[] blocks, Block<T>[] randomBlock) throws Exception {
		PlainBlock[] result = outputBucket(lib.xor(blocks, randomBlock));
		if(role == Party.Bob)
			return null;
		else
			return result;
	}


	public Block<T> inputBlockOfServer(PlainBlock b) throws Exception {
		T[] TArray;
		if(eva != null)
			TArray = eva.inputOfBob(b.toBooleanArray());
		else 
			TArray = gen.inputOfBob(dummyArray);
		os.flush();
		return new Block<T>(TArray, lengthOfIden,lengthOfPos,lengthOfData);

	}

	public Block<T> inputBlockOfClient(PlainBlock b) throws Exception {
		T[] TArray;
		if(eva != null) 
			TArray = eva.inputOfAlice(dummyArray);		
		else 
			TArray = gen.inputOfAlice(b.toBooleanArray());
		os.flush();
		return new Block<T>(TArray, lengthOfIden,lengthOfPos,lengthOfData);
	}


	public Block<T>[] toBlocks(T[] Tarray, int lengthOfIden, int lengthOfPos, int lengthOfData, int capacity) {
		int blockSize = lengthOfIden + lengthOfPos + lengthOfData+1;
		Block<T>[] result = lib.newBlockArray(capacity);
		for(int i = 0; i < capacity; ++i) {
			result[i] = new Block<T>(Arrays.copyOfRange(Tarray, i*blockSize, (i+1)*blockSize),
					lengthOfIden, lengthOfPos, lengthOfData);
		}
		return result;
	}
	
	public Block<T>[] inputBucketOfServer(PlainBlock[] b) throws Exception {
		T[] TArray;
		if(eva != null)
			TArray = eva.inputOfBob(PlainBlock.toBooleanArray(b));
		else 
			TArray = gen.inputOfBob(PlainBlock.toBooleanArray(b));

		os.flush();
		return toBlocks(TArray, lengthOfIden, lengthOfPos, lengthOfData, b.length);//new Block<T>(TArray, lengthOfIden,lengthOfPos,lengthOfData);
	}

	public Block<T>[] inputBucketOfClient(PlainBlock[] b) throws Exception {
		T[] TArray;
		if(eva != null) 
			TArray = eva.inputOfAlice(PlainBlock.toBooleanArray(b));
		else 
			TArray = gen.inputOfAlice(PlainBlock.toBooleanArray(b));
		os.flush();
		return toBlocks(TArray, lengthOfIden, lengthOfPos, lengthOfData, b.length);
	}


	public PlainBlock outputBlock(Block<T> b) throws Exception {
		if(eva != null){
			boolean[] iden = eva.outputToAlice(b.iden);
			boolean[] pos = eva.outputToAlice(b.pos);
			boolean[] data = eva.outputToAlice(b.data);
			boolean isDummy = eva.outputToAlice(b.isDummy);
			os.flush();
			return new PlainBlock(iden, pos, data, isDummy);
		}
		else {
			boolean[] iden = gen.outputToAlice(b.iden);
			boolean[] pos = gen.outputToAlice(b.pos);
			boolean[] data = gen.outputToAlice(b.data);
			boolean isDummy = gen.outputToAlice(b.isDummy);
			os.flush();
			return new PlainBlock(iden, pos, data, isDummy);				
		}
	}

	public PlainBlock[] outputBucket(Block<T>[] b) throws Exception {
		PlainBlock[] result = new PlainBlock[b.length];
		for(int i = 0; i < b.length; ++i)
			result[i] = outputBlock(b[i]);
		return result;
	}
	
	public PlainBlock[][] outputBuckets(Block<T>[][] b) throws Exception {
		PlainBlock[][] result = new PlainBlock[b.length][];
		for(int i = 0; i < b.length; ++i)
			result[i] = outputBucket(b[i]);
		return result;
	}

	public PlainBlock getDummyBlock(boolean b){
		boolean[] iden = new boolean[lengthOfIden];
		boolean[] pos = new boolean[lengthOfPos];
		boolean[] data = new boolean[lengthOfData];
		for(int i = 0; i < lengthOfIden; ++i)
			iden[i] = true;
		for(int i = 0; i < lengthOfPos; ++i)
			pos[i] = true;
		for(int i = 0; i < lengthOfData; ++i)
			data[i] = true;
		return new PlainBlock(iden, pos, data, b);
	}

	PlainBlock r = getDummyBlock(true);
	public PlainBlock randomBlock() {
		PlainBlock result = getDummyBlock(true);
		if(mode == mode.COUNT)
			return result;

		for(int i = 0; i < lengthOfIden; ++i)
			result.iden[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfPos; ++i)
			result.pos[i] = rng.nextBoolean();
		for(int i = 0; i < lengthOfData; ++i)
			result.data[i] = rng.nextBoolean();
		result.isDummy = rng.nextBoolean();

		return result;
	}

	public PlainBlock[] randomBucket(int length) {
		PlainBlock[] result = new PlainBlock[length];
		for(int i = 0; i < length; ++i)
			result[i] = randomBlock();
		return result;
	}
}
