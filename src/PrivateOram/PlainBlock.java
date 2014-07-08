package PrivateOram;

import java.util.Arrays;
import test.Utils;

public class PlainBlock {
	public long iden;
	public long pos;
	public long data;
	public boolean isDummy;
	public PlainBlock(boolean[] iden, boolean[] pos, boolean[] data, boolean isDummy) {
		this.iden = Utils.toLong(iden);//iden;	
		this.pos = Utils.toLong(pos);//pos;
		this.data = Utils.toLong(pos);//data;
		this.isDummy = isDummy;
	}
	
	public PlainBlock(boolean[] d, int lengthOfIden, int lengthOfPos, int lengthOfData) {
		this.iden = Utils.toLong(Arrays.copyOfRange(d, 0, lengthOfIden));
		this.pos = Utils.toLong(Arrays.copyOfRange(d, lengthOfIden, lengthOfIden+lengthOfPos));
		this.data = Utils.toLong(Arrays.copyOfRange(d, lengthOfIden+lengthOfPos, lengthOfIden+lengthOfPos+lengthOfData));
		this.isDummy = d[d.length-1];
	}
	
	public boolean[] toBooleanArray(int lengthOfIden, int lengthOfPos, int lengthOfData){
		boolean[] result = new boolean[lengthOfIden + lengthOfPos + lengthOfData + 1];
		System.arraycopy(Utils.fromLong(iden, lengthOfIden), 0, result, 0, lengthOfIden);
		System.arraycopy(Utils.fromLong(pos, lengthOfPos), 0, result, lengthOfIden, lengthOfPos);
		System.arraycopy(Utils.fromLong(data, lengthOfData), 0, result, lengthOfPos+lengthOfIden, lengthOfData);
		result[result.length-1] = isDummy;
		return result;
	}
	
	static public boolean[] toBooleanArray(PlainBlock[] blocks, int lengthOfIden, int lengthOfPos, int lengthOfData) {
		int blockSize = lengthOfIden + lengthOfPos + lengthOfData + 1;
		boolean[] result = new boolean[ blockSize * blocks.length];
		for(int i = 0; i < blocks.length; ++i) {
			boolean[] tmp = blocks[i].toBooleanArray(lengthOfIden, lengthOfPos, lengthOfData);
			System.arraycopy(tmp, 0, result, i*blockSize, blockSize);
		}
		return result;
	}
}
