package oram;

import java.security.SecureRandom;
import java.util.*;


public class PathORam extends PathORamBasic{

	public static int clientStoreCutoff = 10;
	
	PathORamBasic indexRam;
	
	public PathORam (SecureRandom rand) {
		super(rand);
	}
	
	/*
	 * Each data has 'unitB' bytes.
	 */
	public BitSet[] initialize(BitSet[] data, int unitBits) {
		BitSet[] pm = super.initialize(data, unitBits);
		if (pm.length <= clientStoreCutoff) {
			indexRam = new PathORamBasic(rnd);
			return indexRam.initialize(pm, C*serverTree.D);	
		}
		else {
			indexRam = new PathORam(rnd);
			return indexRam.initialize(pm, C*serverTree.D);
		}
	}

	protected PathORamBasic.Tree.Block access(BitSet[] posMap, PathORamBasic.OpType op, int a, BitSet data) {
		Tree tr = serverTree;
		int head = a / C;
		int tail = a % C;
		BitSet chunk = indexRam.read(posMap, head).data;
		int leafLabel = tr.N-1 + Utils.readBitSet(chunk, tail*tr.D, tr.D);
		Tree.Block[] blocks = tr.readBuckets(leafLabel);
		
		int newlabel = rnd.nextInt(tr.N);
		indexRam.write(posMap, head, Utils.writeBitSet(chunk, tail*tr.D, newlabel, tr.D));

		return rearrangeBlocksAndReturn(op, a, data, leafLabel, blocks, newlabel);
	}
}