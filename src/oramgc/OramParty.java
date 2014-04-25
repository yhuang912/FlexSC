package oramgc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import test.Utils;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;

public abstract class OramParty {

	public class BlockInBinary {
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
	public GCGen gen;
	public GCEva eva;
	public Party role;
	protected SecureRandom rng = new SecureRandom();
	public enum Party { SERVER, CLIENT };
	public BucketLib lib;
	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p) throws Exception {
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
		if(p == Party.SERVER){
			eva = new GCEva(is, os);
			lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, eva);
		}
		else{
			
			gen = new GCGen(is, os);
			lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, gen);	
		}
	}
	
	public OramParty(InputStream is, OutputStream os, int N, int dataSize, Party p, int lengthOfPos) throws Exception {
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
		if(p == Party.SERVER){
			eva = new GCEva(is, os);
			lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, eva);
		}
		else{
			gen = new GCGen(is, os);
			lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, gen);	
		}
	}
	

	public Block[][] prepareBlocks(BlockInBinary[] clientBlock, BlockInBinary[] serverBlock, BlockInBinary[] randomBlock) throws Exception {
		Block[] s = inputBucketOfServer(serverBlock);
		Block[] c = inputBucketOfClient(clientBlock);
		Block[] r = inputBucketOfServer(randomBlock);
		
		Block[] xor = lib.xor(s, c);
		return new Block[][]{xor, r};
	}

	public Block[] prepareBlock(BlockInBinary clientBlock, BlockInBinary serverBlock, BlockInBinary randomBlock) throws Exception {
		Block s = inputBlockOfServer(serverBlock);
		Block c = inputBlockOfClient(clientBlock);
		Block r = inputBlockOfServer(randomBlock);
		
		Block xor = lib.xor(s, c);
		return new Block[]{xor, r};
	}
	
	public BlockInBinary prepareBlockInBinary(Block blocks, Block randomBlock) throws Exception {
		Block res = lib.xor(blocks, randomBlock);
		BlockInBinary clientBlockInBinary = outputBlock(res);
		
		if(role == Party.SERVER) {
			return null;
		}
		else{
			return clientBlockInBinary;	
		}
	}
	
	public BlockInBinary[] prepareBlockInBinaries(Block[] blocks, Block[] randomBlock) throws Exception {
		Block[] res = lib.xor(blocks, randomBlock);
		BlockInBinary[] clientBlockInBinary = outputBucket(res);
		
		if(role == Party.SERVER) {
			return null;
		}
		else{
			return clientBlockInBinary;	
		}
	}
	public Block inputBlockOfServer(BlockInBinary b) throws Exception {
		if(role == Party.SERVER) {
			Signal[] iden = eva.inputOfEva(b.iden);
			Signal[] pos = eva.inputOfEva(b.pos);
			Signal[] data = eva.inputOfEva(b.data);
			Signal isDummy = eva.inputOfEva(b.isDummy);
			return new Block(iden, pos, data, isDummy);
		}
		else {
			Signal[] iden = gen.inputOfEva(new boolean[lengthOfIden]);
			Signal[] pos = gen.inputOfEva(new boolean[lengthOfPos]);
			Signal[] data = gen.inputOfEva(new boolean[lengthOfData]);
			Signal isDummy = gen.inputOfEva(false);
			return new Block(iden, pos, data, isDummy);
		}
	}

	public Block inputBlockOfClient(BlockInBinary b) throws Exception {
		if(eva != null) {
			Signal[] iden = eva.inputOfGen(new boolean[lengthOfIden]);
			Signal[] pos = eva.inputOfGen(new boolean[lengthOfPos]);
			Signal[] data = eva.inputOfGen(new boolean[lengthOfData]);
			Signal isDummy = eva.inputOfGen(false);
			return new Block(iden, pos, data, isDummy);
		}
		else {
			Signal[] iden = gen.inputOfGen(b.iden);
			Signal[] pos = gen.inputOfGen(b.pos);
			Signal[] data = gen.inputOfGen(b.data);
			Signal isDummy = gen.inputOfGen(b.isDummy);
			return new Block(iden, pos, data, isDummy);
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
		return new BlockInBinary(iden, pos, data, true);
	}
	
	public BlockInBinary getDummyBlock2(){
		boolean[] iden = new boolean[lengthOfIden];
		boolean[] pos = new boolean[lengthOfPos];
		boolean[] data = new boolean[lengthOfData];
		for(int i = 0; i < lengthOfIden; ++i)
			iden[i] = false;
		for(int i = 0; i < lengthOfPos; ++i)
			pos[i] = false;
		for(int i = 0; i < lengthOfData; ++i)
			data[i] = false;
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
	
	public void debug(Block b) throws Exception{
		if(eva != null)
			outputBlock(b);
		else {
			System.out.print("DEBUG: ");
			System.out.print(Utils.toInt(outputBlock(b).data));
			System.out.println(" ");
		}
	}
	
	public void debug(Block[] b) throws Exception{
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
