package PrivateOram.trash;

import java.io.InputStream;
import java.io.OutputStream;

import PrivateOram.CircuitOramLib;
import oram.Block;
import oram.PlainBlock;
import flexsc.Mode;
import flexsc.Party;

public class CircuitOramClient<T> extends CircuitOramParty<T> {
	CircuitOramLib<T> lib;
	Block<T>[][] scQueue;
//	T[] scIden;
	
	public CircuitOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m, sp);
		lib = new CircuitOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, gen);
		scQueue = prepareBlocks(queue, queue, queue);
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);

		lib.flush(scPath[0], pos, scQueue[0]);

		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
	}


	public T[] readAndRemove(T[] scIden, boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);
//		scIden = gen.inputOfAlice(iden);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);
		
		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
//		PlainBlock r = outputBlock(finalRes);
		return finalRes.data;//r.data;
	}


	public void putBack(T[] scIden, T[] scNewPos, T[] scData) throws Exception {
//		boolean[] tmp = new boolean[lengthOfPos+lengthOfData];
//		System.arraycopy(newPos, 0, tmp, 0, lengthOfPos);
//		System.arraycopy(data, 0, tmp, lengthOfPos, lengthOfData);

//		T[] SCTmp = gen.inputOfAlice(tmp);

//		T[] scNewPos = Arrays.copyOfRange(SCTmp, 0, lengthOfPos);
//		T[] scData = Arrays.copyOfRange(SCTmp, lengthOfPos, lengthOfData+lengthOfPos);
		
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);
		lib.add(scQueue[0], b);
		os.flush();
		ControlEviction();
	}

//	public T[] readAndRemove(int iden, boolean[] pos) throws Exception {
//		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos);
//	}
//
//	public void putBack(int iden, boolean[] pos, boolean[] data) throws Exception {
//		putBack(Utils.fromInt(iden, lengthOfIden), pos, data);
//	}

//	public boolean[] read(int iden, int pos, int newPos) throws Exception {
//		return read(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos));
//	}
//
//	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
//		write(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos), data);
//	}

	public T[] read(T[] scIden, boolean[] pos, T[] scNewPos) throws Exception {
		T[] r = readAndRemove(scIden, pos);
		putBack(scIden, scNewPos, r);
		return r;
	}

	public void write(T[] scIden, boolean[] pos, T[] scNewPos, T[] scData) throws Exception {
		readAndRemove(scIden, pos);
		putBack(scIden, scNewPos, scData);
	}

}
