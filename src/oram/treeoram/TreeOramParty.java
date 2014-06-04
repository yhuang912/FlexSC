package oram.treeoram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import flexsc.*;
import oram.Block;
import oram.PlainBlock;
import oram.TreeBasedOramParty;

public abstract class TreeOramParty<T> extends TreeBasedOramParty<T> {
	public TreeOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, (int)1.5*sp, m);
	}
	
	//to be fixed, assume uniform bucket size
	public int randomIndexOnLevel(int level) throws IOException {
		if(level == 1)
			return 1;
		
		int left = 1<<(level-1);
		int temp = rng.nextInt(left) + left;
		if(role == Party.Alice) {
			os.write(ByteBuffer.allocate(4).putInt(temp).array());
			return temp;
		}
		else{
			byte[] tempArray = new byte[4];
			is.read(tempArray);
			return ByteBuffer.wrap(tempArray).getInt();
			
		}
	}
	
	public void evict() throws Exception {
		for(int i = 1; i < logN; ++i) {
			int index = randomIndexOnLevel(i);
			evictUnit(index, i);
		}
	}
	
	abstract public void evictUnit(int index, int level) throws Exception;
	
}
