package oram;

import java.security.SecureRandom;
import java.util.*;

public class TreeORam {
	public static int vu = 2;
	public static int clientStoreCutoff = 10;
	public static int L = 20;
	
	ArrayList<Tree> trees;
	BitSet[] clientPosMap;
		
	static class Tree {
		public int N; // the number of logic blocks in the tree
		public int D; // depth of the tree
		public int C; // memory reduction factor
		
		public int dataWidth;
		public Bucket[] tree;
	
		/*
		 * Given input "data" (to be outsourced), initialize the server side storage and return the client side position map. 
		 * No recursion on the tree is considered. 
		 */
		private BitSet[] initialize(BitSet[] data, int unitB) {
			init(data, unitB);
			
			// setup the position map according to the permuted blocks
			BitSet[] posMap = new BitSet[(N + C-1) / C];	// clientPosMap[i] is the leaf label of the i-th block.
			for (int i = 0; i < posMap.length; i++)
				posMap[i] = new BitSet(C*D);

			for (int i = 0; i < N; i++) {
				if (!tree[i+tree.length/2].blocks[0].isDummy())
					writePositionMap(posMap, this, tree[i+tree.length/2].blocks[0].id, i);
			}

			return posMap;
		}

		/*
		 * Scan a path from root to the leaf labeled by "leafLabel" to find the block with "id == u".
		 */
		private Block readAndRemoveBlock(int leafLabel, int u) {
			Block res = null;
			Bucket[] buckets = getBucketsFromPath(leafLabel);
			for (Bucket buck : buckets) {
				Block temp = buck.ReadAndRemove(u);
				if (!temp.isDummy())
					res = temp;
			}
			
			assert (!res.isDummy()) : "Tree invariant broken.";
			return res;
		}

		private Bucket[] getBucketsFromPath(int leaf) {
			int depth = (int) (Math.log(tree.length+1)/Math.log(2));
			Bucket[] ret = new Bucket[depth];

			int temp = leaf;
			for (int i = 0; i < depth; i++) {
				ret[i] = tree[temp];
				if (temp > 0)
					temp = (temp-1)>>1;
			}
			return ret;
		}

		private void evict() throws Exception {
			for (int i = 0; i < D; i++) {
				int base = (int) Math.pow(2, i) - 1;
				
				for (int j = 0; j < vu; j++) {
					int a = rnd.nextInt((int) Math.pow(2, i));
					int pos = base + a;
					Block b = tree[pos].Pop();
					if (!b.isDummy()) {
						int temp = ((N-1 + b.treeLabel + 1)>>(D-i-1))-1;
						if (temp == 2*pos + 1) {
							tree[2*pos+1].Write(b);
							tree[2*pos+2].Write(new Block());
						}
						else if (temp == 2*pos + 2) {
							tree[2*pos+2].Write(b);
							tree[2*pos+1].Write(new Block());
						}
						else
							throw new Exception("One of the above two branches has to be entered.");
					}
				}
			}
		}

		private void AddBlock(int level, BitSet retrievedData, BitSet newData, int u, int label,
				int upperData, int upperU, int upperC, int upperD) throws Exception {
			if (level == 0) // st.serverTree stores information about the original data
				tree[0].Write(new Block(newData, u, label));
			else { // st.serverTree stores information about the position map for the tree on level-1 
				BitSet d = (BitSet) retrievedData.clone();
				writeBitSet(d, (upperU % upperC)*upperD, upperData, upperD); // prepare the data to update from upper level tree info.
				tree[0].Write(new Block(d, u, label));
			}
			
			evict();
		}
		
		class Block {
			BitSet data;
			int id; // range: 0...N-1;
			int treeLabel;
			
			public Block(BitSet data, int id, int label) {
				this.data = data;
				this.id = id;
				this.treeLabel = label;
			}
			
			public Block() {
				data = new BitSet(0);
				this.id = N; // id == N marks a dummy block, so the range of id is from 0 to N, both ends inclusive. Hence the bit length of id is D+1.
			}
			
			public boolean isDummy() {
				return id == N;
			}
		}
		
		class Bucket {
			Block[] blocks = new Block[L];
			
			Bucket(Block b) {
				blocks[0] = b;
				for (int i = 1; i < L; i++)
					blocks[i] = new Block();
			}
			
			Block ReadAndRemove(int u) {
				Block ret = new Block();
				for (int i = 0; i < L; i++) {
					if (!blocks[i].isDummy() && blocks[i].id == u) {
						ret = blocks[i];
						blocks[i] = new Block(); // replace it with a dummy
					}
//					boolean t1 = GCGadgets.EQ(blocks[i].id, u, st.D+1);
//					t1 = GCGadgets.AND(t1, !blocks[i].isDummy());
//					ret.data = GCGadgets.MUX(ret.data, blocks[i].data, t1);
//					ret.id = GCGadgets.MUX(ret.id, blocks[i].id, t1, st.D+1);
//					ret.treeLabel = GCGadgets.MUX(ret.treeLabel, blocks[i].treeLabel, t1, st.D);
//					
//					blocks[i].id = GCGadgets.MUX(blocks[i].id, st.N, t1, st.D+1);
				}

				return ret;
			}
			
			void Write(Block b) throws Exception {
				for (int i = 0; i < L; i++) {
					if (blocks[i].isDummy()) {
					 	blocks[i] = b;
					 	return;
					}
				}
				
				throw new Exception("Bucket full!");
			}
			
			Block Pop() {
				Block b = new Block();
				for (int i = 0; i < L; i++) {
					if (!blocks[i].isDummy()) {
						b = blocks[i];
						blocks[i] = new Block();
						break;
					}
				}

				return b;
			}
		}
		
		void init(BitSet[] data, int unitB) {
			N = (int) Math.pow(2, Math.ceil(Math.log(data.length)/Math.log(2)));
			D = bitLength(N);
			C = (int) Math.pow(2, (int) (Math.log(unitB/D) / Math.log(2))); // make sure C is a power of 2
			dataWidth = unitB;

			assert (C>=2) : "Assertion Failure: C < 2.";

			Block[] initBlocks = new Block[N];
			for (int i = 0; i < N; i++) {
				if (i < data.length)
					initBlocks[i] = new Block(data[i], i, 0);
				else
					initBlocks[i] = new Block();
			}
			
			// randomly permute initBlocks (NEEDS TO BE REPLACED WITH A PERMUTATION NETWORK IF THE ORIGINAL "DATA" IS OBLIVIOUS) 
			for (int i = 0; i < N; i++) {
				int j = rnd.nextInt(i+1);
				swap(initBlocks, i, j);
			}
			
			// initialize the server-side tree
			tree = new Bucket[2*N-1];
			for (int i = 0; i < tree.length; i++) {
				if (i < tree.length/2)
					tree[i] = new Bucket(new Block());
				else { 
					tree[i] = new Bucket(initBlocks[i-tree.length/2]);
					tree[i].blocks[0].treeLabel = i-tree.length/2;
				}
			}
		}
	}
	
	/* 
	 * n has to be a power of 2.
	 */
	static int bitLength(int n) {
		int res = 0;
		do {
			n = n >> 1;
			res++;
		} while (n > 0);
		return (res == 1) ? 1 : (res-1);
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

	public TreeORam (SecureRandom rand) {
		rnd = rand;
	}
	
	private static SecureRandom rnd;
	
	private static <T> void swap(T[] arr, int i, int j) {
		T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	static void writePositionMap(BitSet[] map, Tree st, int index, int val) {
		int base = (index % st.C) * st.D;
		writeBitSet(map[index/st.C], base, val, st.D);
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
		int base = fastMod(index, st.C) * st.D;
		return readBitSet(map[fastDivide(index, st.C)], base, st.D);
	}
	
	static int readBitSet(BitSet map, int base, int d) {
		int ret = 0;
		for (int i = 0; i < d; i++) {
			if (map.get(base + i) == true)
				ret ^= (1<<i);
		}
		return ret;
	}
	
	public void initialize(BitSet[] data, int unitB) {
		assert(data.length < (~(1<<63))) : "Too many blocks in a tree.";
		
		trees = new ArrayList<Tree>();
		
		int n = data.length;
		BitSet[] posMap = data;
		int nextB = unitB;
		do {
			Tree s = new Tree();
			posMap = s.initialize(posMap, nextB);
			trees.add(s);
			n = (n + s.C - 1) / s.C;
			nextB = s.C * s.D;
		} while(n > clientStoreCutoff);

//		return posMap;
		clientPosMap = posMap;
	}
	
	public Tree.Block read(int u) throws Exception {
		Tree.Block b = readHelper(clientPosMap, u, 0, 0, 0, 0, 0);
		return b;
	}

	private Tree.Block readHelper(BitSet[] clientMap, int u, int level, int upperData, int upperU, int upperD, int upperC) throws Exception {
		Tree s = trees.get(level);
		Tree.Block res = null;
				
		if (level == trees.size()-1) {
			int leafLabel = s.N-1 + readPositionMap(clientMap, s, u);
			res = s.readAndRemoveBlock(leafLabel, u);

			int newlabel = rnd.nextInt(s.N);
			writePositionMap(clientMap, s, u, newlabel); //(u % s.C)*s.D, newlabel, s.D);
			
			s.AddBlock(level, res.data, res.data, u, newlabel, upperData, upperU, upperC, upperD);
		}
		else {
			int newlabel = rnd.nextInt(s.N);
			Tree.Block lowerBlock = readHelper(clientMap, fastDivide(u, s.C), level+1, newlabel, u, s.D, s.C);
			int leafLabel = s.N-1 + readBitSet(lowerBlock.data, fastMod(u, s.C) * s.D, s.D);
			
			res = s.readAndRemoveBlock(leafLabel, u);
			s.AddBlock(level, res.data, res.data, u, newlabel, upperData, upperU, upperC, upperD);
		}

		return res;
	}	
		
	public void write(int u, BitSet data) throws Exception {
		writeHelper(clientPosMap, u, data, 0, 0, 0, 0, 0);
	}
	
	
	private void writeHelper(BitSet[] clientMap, int u, BitSet data, int level, int upperData, int upperU, int upperD, int upperC) throws Exception {
		Tree s = trees.get(level);
		Tree.Block res = null;
				
		if (level == trees.size()-1) {
			int leafLabel = s.N-1 + readPositionMap(clientMap, s, u);
			res = s.readAndRemoveBlock(leafLabel, u);

			int newlabel = rnd.nextInt(s.N);
			writePositionMap(clientMap, s, u, newlabel); //(u % s.C)*s.D, newlabel, s.D);
			
			s.AddBlock(level, res.data, data, u, newlabel, upperData, upperU, upperC, upperD);
		}
		else {
			int newlabel = rnd.nextInt(s.N);
			Tree.Block lowerBlock = readHelper(clientMap, fastDivide(u, s.C), level+1, newlabel, u, s.D, s.C);
			int leafLabel = s.N-1 + readBitSet(lowerBlock.data, fastMod(u, s.C) * s.D, s.D);
			
			res = s.readAndRemoveBlock(leafLabel, u);
			
			s.AddBlock(level, res.data, data, u, newlabel, upperData, upperU, upperC, upperD);
		}
	}
}