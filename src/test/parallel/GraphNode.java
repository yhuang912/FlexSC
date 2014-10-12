package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
		IntegerLib<T> lib = new IntegerLib<>(env);
		this.u = lib.zeros(VERTEX_LEN);
		this.v = lib.zeros(VERTEX_LEN);
		this.isVertex = env.ZERO();
	}

	public GraphNode() {

	}

	public abstract void send(OutputStream os, CompEnv<T> env) throws IOException;

	public abstract void read(InputStream is, CompEnv<T> env) throws IOException;

	public abstract GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env);
}