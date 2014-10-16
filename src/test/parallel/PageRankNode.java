package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;

public class PageRankNode<T> extends GraphNode<T> {
	static int INT_LEN = 32;

	T[] pr;
	T[] l;

	public PageRankNode(T[] u, T[] v, T isVertex, CompEnv<T> env) {
		super(u, v, isVertex);
		this.pr = env.newTArray(u.length);
		this.l = env.newTArray(u.length);
	}

	public PageRankNode(CompEnv<T> env) {
		super(env);
		try {
//			this.pr = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_V, PageRank.FLOAT_P));
//			this.l = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
			this.pr = env.inputOfAlice(Utils.fromFixPoint(0, 40, 20));
			this.l = env.inputOfAlice(Utils.fromFixPoint(0, 40, 20));
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	public PageRankNode() {
		
	}

	@Override
	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		super.send(os, env);
		NetworkUtil.send(os, pr, env);
		NetworkUtil.send(os, l, env);
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		super.read(is, env);
//		this.pr = NetworkUtil.read(is, INT_LEN, env);
//		this.l = NetworkUtil.read(is, INT_LEN, env);
		this.pr = NetworkUtil.read(is, 40, env);
		this.l = NetworkUtil.read(is, 40, env);
	}

	@Override
	public GraphNode<T> mux(GraphNode<T> b1, T condition, CompEnv<T> env) {
		IntegerLib<T> lib = new IntegerLib<T>(env);
		PageRankNode<T> ret = new PageRankNode<T>(env);
		ret.u = lib.mux(((PageRankNode<T>) b1).u, this.u, condition);
		ret.v = lib.mux(((PageRankNode<T>) b1).v, this.v, condition);
		ret.pr = lib.mux(((PageRankNode<T>) b1).pr, this.pr, condition);
		ret.l = lib.mux(((PageRankNode<T>) b1).l, this.l, condition);
		ret.isVertex = lib.mux(((PageRankNode<T>) b1).isVertex, this.isVertex, condition);
		return ret;
	}

	@Override
	public GraphNode<T> getCopy(CompEnv<T> env) {
		PageRankNode<T> a = new PageRankNode<>(env);
		a.u = this.u;
		a.v = this.v;
		a.pr = this.pr;
		a.l = this.l;
		a.isVertex = this.isVertex;
		return a;
	}

	@Override
	public T[] flatten(CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		vert[0] = (T) isVertex;
		return Utils.flatten(env, u, v, pr, l, vert);
	}

	@Override
	public void unflatten(T[] flat, CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		Utils.unflatten(flat, u, v, pr, l, vert);
		isVertex = vert[0];
	}
}
