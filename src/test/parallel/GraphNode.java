package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.Comparator;
import circuits.IntegerLib;
import flexsc.CompEnv;

public abstract class GraphNode<T> {

	static int VERTEX_LEN = 10;

	T[] u;
	T[] v;
	T isVertex;
	
	public GraphNode(T[] u, T[] v, T isVertex) {
		this.u = u;
		this.v = v;
		this.isVertex = isVertex;
	}

	public GraphNode(CompEnv<T> env) {
		try {
			this.u = env.inputOfAlice(Utils.fromInt(0, VERTEX_LEN));
			this.v = env.inputOfAlice(Utils.fromInt(0, VERTEX_LEN));
			this.isVertex = env.inputOfAlice(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GraphNode() {

	}

	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		NetworkUtil.send(os, u, env);
		NetworkUtil.send(os, v, env);
		NetworkUtil.send(os, isVertex, env);
	}

	public void read(InputStream is, CompEnv<T> env) throws IOException {
		this.u = NetworkUtil.read(is, VERTEX_LEN, env);
		this.v = NetworkUtil.read(is, VERTEX_LEN, env);
		this.isVertex = NetworkUtil.read(is, env);
	}

	public abstract GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env);

	public abstract GraphNode<T> getCopy(CompEnv<T> env);

	public void swapEdgeDirections() {
		T[] temp = this.u;
		this.u = this.v;
		this.v = temp;
	}

	public abstract T[] flatten(CompEnv<T> env);

	public abstract void unflatten(T[] flat, CompEnv<T> env);

	// sort on u and have the vertex last/first
	public Comparator<T> getComparator(final CompEnv<T> env, final boolean isVertexLast) {
		Comparator<T> firstSortComparator = new Comparator<T>() {

			@Override
			public T leq(GraphNode<T> n1, GraphNode<T> n2) {
//				PageRankNode<T> n1 = (PageRankNode<T>) node1;
//				PageRankNode<T> n2 = (PageRankNode<T>) node2;
				IntegerLib<T> lib = new IntegerLib<>(env);
//							T v = lib.geq(vi, vj);
//							T eq = lib.eq(ui, uj);
//							T u = lib.leq(ui, uj);
//							return lib.mux(u, v, eq);

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
		};
		return firstSortComparator;
	}

	public static <T> Comparator<T> vertexFirstComparator(final CompEnv<T> env) {
		Comparator<T> firstSortComparator = new Comparator<T>() {

			@Override
			public T leq(GraphNode<T> n1, GraphNode<T> n2) {
//				PageRankNode<T> n1 = (PageRankNode<T>) node1;
//				PageRankNode<T> n2 = (PageRankNode<T>) node2;
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
		};
		return firstSortComparator;
	}
}