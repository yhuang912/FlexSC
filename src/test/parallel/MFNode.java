package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;

public class MFNode<T> extends GraphNode<T> {
	public static final int FIX_POINT_WIDTH = 40;
	public static final int OFFSET = 20;

	public static final int D = 2;

	public T[] rating;// s4
	public T[][] userProfile;// s5
	public T[][] itemProfile;// s6

	FixedPointLib<T> lib;
	IntegerLib<T> ilib;

	public MFNode(CompEnv<T> env) {
		super(env);
		lib = new FixedPointLib<>(env, FIX_POINT_WIDTH, OFFSET);
		ilib = new IntegerLib<T>(env);
		try {
			rating = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
			setProfiles(env);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public MFNode(CompEnv<T> env, boolean identity) {
		super(env);
		lib = new FixedPointLib<>(env, FIX_POINT_WIDTH, OFFSET);
		ilib = new IntegerLib<T>(env);
		// initialize all to 0
		try {
			rating = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
			userProfile = env.newTArray(D, FIX_POINT_WIDTH);
			itemProfile = env.newTArray(D, FIX_POINT_WIDTH);
			for (int i = 0; i < D; ++i) {
				userProfile[i] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
				itemProfile[i] = env.inputOfAlice(Utils.fromFixPoint(0, FIX_POINT_WIDTH, OFFSET));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public MFNode(T[] u,
			T[] v,
			T isVertex,
			T[] rating,
			CompEnv<T> env) {
		super(u, v, isVertex);
		lib = new FixedPointLib<>(env, FIX_POINT_WIDTH, OFFSET);
		ilib = new IntegerLib<T>(env);
		this.rating = rating;
		try {
			setProfiles(env);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setProfiles(CompEnv<T> env) throws IOException {
		userProfile = env.newTArray(D, FIX_POINT_WIDTH);
		itemProfile = env.newTArray(D, FIX_POINT_WIDTH);
		for (int i = 0; i < D; ++i) {
			userProfile[i] = env.inputOfAlice(Utils.fromFixPoint(Math.random(), FIX_POINT_WIDTH, OFFSET));
			itemProfile[i] = env.inputOfAlice(Utils.fromFixPoint(Math.random(), FIX_POINT_WIDTH, OFFSET));
		}
	}

	// used by sort gadget
	public MFNode() {

	}

	public int numberOfBits() {
		return VERTEX_LEN * 2 + 1 + FIX_POINT_WIDTH * (2 * D + 1);
	}

	// step 6.
	public void computeGradient(double gamma, CompEnv<T> env) {
		T[] twoGamma = lib.publicValue(gamma * 2);

		T[] innerProductResult = innerProduct(userProfile, itemProfile);
		T[] scalar = lib.sub(rating, innerProductResult);
		scalar = lib.multiply(twoGamma, scalar);
		T[][] newUserProfile = multiplyToVector(scalar, itemProfile, env);

		for (int i = 0; i < userProfile.length; ++i)
			userProfile[i] = ilib.mux(userProfile[i], newUserProfile[i],
					ilib.not(isVertex));

		T[][] newItemProfile = multiplyToVector(scalar, userProfile, env);

		for (int i = 0; i < itemProfile.length; ++i)
			itemProfile[i] = ilib.mux(itemProfile[i], newItemProfile[i],
					ilib.not(isVertex));
	}

	public T[] innerProduct(T[][] userProfile, T[][] itemProfile) {
		T[] res = lib.publicValue(0);
		for (int i = 0; i < userProfile.length; ++i)
			res = lib.add(lib.multiply(userProfile[i], itemProfile[i]), res);
		return res;
	}

	public T[][] multiplyToVector(T[] scalar, T[][] vector, CompEnv<T> env) {
		T[][] res = env.newTArray(vector.length, 1);
		for (int i = 0; i < vector.length; ++i)
			res[i] = lib.multiply(vector[i], scalar);
		return res;
	}

//	// one more bit for sign.
//	public T[] keyRow13() {
//		T[] res = ilib.zeros(userId.length + 2);
//		System.arraycopy(userId, 0, res, 1, userId.length);
//		res[0] = isProfile;
//		return res;
//	}
//
//	public T[] keyrow23() {
//		T[] res = ilib.zeros(itemId.length + 2);
//		System.arraycopy(itemId, 0, res, 1, itemId.length);
//		res[0] = isProfile;
//		return res;
//	}
//
//	public T[] keyrow32() {
//		T[] res = ilib.zeros(itemId.length + 2);
//		System.arraycopy(itemId, 0, res, 0, itemId.length);
//		res[res.length - 2] = isProfile;
//		return res;
//	}

//	public T[] toBitArray() {
//		T[] res = env.newTArray(numberOfBits());
//		int now = 0;
//		System.arraycopy(userId, 0, res, now, userId.length);
//		now += userId.length;
//
//		System.arraycopy(itemId, 0, res, now, itemId.length);
//		now += itemId.length;
//
//		res[now] = isProfile;
//		now += 1;
//
//		System.arraycopy(ratings, 0, res, 0, ratings.length);
//		now += FIX_POINT_WIDTH;
//
//		for (int i = 0; i < userProfile.length; ++i) {
//			System.arraycopy(userProfile[i], 0, res, now, userProfile[i].length);
//			now += FIX_POINT_WIDTH;
//		}
//		for (int i = 0; i < itemProfile.length; ++i) {
//			System.arraycopy(itemProfile[i], 0, res, now, itemProfile[i].length);
//			now += FIX_POINT_WIDTH;
//		}
//		return res;
//	}
//
//	public void updateValue(T[] bits) {
//		int now = 0;
//
//		userId = Arrays.copyOfRange(bits, now, now + userId.length);
//		now += userId.length;
//
//		itemId = Arrays.copyOfRange(bits, now, now + itemId.length);
//		now += itemId.length;
//
//		isProfile = bits[now];
//		now += 1;
//
//		ratings = Arrays.copyOfRange(bits, now, now + FIX_POINT_WIDTH);
//		now += FIX_POINT_WIDTH;
//
//		userProfile = env.newTArray(D, 1);
//		for (int i = 0; i < userProfile.length; ++i) {
//			userProfile[i] = Arrays.copyOfRange(bits, now, now + FIX_POINT_WIDTH);
//			now += FIX_POINT_WIDTH;
//		}
//
//		for (int i = 0; i < itemProfile.length; ++i) {
//			itemProfile[i] = Arrays.copyOfRange(bits, now, now + FIX_POINT_WIDTH);
//			now += FIX_POINT_WIDTH;
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//		CompEnv<Boolean> env = CompEnv.getEnv(Mode.COUNT, Party.Alice, null,
//				null);
//		MatrixFactorizationNode<Boolean> a = new MatrixFactorizationNode<Boolean>(env);
//		a.itemId = a.ilib.toSignals(2, a.USER_ID_WIDTH);
//		Boolean[] bb = a.toBitArray();
//		MatrixFactorizationNode<Boolean> b = new MatrixFactorizationNode<Boolean>(env);
//		b.updateValue(bb);
//		System.out.println(Utils.toInt(env.outputToAlice(a.itemId)));
//	}

	@Override
	public void send(OutputStream os, CompEnv<T> env) throws IOException {
		super.send(os, env);
		NetworkUtil.send(os, rating, env);
		NetworkUtil.send(os, userProfile, env);
		NetworkUtil.send(os, itemProfile, env);
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		super.read(is, env);
		this.rating = NetworkUtil.read(is, FIX_POINT_WIDTH, env);
		this.userProfile = NetworkUtil.read(is, D, FIX_POINT_WIDTH, env);
		this.itemProfile = NetworkUtil.read(is, D, FIX_POINT_WIDTH, env);
	}

	@Override
	public GraphNode<T> mux(GraphNode<T> b1, T condition, CompEnv<T> env) {
		IntegerLib<T> lib = new IntegerLib<T>(env);
		MFNode<T> ret = new MFNode<T>(env);
		ret.u = lib.mux(((MFNode<T>) b1).u, this.u, condition);
		ret.v = lib.mux(((MFNode<T>) b1).v, this.v, condition);
		ret.isVertex = lib.mux(((MFNode<T>) b1).isVertex, this.isVertex, condition);
		ret.rating = lib.mux(((MFNode<T>) b1).rating, this.rating, condition);
		ret.userProfile = lib.mux(((MFNode<T>) b1).userProfile, this.userProfile, condition);
		ret.itemProfile = lib.mux(((MFNode<T>) b1).itemProfile, this.itemProfile, condition);
		return ret;
	}

	@Override
	public GraphNode<T> getCopy(CompEnv<T> env) {
		MFNode<T> a = new MFNode<>(env);
		a.u = this.u;
		a.v = this.v;
		a.isVertex = this.isVertex;
		a.rating = this.rating;
		a.userProfile = this.userProfile;
		a.itemProfile = this.itemProfile;
		return a;
	}

	@Override
	public T[] flatten(CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		vert[0] = (T) isVertex;
		T[] flattenedUserProfile = Utils.flatten(env, userProfile);
		T[] flattenedItemProfile = Utils.flatten(env, itemProfile);
		return Utils.flatten(env, u, v, vert, rating, flattenedUserProfile, flattenedItemProfile);
	}

	@Override
	public void unflatten(T[] flat, CompEnv<T> env) {
		T[] vert = env.newTArray(1);
		T[] flattenedUserProfile = env.newTArray(D * FIX_POINT_WIDTH);
		T[] flattenedItemProfile = env.newTArray(D * FIX_POINT_WIDTH);
		Utils.unflatten(flat, u, v, vert, rating, flattenedUserProfile, flattenedItemProfile);
		Utils.unflatten(flattenedUserProfile, userProfile);
		Utils.unflatten(flattenedItemProfile, itemProfile);
		isVertex = vert[0];
	}

}