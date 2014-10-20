package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;

public class HistogramNode<T> extends GraphNode<T> {

	static int LEN = 20;

	T[] count;

	public HistogramNode(T[] u, T[] v, T isVertex, CompEnv<T> env) {
		super(u, v, isVertex);
		try {
			this.count = env.inputOfAlice(Utils.fromInt(0, LEN));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HistogramNode(CompEnv<T> env) {
		super(env);
		try {
			this.count = env.inputOfAlice(Utils.fromInt(0, LEN));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public HistogramNode() {

	}

	@Override
	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		super.send(os, env);
		NetworkUtil.send(os, count, env);
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		super.read(is, env);
		this.count = NetworkUtil.read(is, LEN, env);
	}

	@Override
	public GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env) {
		IntegerLib<T> lib = new IntegerLib<T>(env);
		HistogramNode<T> ret = new HistogramNode<T>(env);
		ret.u = lib.mux(b.u, this.u, condition);
		ret.v = lib.mux(b.v, this.v, condition);
		ret.isVertex = lib.mux(b.isVertex, this.isVertex, condition);
		ret.count = lib.mux(((HistogramNode<T>) b).count, this.count, condition);
		return ret;
	}

	@Override
	public GraphNode<T> getCopy(CompEnv<T> env) {
		HistogramNode<T> a = new HistogramNode<>(env);
		a.u = this.u;
		a.v = this.v;
		a.isVertex = this.isVertex;
		a.count = this.count;
		return a;
	}

	@Override
	public T[] flatten(CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		vert[0] = (T) isVertex;
		return Utils.flatten(env, u, v, vert, count);
	}

	@Override
	public void unflatten(T[] flat, CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		Utils.unflatten(flat, u, v, vert, count);
		isVertex = vert[0];
	}

}
