package oram;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class PathORamBasic {
	protected SecureRandom rnd;
	static final int keyLen = 10; // length of key (to encrypt each data piece) in bytes

	public static int Z = 5;
	public static int C = 4; // memory reduction factor (ensure it is a power of 2)
	
	Tree serverTree;
	
	final byte[] clientKey;
	
	class Tree {
		public int N; // the number of logic blocks in the tree
		public int D; // depth of the tree
		int dataLen; // data length in bits
		
		public Bucket[] tree;
		public Stash stash;
	
		/*
		 * Given input "data" (to be outsourced), initialize the server side storage and return the client side position map. 
		 * No recursion on the tree is considered. 
		 */
		private BitSet[] initialize(BitSet[] data, int unitBits) {
			dataLen = unitBits;
			buildTree(data);
			stash = new Stash(125);
			
			// setup the position map according to the permuted blocks
			BitSet[] posMap = new BitSet[(N + C-1) / C];	// clientPosMap[i] is the leaf label of the i-th block.
			for (int i = 0; i < posMap.length; i++)
				posMap[i] = new BitSet(C*D);

			for (int i = 0; i < N; i++) {
				if (!tree[i+tree.length/2].blocks[0].isDummy())
					Utils.writePositionMap(posMap, this, tree[i+tree.length/2].blocks[0].id, i);
			}

			encryptTreeAndStash();
			
			return posMap;
		}

		private void buildTree(BitSet[] data) {
			// set N to be the smallest power of 2 that is bigger than 'data.length'. 
			N = (int) Math.pow(2, Math.ceil(Math.log(data.length)/Math.log(2)));
			D = Utils.bitLength(N)-1;

			Block[] initBlocks = new Block[N];
			for (int i = 0; i < N; i++) {
				if (i < data.length)
					initBlocks[i] = new Block((BitSet)data[i].clone(), i, 0);
				else
					initBlocks[i] = new Block();
			}
			
			// randomly permute initBlocks (NEEDS TO BE REPLACED WITH A PERMUTATION NETWORK IF THE ORIGINAL "DATA" IS OBLIVIOUS) 
			for (int i = 0; i < N; i++) {
				int j = rnd.nextInt(i+1);
				Utils.swap(initBlocks, i, j);
			}
			
			// initialize the tree
			tree = new Bucket[2*N-1];
			for (int i = 0; i < tree.length; i++) {
				if (i < tree.length/2)
					tree[i] = new Bucket(new Block());
				else { 
					tree[i] = new Bucket(initBlocks[i-(N-1)]);
					tree[i].blocks[0].treeLabel = i-(N-1);
				}
			}
		}
		
		private void encryptTreeAndStash() {
			for (Bucket bkt : tree)
				bkt.encryptBlocks();
			
			for (Block blk : stash.blocks)
				blk.enc();
		}
		
		protected Block[] readBuckets(int leafLabel) {
			
			Bucket[] buckets = getBucketsFromPath(leafLabel);
			Block[] res = new Block[Z*buckets.length];
			int i = 0;
			for (Bucket bkt : buckets) {
				for (Block blk : bkt.blocks)
					res[i++] = new Block(blk);
			}
			
			return res;
		}

		private Bucket[] getBucketsFromPath(int leaf) {
			Bucket[] ret = new Bucket[D+1];

			int temp = leaf; //((leaf+1)>>1)-1;
			for (int i = 0; i < ret.length; i++) {
				ret[i] = tree[temp];
				if (temp > 0)
					temp = ((temp+1)>>1)-1;
			}
			return ret;
		}


		class Block {
			BitSet data;
			int id; // range: 0...N-1;
			int treeLabel;
			
			private byte[] r; 
			
			public Block(Block blk) {
				assert (blk.data != null) : "no BitSet data pointers is allowed to be null.";
				try { data = (BitSet) blk.data.clone(); } 
				catch (Exception e) { e.printStackTrace(); System.exit(1); }
				id = blk.id;
				treeLabel = blk.treeLabel;
				r = blk.r;
			}
			
			Block(BitSet data, int id, int label) {
				assert (data != null) : "no BitSet data pointers allowed.";
				this.data = data;
				this.id = id;
				this.treeLabel = label;
			}
			
			public Block() {
				data = new BitSet(0);
				id = N; // id == N marks a dummy block, so the range of id is from 0 to N, both ends inclusive. Hence the bit length of id is D+1.
			}
			
			public boolean isDummy() {
				assert (r == null) : "isDummy() was called on encrypted block";
				return id == N;
			}
			
			public void erase() { id = N; treeLabel = 0; }
			
			private void enc() {
				r = Utils.genPRBits(rnd);
				mask();
			}
			
			private void dec() { 
				mask();
				r = null;
			}
			
			private void mask() {
				try {
					MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
					sha1.update(clientKey);
					sha1.update(r);
					byte[] mask = sha1.digest();
					BitSet dataMask = BitSet.valueOf(Arrays.copyOfRange(mask, 0, dataLen/8));
					data.xor(dataMask);
					id ^= ByteBuffer.wrap(mask, dataLen/8, 4).getInt();
					treeLabel ^= ByteBuffer.wrap(mask, dataLen/8+4, 4).getInt();
				} catch (Exception e) { 
					e.printStackTrace(); 
					System.exit(1); 
				}
			}
		}
		
		class Bucket {
			Block[] blocks = new Block[Z];
			
			Bucket(Block b) {
				assert (b != null) : "No null block pointers allowed.";
				blocks[0] = b;
				for (int i = 1; i < Z; i++)
					blocks[i] = new Block();
			}
			
			void encryptBlocks() {
				for (Block blk : blocks)
					blk.enc();
			}
		}
		
		class Stash {
			Tree.Block[] blocks;
			
			public Stash(int size) {
				blocks = new Tree.Block[size];
				for (int i = 0; i < blocks.length; i++) {
					blocks[i] = new Block();
				}
			}
		}
	}
	
	public PathORamBasic (SecureRandom rand) {
		rnd = rand;
		clientKey = Utils.genPRBits(rnd);
	}
	
	/*
	 * Each data has 'unitB' bytes.
	 * 'data' will be intact since the real block has a clone of each element in 'data'.
	 */
	public BitSet[] initialize(BitSet[] data, int unitB) {
		assert(data.length < (~(1<<63))) : "Too many blocks in a tree.";
		
		int nextB = unitB * 8;
		serverTree = new Tree();
		BitSet[] posMap = serverTree.initialize(data, nextB);

		return posMap;
	}

	public enum OpType {Read, Write};
	protected Tree.Block access(BitSet[] posMap, OpType op, int a, BitSet data) {
		Tree tr = serverTree;

		int leafLabel = tr.N-1 + Utils.readPositionMap(posMap, tr, a);
		Tree.Block[] blocks = tr.readBuckets(leafLabel);
		
		int newlabel = rnd.nextInt(tr.N);
		Utils.writePositionMap(posMap, tr, a, newlabel); //(u % s.C)*s.D, newlabel, s.D);
		
		return rearrangeBlocksAndReturn(op, a, data, leafLabel, blocks, newlabel);
	}

	protected Tree.Block rearrangeBlocksAndReturn(OpType op, int a, BitSet data, int leafLabel, Tree.Block[] blocks, int newlabel) {
		Tree tr = serverTree;
		Tree.Block res = null;
		Tree.Block[] union = new Tree.Block[tr.stash.blocks.length+blocks.length];
		for (int i = 0; i < tr.stash.blocks.length; i++) 
			union[i] = tr.new Block(tr.stash.blocks[i]);
		for (int i = 0; i < blocks.length; i++) 
			union[i+tr.stash.blocks.length] = tr.new Block(blocks[i]);
		
		for (Tree.Block blk : union)
			blk.dec();
		
		for (int i = 0; i < union.length; i++)
			if (union[i].id == a) {
				res = tr.new Block(union[i]);
				union[i].treeLabel = newlabel;
				if (op == OpType.Write) {
					union[i].data = data;
				}
			}

		for (int i = tr.D+1; i > 0; i--) {
			int prefix = Utils.iBitsPrefix(leafLabel+1, tr.D+1, i);
			Tree.Bucket bucket = tr.new Bucket(tr.new Block());
			for (int j = 0, k = 0; j < union.length && k < Z; j++) {
				if (!union[j].isDummy() && prefix == Utils.iBitsPrefix(union[j].treeLabel+tr.N, tr.D+1, i)) {
					bucket.blocks[k++] = tr.new Block(union[j]);
					union[j].erase();
				}
			}
			bucket.encryptBlocks();
			tr.tree[(prefix>>(tr.D+1-i))-1] = bucket;
		}
		
		// put the rest of the blocks in 'union' into the 'stash'
		int j = 0, k = 0;
		for (; j < union.length && k < tr.stash.blocks.length; j++) {
			if (!union[j].isDummy()) {
				tr.stash.blocks[k++] = tr.new Block(union[j]);
				union[j].erase();
			}
		}
		if (k == tr.stash.blocks.length) {
			for (; j < union.length; j++)
				assert (union[j].isDummy()) : "Stash is overflown: " + tr.stash.blocks.length;
					
		}
		while (k < tr.stash.blocks.length)
			tr.stash.blocks[k++].erase();
		
		for (Tree.Block blk : tr.stash.blocks)
			blk.enc();
		
		return res;
	}
	
	Tree.Block read(BitSet[] pm, int i) { return access(pm, OpType.Read, i, null); }
	
	Tree.Block write(BitSet[] pm, int i, BitSet d) { return access(pm, OpType.Write, i, d); }

	
	static class Utils {
		/*
		 * Given 'n' that has 'w' bits, output 'res' whose most significant 'i' bits are identical to 'n' but the rest are 0.
		 */
		static int iBitsPrefix(int n, int w, int i) {
			return (~((1<<(w-i)) - 1)) & n;
		}
		
		/*
		 * keys, from 1 to D, are sorted in ascending order
		 */
		static void refineKeys(int[] keys, int d, int z) {
			int j = 0, k = 1;
			for (int i = 1; i <= d && j < keys.length; i++) {
				while (j < keys.length && keys[j] == i) {
					keys[j] = (i-1)*z + k;
					j++; k++;
				}
				if (k <= z) 
					k = 1;
				else
					k -= z;
			}
		}
		
		
		private static <T> void swap(T[] arr, int i, int j) {
			T temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
		
		static void writePositionMap(BitSet[] map, Tree st, int index, int val) {
			int base = (index % C) * st.D;
			writeBitSet(map[index/C], base, val, st.D);
		}
		
		static BitSet writeBitSet(BitSet map, int base, int val, int d) {
			for (int i = 0; i < d; i++) {
				if (((val>>i) & 1) == 1)
					map.set(base + i);
				else
					map.clear(base + i);
			}
			
			return map;
		}
		
		static BitSet writeBitSet(BitSet map, int base, BitSet val, int d) {
			for (int i = 0; i < d; i++) {
				if (val.get(i))
					map.set(base + i);
				else
					map.clear(base + i);
			}
			
			return map;
		}
		
		static int readPositionMap(BitSet[] map, Tree st, int index) {
			int base = fastMod(index, C) * st.D;
			return readBitSet(map[fastDivide(index, C)], base, st.D);
		}
		
		static int readBitSet(BitSet map, int base, int d) {
			int ret = 0;
			for (int i = 0; i < d; i++) {
				if (map.get(base + i) == true)
					ret ^= (1<<i);
			}
			return ret;
		}
		
		/* 
		 * n has to be a power of 2.
		 * Return the number of bits to denote n, including the leading 1.
		 */
		static int bitLength(int n) {
			if (n == 0) 
				return 1;
			
			int res = 0;
			do {
				n = n >> 1;
				res++;
			} while (n > 0);
			return res;
		}
		
		static int fastMod(int a, int b) {
			// b is a power of 2
			int shifts = (int) (Math.log(b)/Math.log(2));
			return  a & (1<<shifts) - 1;
		}
		
		static int fastDivide(int a, int b) {
			// b is a power of 2
			int shifts = (int) (Math.log(b)/Math.log(2));
			return  a >> shifts;
		}
		
		static byte[] genPRBits(SecureRandom rnd) {
			byte[] b = new byte[keyLen];
			rnd.nextBytes(b);
			return b;
		}
	}
}