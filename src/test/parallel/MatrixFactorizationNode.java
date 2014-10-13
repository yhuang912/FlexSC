package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import test.Utils;
import circuits.IntegerLib;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

public class MatrixFactorizationNode<T> extends GraphNode<T> {
	public static final int FIX_POINT_WIDTH = 40;
	public static final int OFFSET = 20;

	public static final int USER_ID_WIDTH = 10;
	public static final int PROFILE_ID_WIDTH = 10;
	public static final int D = 20;

	public T[] userId;// s1
	public T[] itemId;// s2
	public T isProfile;// s3
	public T[] ratings;// s4
	public T[][] userProfile;// s5
	public T[][] itemProfile;// s6

	CompEnv<T> env;
	FixedPointLib<T> lib;
	IntegerLib<T> ilib;

	public MatrixFactorizationNode(CompEnv<T> e) {

		env = e;
		lib = new FixedPointLib<>(e, FIX_POINT_WIDTH, OFFSET);
		ilib = new IntegerLib<T>(e);
		// initialize all to 0
		userId = ilib.zeros(USER_ID_WIDTH);
		itemId = ilib.zeros(PROFILE_ID_WIDTH);
		isProfile = ilib.SIGNAL_ZERO;
		ratings = lib.publicValue(0);
		userProfile = env.newTArray(D, 1);
		itemProfile = env.newTArray(D, 1);
		for (int i = 0; i < D; ++i) {
			userProfile[i] = ratings;
			itemProfile[i] = ratings;
		}
	}

	public int numberOfBits() {
		return USER_ID_WIDTH + PROFILE_ID_WIDTH + 1 + FIX_POINT_WIDTH * (2 * D + 1);
	}

	// step 6.
	public void computeGradient(double gamma) {
		T[] twoGamma = lib.publicValue(gamma * 2);

		T[] innerProductResult = innerProduct(userProfile, itemProfile);
		T[] scalar = lib.sub(ratings, innerProductResult);
		scalar = lib.multiply(twoGamma, scalar);
		T[][] newUserProfile = multiplyToVector(scalar, itemProfile);

		for (int i = 0; i < userProfile.length; ++i)
			userProfile[i] = ilib.mux(userProfile[i], newUserProfile[i],
					isProfile);

		T[][] newItemProfile = multiplyToVector(scalar, userProfile);

		for (int i = 0; i < itemProfile.length; ++i)
			itemProfile[i] = ilib.mux(itemProfile[i], newItemProfile[i],
					isProfile);
	}

	public T[] innerProduct(T[][] userProfile, T[][] itemProfile) {
		T[] res = lib.publicValue(0);
		for (int i = 0; i < userProfile.length; ++i)
			res = lib.add(lib.multiply(userProfile[i], itemProfile[i]), res);
		return res;
	}

	public T[][] multiplyToVector(T[] scalar, T[][] vector) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(InputStream is, CompEnv<T> env) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphNode<T> getCopy(CompEnv<T> env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[] flatten(CompEnv<T> env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unflatten(T[] flat, CompEnv<T> env) {
		// TODO Auto-generated method stub
		
	}

}