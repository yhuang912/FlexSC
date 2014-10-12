package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.Comparator;
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
			this.pr = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_V, PageRank.FLOAT_P));
			this.l = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	public PageRankNode() {
		
	}

	// sort on u and have the vertex last/first
	@Override
	public Comparator<T> getComparator(final CompEnv<T> env, final boolean isVertexLast) {
		Comparator<T> firstSortComparator = new Comparator<T>() {

			@Override
			public T leq(GraphNode<T> node1, GraphNode<T> node2) {
				PageRankNode<T> n1 = (PageRankNode<T>) node1;
				PageRankNode<T> n2 = (PageRankNode<T>) node2;
				IntegerLib<T> lib = new IntegerLib<>(env);
//					T v = lib.geq(vi, vj);
//					T eq = lib.eq(ui, uj);
//					T u = lib.leq(ui, uj);
//					return lib.mux(u, v, eq);

				T[] v1 = env.newTArray(1), v2 = env.newTArray(1);
				if (isVertexLast) {
					v1[0] = n1.isVertex;
					v2[0] = n2.isVertex;
				} else {
					v1[0] = lib.not(n1.isVertex);
					v2[0] = lib.not(n2.isVertex);
				}
				T[] ai = (T[]) Utils.flatten(env, v1, n1.u);
				T[] aj = (T[]) Utils.flatten(env, v2, n2.u);
				return lib.leq(ai, aj);
			}

			@Override
			public void swap(GraphNode<T> node1, GraphNode<T> node2, T swap) {
				PageRankNode<T> n1 = (PageRankNode<T>) node1;
				PageRankNode<T> n2 = (PageRankNode<T>) node2;

				IntegerLib<T> lib = new IntegerLib<>(env);
				T[] s = lib.mux(n2.u, n1.u, swap);
		    	s = lib.xor(s, n1.u);
		    	T[] ki = lib.xor(n2.u, s);
		    	T[] kj = lib.xor(n1.u, s);
		    	n1.u = ki;
		    	n2.u = kj;

		    	s = lib.mux(n2.v, n1.v, swap);
		    	s = lib.xor(s, n1.v);
		    	ki = lib.xor(n2.v, s);
		    	kj = lib.xor(n1.v, s);
		    	n1.v = ki;
		    	n2.v = kj;

		    	s = lib.mux(n2.pr, n1.pr, swap);
		    	s = lib.xor(s, n1.pr);
		    	ki = lib.xor(n2.pr, s);
		    	kj = lib.xor(n1.pr, s);
		    	n1.pr = ki;
		    	n2.pr = kj;

		    	s = lib.mux(n2.l, n1.l, swap);
		    	s = lib.xor(s, n1.l);
		    	ki = lib.xor(n2.l, s);
		    	kj = lib.xor(n1.l, s);
		    	n1.l = ki;
		    	n2.l = kj;

		    	T s2 = lib.mux(n2.isVertex, n1.isVertex, swap);
		    	s2 = lib.xor(s2, n1.isVertex);
		    	T ki2 = lib.xor(n2.isVertex, s2);
		    	T kj2 = lib.xor(n1.isVertex, s2);
		    	n1.isVertex = ki2;
		    	n2.isVertex = kj2;
			}
		};
		return firstSortComparator;
	}

	@Override
	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		NetworkUtil.send(os, u, env);
		NetworkUtil.send(os, v, env);
		NetworkUtil.send(os, pr, env);
		NetworkUtil.send(os, l, env);
		NetworkUtil.send(os, isVertex, env);
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		this.u = NetworkUtil.read(is, INT_LEN, env);
		this.v = NetworkUtil.read(is, INT_LEN, env);
		this.pr = NetworkUtil.read(is, INT_LEN, env);
		this.l = NetworkUtil.read(is, INT_LEN, env);
		this.isVertex = NetworkUtil.read(is, env); 
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

	public static <T> Comparator<T> vertexFirstComparator(final CompEnv<T> env) {
		Comparator<T> firstSortComparator = new Comparator<T>() {

			@Override
			public T leq(GraphNode<T> node1, GraphNode<T> node2) {
				PageRankNode<T> n1 = (PageRankNode<T>) node1;
				PageRankNode<T> n2 = (PageRankNode<T>) node2;
				IntegerLib<T> lib = new IntegerLib<>(env);

				T[] v1 = env.newTArray(2), v2 = env.newTArray(2);
				v1[1] = env.ZERO();
				v2[1] = env.ZERO();
				v1[0] = lib.not(n1.isVertex);
				v2[0] = lib.not(n2.isVertex);
				T[] ai = (T[]) Utils.flatten(env, n1.u, v1);
				T[] aj = (T[]) Utils.flatten(env, n2.u, v2);
				return lib.leq(ai, aj);
			}

			@Override
			public void swap(GraphNode<T> node1, GraphNode<T> node2, T swap) {
				PageRankNode<T> n1 = (PageRankNode<T>) node1;
				PageRankNode<T> n2 = (PageRankNode<T>) node2;

				IntegerLib<T> lib = new IntegerLib<>(env);
				T[] s = lib.mux(n2.u, n1.u, swap);
		    	s = lib.xor(s, n1.u);
		    	T[] ki = lib.xor(n2.u, s);
		    	T[] kj = lib.xor(n1.u, s);
		    	n1.u = ki;
		    	n2.u = kj;

		    	s = lib.mux(n2.v, n1.v, swap);
		    	s = lib.xor(s, n1.v);
		    	ki = lib.xor(n2.v, s);
		    	kj = lib.xor(n1.v, s);
		    	n1.v = ki;
		    	n2.v = kj;

		    	s = lib.mux(n2.pr, n1.pr, swap);
		    	s = lib.xor(s, n1.pr);
		    	ki = lib.xor(n2.pr, s);
		    	kj = lib.xor(n1.pr, s);
		    	n1.pr = ki;
		    	n2.pr = kj;

		    	s = lib.mux(n2.l, n1.l, swap);
		    	s = lib.xor(s, n1.l);
		    	ki = lib.xor(n2.l, s);
		    	kj = lib.xor(n1.l, s);
		    	n1.l = ki;
		    	n2.l = kj;

		    	T s2 = lib.mux(n2.isVertex, n1.isVertex, swap);
		    	s2 = lib.xor(s2, n1.isVertex);
		    	T ki2 = lib.xor(n2.isVertex, s2);
		    	T kj2 = lib.xor(n1.isVertex, s2);
		    	n1.isVertex = ki2;
		    	n2.isVertex = kj2;
			}
		};
		return firstSortComparator;
	}
}
