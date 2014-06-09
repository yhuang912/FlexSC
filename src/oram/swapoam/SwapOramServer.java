package oram.swapoam;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.*;


public class SwapOramServer<T> extends SwapOramParty<T> {
	SwapOramLib<T> lib;
	Block<T>[][] scQueue;
	T[] scIden;
	PlainBlock[] randomQueue;

	public SwapOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m, sp);
		lib = new SwapOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
		randomQueue = randomBucket(queueCapacity);
		scQueue = prepareBlocks(queue, queue, randomQueue);
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		PlainBlock[][] randPath = randomPath(blocks);
		Block<T>[][][] scPath = preparePath(blocks, blocks, randPath);

		lib.flush(scPath[0], pos, scQueue[0]);
		
		preparePlainPath(scPath[0], scPath[1]);
		putPath(randPath, pos);
	}

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
		
		flushOneTime(nextPath());
		flushOneTime(nextPath());
	}

	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}

	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}

}