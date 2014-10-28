package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.ArithmeticLib;
import circuits.IntegerLib;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;

public class AlsNode<T> extends GraphNode<T> {

	public static final int FIX_POINT_WIDTH = 40;
	public static final int OFFSET = 20;

	public T[] rating;
	public T[][] up;
	public T[][] vp;
	public T isU;
	public T isV;
	public T[][][] M;

	ArithmeticLib<T> flib;
	IntegerLib<T> lib;

	public AlsNode(CompEnv<T> env) {
		super(env);
		flib = new FixedPointLib<>(env, FIX_POINT_WIDTH, OFFSET);
		lib = new IntegerLib<T>(env);
		// initialize all to 0
		try {
			rating = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
			up = env.newTArray(Als.D, FIX_POINT_WIDTH);
			vp = env.newTArray(Als.D, FIX_POINT_WIDTH);
			M = env.newTArray(Als.D, Als.D, FIX_POINT_WIDTH);
			for (int i = 0; i < Als.D; ++i) {
				up[i] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
				vp[i] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
				for (int j = 0; j < Als.D; j++) {
					M[i][j] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
				}
			}
			this.isU = env.inputOfAlice(false);
			this.isV = env.inputOfAlice(false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public AlsNode(T[] u,
			T[] v,
			T isVertex,
			T[] rating,
			T[][] up,
			T[][] vp,
			T isU,
			T isV,
			CompEnv<T> env) {
		super(u, v, isVertex);
		flib = new FixedPointLib<>(env, FIX_POINT_WIDTH, OFFSET);
		lib = new IntegerLib<T>(env);
		this.rating = rating;
		this.up = up;
		this.vp = vp;
		M = env.newTArray(Als.D, Als.D, FIX_POINT_WIDTH);
		for (int i = 0; i < Als.D; ++i) {
			for (int j = 0; j < Als.D; j++) {
				try {
					M[i][j] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.isU = isU;
		this.isV = isV;
	}

	public AlsNode() {

	}

	public void solveU(CompEnv<T> env) {
		T[] tempM = env.newTArray(Als.D);
		T[] tempVp = env.newTArray(Als.D);
		for (int i = 0; i < Als.D; i++) {
			for (int j = 0; j < Als.D; j++) {
				tempM = flib.multiply(vp[i], vp[j]);
				M[i][j] = lib.mux(tempM, M[i][j], isVertex);
			}
		}
		for (int i = 0; i < Als.D; i++) {
			tempVp = flib.multiply(rating, vp[i]);
			vp[i] = lib.mux(tempVp, vp[i], isVertex);
		}
	}

	public void solveV(CompEnv<T> env) {
		T[] tempM = env.newTArray(Als.D);
		T[] tempUp = env.newTArray(Als.D);
		for (int i = 0; i < Als.D; i++) {
			for (int j = 0; j < Als.D; j++) {
				tempM = flib.multiply(up[i], up[j]);
				M[i][j] = lib.mux(tempM, M[i][j], isVertex);
			}
		}
		for (int i = 0; i < Als.D; i++) {
			tempUp = flib.multiply(rating, up[i]);
			up[i] = lib.mux(tempUp, up[i], isVertex);
		}
	}

	@Override
	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		super.send(os, env);
		NetworkUtil.send(os, rating, env);
		NetworkUtil.send(os, up, env);
		NetworkUtil.send(os, vp, env);
		NetworkUtil.send(os, isU, env);
		NetworkUtil.send(os, isV, env);
		NetworkUtil.send(os, M, env);
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		super.read(is, env);
		this.rating = NetworkUtil.read(is, FIX_POINT_WIDTH, env);
		this.up = NetworkUtil.read(is, Als.D, FIX_POINT_WIDTH, env);
		this.vp = NetworkUtil.read(is, Als.D, FIX_POINT_WIDTH, env);
		this.isU = NetworkUtil.read(is, env);
		this.isV = NetworkUtil.read(is, env);
		this.M = NetworkUtil.read(is, Als.D, Als.D, FIX_POINT_WIDTH, env);
	}

	@Override
	public GraphNode<T> mux(GraphNode<T> b1, T condition, CompEnv<T> env) {
		IntegerLib<T> lib = new IntegerLib<T>(env);
		AlsNode<T> ret = new AlsNode<T>(env);
		ret.u = lib.mux(((AlsNode<T>) b1).u, this.u, condition);
		ret.v = lib.mux(((AlsNode<T>) b1).v, this.v, condition);
		ret.isVertex = lib.mux(((AlsNode<T>) b1).isVertex, this.isVertex, condition);
		ret.rating = lib.mux(((AlsNode<T>) b1).rating, this.rating, condition);
		ret.up = lib.mux(((AlsNode<T>) b1).up, this.up, condition);
		ret.vp = lib.mux(((AlsNode<T>) b1).vp, this.vp, condition);
		ret.isU = lib.mux(((AlsNode<T>) b1).isU, this.isU, condition);
		ret.isV = lib.mux(((AlsNode<T>) b1).isV, this.isV, condition);
		ret.M = lib.mux(((AlsNode<T>) b1).M, this.M, condition);
		return ret;
	}

	@Override
	public GraphNode<T> getCopy(CompEnv<T> env) {
		AlsNode<T> a = new AlsNode<>(env);
		a.u = this.u;
		a.v = this.v;
		a.isVertex = this.isVertex;
		a.rating = this.rating;
		a.up = this.up;
		a.vp = this.vp;
		a.isU = this.isU;
		a.isV = this.isV;
		a.M = this.M;
		return a;
	}

	@Override
	public T[] flatten(CompEnv<T> env) {
		T[] vert = env.newTArray(3);
		vert[0] = (T) isVertex;
		vert[1] = (T) isU;
		vert[2] = (T) isV;
		T[] flattenedUserProfile = Utils.flatten(env, up);
		T[] flattenedItemProfile = Utils.flatten(env, vp);
		T[] flattenedM = Utils.flatten(env, Utils.flatten(env, M));
		return Utils.flatten(env, u, v, vert, rating, flattenedUserProfile, flattenedItemProfile, flattenedM);
	}

	@Override
	public void unflatten(T[] flat, CompEnv<T> env) {
		T[] vert = env.newTArray(3);
		T[] flattenedUserProfile = env.newTArray(Als.D * FIX_POINT_WIDTH);
		T[] flattenedItemProfile = env.newTArray(Als.D * FIX_POINT_WIDTH);
		T[] flattenedM = env.newTArray(Als.D * Als.D * FIX_POINT_WIDTH);
		Utils.unflatten(flat, u, v, vert, rating, flattenedUserProfile, flattenedItemProfile, flattenedM);
		Utils.unflatten(flattenedUserProfile, up);
		Utils.unflatten(flattenedItemProfile, vp);
		T[][] m = env.newTArray(Als.D, Als.D * FIX_POINT_WIDTH);
		Utils.unflatten(flattenedM, m);
		for (int i = 0; i < Als.D; i++) {
			Utils.unflatten(m[i], M[i]);
		}
		isVertex = vert[0];
		isU = vert[1];
		isV = vert[2];
	}

}
