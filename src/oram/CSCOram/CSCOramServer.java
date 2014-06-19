package oram.CSCOram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.Mode;
import flexsc.Party;


public class CSCOramServer<T> extends CSCOramParty<T> {
	CSCOramLib<T> lib;
	public CSCOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m, sp);
		lib = new CSCOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
		randomQueue = randomBucket(queueCapacity);
		scQueue = prepareBlocks(queue, queue, randomQueue);
	}

	PlainBlock[][] randomPath = new PlainBlock[logN+1][capacity];

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		lib.flush(globalSCPath[0], pos);
		preparePlainPath(globalSCPath[0], globalSCPath[1]);
		putPath(randomPath, pos);
	}


	Block<T>[][][] globalSCPath;
	@Override
	void extractFromQueue(boolean [] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		randomPath = randomPath(blocks);
		globalSCPath = preparePath(blocks, blocks, randomPath);

		lib.putFromQueueToPath(globalSCPath[0], scQueue[0], pos);

	}


	Block<T>[][] scQueue;
	T[] scIden;
	PlainBlock[] randomQueue;
	public void readAndRemove(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		PlainBlock[][] ranPath = randomPath(blocks);
		Block<T>[][][] scPath = preparePath(blocks, blocks, ranPath);
		
		scIden = eva.inputOfAlice(new boolean[lengthOfIden]);

		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		lib.putFromQueueToPath(scPath[0], scQueue[0], pos);
		lib.flush(scPath[0], pos);
		
		preparePlainPath(scPath[0], scPath[1]);
		putPath(ranPath, pos);
		outputBlock(finalRes);
	}

	public void putBack() throws Exception {		
		boolean[] tmp = new boolean[lengthOfPos+lengthOfData];
		T[] SCTmp = eva.inputOfAlice(tmp);
		T[] scNewPos = Arrays.copyOfRange(SCTmp, 0, lengthOfPos);
		T[] scData = Arrays.copyOfRange(SCTmp, lengthOfPos, lengthOfData+lengthOfPos);

		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		
		GEOdequeue();
	}	

	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}

	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}



}
