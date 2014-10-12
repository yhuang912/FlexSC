package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;
import circuits.Comparator;
import circuits.IntegerLib;
import flexsc.CompEnv;

public abstract class GraphNode<T> {

	static int VERTEX_LEN = 32;

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
			this.u = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
			this.v = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
			this.isVertex = env.inputOfAlice(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GraphNode() {

	}

	public abstract void send(OutputStream os, CompEnv<T> env) throws IOException;

	public abstract void read(InputStream is, CompEnv<T> env) throws IOException;

	public abstract GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env);

	public abstract Comparator<T> getComparator(CompEnv<T> env, boolean isVertexLast);

	public abstract GraphNode<T> getCopy(CompEnv<T> env);

	public void swapEdgeDirections() {
		T[] temp = this.u;
		this.u = this.v;
		this.v = temp;
	}
}