package oram.Swapoam;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.*;

public class SwapOramClient<T> extends SwapOramParty<T> {
	SwapOramLib<T> lib;
	public SwapOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m, sp);
		lib = new SwapOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, gen);
		scQueue = prepareBlocks(queue, queue, queue);
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);

		lib.flush(scPath[0], pos);

		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
	}

	Block<T>[][] scQueue;
	T[] scIden;
	public boolean[] readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);
		
		scIden = gen.inputOfAlice(iden);

		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);
		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);

		PlainBlock r = outputBlock(finalRes);
		return r.data;
//				return new boolean[]{true};
	}


	public void putBack(boolean[] iden, boolean[] newPos, boolean[] data) throws Exception {
		boolean[] tmp = new boolean[newPos.length+data.length];
		System.arraycopy(newPos, 0, tmp, 0, lengthOfPos);
		System.arraycopy(data, 0, tmp, lengthOfPos, lengthOfData);

		T[] SCTmp = gen.inputOfAlice(tmp);

		T[] scNewPos = Arrays.copyOfRange(SCTmp, 0, lengthOfPos);
		T[] scData = Arrays.copyOfRange(SCTmp, lengthOfPos, lengthOfData+lengthOfPos);
		
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);

		flushOneTime(nextPath());
		flushOneTime(nextPath());
	}

	public boolean[] readAndRemove(int iden, boolean[] pos) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos);
	}

	public void putBack(int iden, boolean[] pos, boolean[] data) throws Exception {
		putBack(Utils.fromInt(iden, lengthOfIden), pos, data);
	}

	public boolean[] read(int iden, int pos, int newPos) throws Exception {
		return read(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos));
	}

	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
		write(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos), data);
	}

	public boolean[] read(int iden, boolean[] pos, boolean[] newPos) throws Exception {
		boolean[] r = readAndRemove(iden, pos);
		putBack(iden, newPos, r);
		return r;
	}

	public void write(int iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		readAndRemove(iden, pos);
		putBack(iden, newPos, data);
	}

}
