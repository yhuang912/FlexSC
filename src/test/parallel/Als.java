package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import circuits.ArithmeticLib;
import circuits.IntegerLib;
import circuits.arithmetic.DenseMatrixLib;
import circuits.arithmetic.FixedPointLib;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import ot.IncorrectOtUsageException;
import test.Utils;
import flexsc.CompEnv;
import flexsc.Party;
import gc.BadLabelException;

public class Als<T> implements ParallelGadget<T> {

	public static int ITERATIONS = 1;
	public static double GAMMA = 0.0002;
	public static double MU = 0.02;
	public static int USERS = 0;
	public static int ITEMS = 0;
	public static final int D = 2;

	private Object[] getInput(int inputLength) throws IOException {
		Machine.RAND = new double[MatrixFactorization.RAND_LIM];
		BufferedReader reader = new BufferedReader(new FileReader("rand.out"));
		for (int i = 0; i < MatrixFactorization.RAND_LIM; i++) {
			Machine.RAND[i] = Double.parseDouble(reader.readLine());
		}
		reader.close();
		int[] u = new int[inputLength];
		int[] v = new int[inputLength];
		boolean[] isVertex = new boolean[inputLength];
		double[] rating = new double[inputLength];
		double[][] userProfile = new double[inputLength][D];
		double[][] itemProfile = new double[inputLength][D];
		boolean[] isU = new boolean[inputLength];
		boolean[] isV = new boolean[inputLength];
		BufferedReader br = new BufferedReader(new FileReader("in/als" + inputLength + ".in"));
		USERS = Integer.parseInt(br.readLine());
		ITEMS = Integer.parseInt(br.readLine());
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			u[i] = Integer.parseInt(split[0]);
			v[i] = Integer.parseInt(split[1]);
			isVertex[i] = (Integer.parseInt(split[2]) == 1);
			rating[i] = Double.parseDouble(split[3]);
			for (int j = 0; j < D; j++) {
				userProfile[i][j] = MatrixFactorization.getRandom();
			}
			for (int j = 0; j < D; j++) {
				itemProfile[i][j] = MatrixFactorization.getRandom();
			}
			if (i < USERS) {
				isU[i] = true;
				isV[i] = false;
			} else if (i < USERS + ITEMS) {
				isU[i] = false;
				isV[i] = true;
			} else {
				isU[i] = false;
				isV[i] = false;
			}
		}
		br.close();
		boolean[][] ua = new boolean[u.length][];
		boolean[][] va = new boolean[v.length][];
		boolean[] isVertexa = new boolean[isVertex.length];
		boolean[][] ratinga = new boolean[rating.length][];
		boolean[][][] userProfilea = new boolean[userProfile.length][D][];
		boolean[][][] itemProfilea = new boolean[itemProfile.length][D][];
		boolean[] isUa = new boolean[isVertex.length];
		boolean[] isVa = new boolean[isVertex.length];
		for (int i = 0; i < u.length; i++) {
			ua[i] = Utils.fromInt(u[i], GraphNode.VERTEX_LEN);
			va[i] = Utils.fromInt(v[i], GraphNode.VERTEX_LEN);
			isVertexa[i] = isVertex[i];
			ratinga[i] = Utils.fromFixPoint(rating[i], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET);
			for (int j = 0; j < D; j++) {
				userProfilea[i][j] = Utils.fromFixPoint(userProfile[i][j], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET);
			}
			for (int j = 0; j < D; j++) {
				itemProfilea[i][j] = Utils.fromFixPoint(itemProfile[i][j], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET);
			}
			isUa[i] = isU[i];
			isVa[i] = isV[i];
		}
		Object[] ret = new Object[8];
		ret[0] = ua;
		ret[1] = va;
		ret[2] = isVertexa;
		ret[3] = ratinga;
		ret[4] = userProfilea;
		ret[5] = itemProfilea;
		ret[6] = isUa;
		ret[7] = isVa;
		return ret;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException, IncorrectOtUsageException {
		T[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[] tIsVertex = env.newTArray(inputLength /* number of entries in the input */);
		T[][] trating = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][][] tUserProfile = env.newTArray(inputLength /* number of entries in the input */, D, 0);
		T[][][] tItemProfile = env.newTArray(inputLength /* number of entries in the input */, D, 0);
		T[] tIsU = env.newTArray(inputLength /* number of entries in the input */);
		T[] tIsV = env.newTArray(inputLength /* number of entries in the input */);
		if (isGen) {
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			tIsVertex = env.inputOfBob(new boolean[tIsVertex.length]);
			for(int i = 0; i < trating.length; ++i)
				trating[i] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
			for(int i = 0; i < tUserProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tUserProfile[i][j] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
				}
			}
			for(int i = 0; i < tItemProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tItemProfile[i][j] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
				}
			}
			tIsU = env.inputOfBob(new boolean[tIsVertex.length]);
			tIsV = env.inputOfBob(new boolean[tIsVertex.length]);
		} else {
			Object[] input = getInput(inputLength);
			boolean[][] u = (boolean[][]) input[0];
			boolean[][] v = (boolean[][]) input[1];
			boolean[] isVertex = (boolean[]) input[2];
			boolean[][] rating = (boolean[][]) input[3];
			boolean[][][] userProfile = (boolean[][][]) input[4];
			boolean[][][] itemProfile = (boolean[][][]) input[5];
			boolean[] isU = (boolean[]) input[6];
			boolean[] isV = (boolean[]) input[7];
			for (int i = 0; i < tu.length; ++i) {
				tu[i] = env.inputOfBob((boolean[]) u[i]);
			}
			for (int i = 0; i < tv.length; ++i) {
				tv[i] = env.inputOfBob((boolean[]) v[i]);
			}
			tIsVertex = env.inputOfBob(isVertex);
			for (int i = 0; i < trating.length; i++) {
				trating[i] = env.inputOfBob((boolean[]) rating[i]);
			}
			for(int i = 0; i < tUserProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tUserProfile[i][j] = env.inputOfBob((boolean[]) userProfile[i][j]);
				}
			}
			for(int i = 0; i < tItemProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tItemProfile[i][j] = env.inputOfBob((boolean[]) itemProfile[i][j]);
				}
			}
			tIsU = env.inputOfBob(isU);
			tIsV = env.inputOfBob(isV);
		}
		Object[] inputU = new Object[machines];
		Object[] inputV = new Object[machines];
		Object[] inputIsVertex = new Object[machines];
		Object[] inputRating = new Object[machines];
		Object[] inputUserProfile = new Object[machines];
		Object[] inputItemProfile = new Object[machines];
		Object[] inputIsU = new Object[machines];
		Object[] inputIsV = new Object[machines];

		for(int i = 0; i < machines; ++i) {
			inputU[i] = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			inputV[i] = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
			inputIsVertex[i] = Arrays.copyOfRange(tIsVertex, i * tIsVertex.length / machines, (i + 1) * tIsVertex.length / machines);
			inputRating[i] = Arrays.copyOfRange(trating, i * trating.length / machines, (i + 1) * trating.length / machines);
			inputUserProfile[i] = Arrays.copyOfRange(tUserProfile, i * tUserProfile.length / machines, (i + 1) * tUserProfile.length / machines);
			inputItemProfile[i] = Arrays.copyOfRange(tItemProfile, i * tItemProfile.length / machines, (i + 1) * tItemProfile.length / machines);
			inputIsU[i] = Arrays.copyOfRange(tIsU, i * tIsU.length / machines, (i + 1) * tIsU.length / machines);
			inputIsV[i] = Arrays.copyOfRange(tIsV, i * tIsV.length / machines, (i + 1) * tIsV.length / machines);
		}
		Object[] input = new Object[8];
		input[0] = inputU;
		input[1] = inputV;
		input[2] = inputIsVertex;
		input[3] = inputRating;
		input[4] = inputUserProfile;
		input[5] = inputItemProfile;
		input[6] = inputIsU;
		input[7] = inputIsV;
		return input;
	}

	@Override
	public void sendInputToMachines(int inputLength, int machines,
			boolean isGen, CompEnv<T> env, OutputStream[] os)
			throws IOException, IncorrectOtUsageException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		Object[] inputU = (Object[]) input[0];
		Object[] inputV = (Object[]) input[1];
		Object[] inputIsVertex = (Object[]) input[2];
		Object[] inputRating = (Object[]) input[3];
		Object[] inputUserProfile = (Object[]) input[4];
		Object[] inputItemProfile = (Object[]) input[5];
		Object[] inputIsU = (Object[]) input[6];
		Object[] inputIsV = (Object[]) input[7];
		for (int i = 0; i < machines; i++) {
			T[][] gcInputU = (T[][]) inputU[i];
			T[][] gcInputV = (T[][]) inputV[i];
			T[] gcInputIsVertex = (T[]) inputIsVertex[i];
			T[][] gcInputRating = (T[][]) inputRating[i];
			T[][][] gcInputUserProfile = (T[][][]) inputUserProfile[i];
			T[][][] gcInputItemProfile = (T[][][]) inputItemProfile[i];
			T[] gcInputIsU = (T[]) inputIsU[i];
			T[] gcInputIsV = (T[]) inputIsV[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
			NetworkUtil.send(os[i], gcInputU, env);
			NetworkUtil.send(os[i], gcInputV, env);
			NetworkUtil.send(os[i], gcInputIsVertex, env);
			NetworkUtil.send(os[i], gcInputRating, env);
			NetworkUtil.send(os[i], gcInputIsU, env);
			NetworkUtil.send(os[i], gcInputIsV, env);
			for (int j = 0; j < gcInputUserProfile.length; j++) {
				NetworkUtil.send(os[i], gcInputUserProfile[j], env);
			}
			for (int j = 0; j < gcInputItemProfile.length; j++) {
				NetworkUtil.send(os[i], gcInputItemProfile[j], env);
			}
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, InputStream masterIs,
			CompEnv<T> env) throws IOException {
		T[][] gcInputU = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[][] gcInputV = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[] gcInputIsVertex = NetworkUtil.read(masterIs, inputLength, env);
		T[][] gcInputRating = NetworkUtil.read(masterIs, inputLength, AlsNode.FIX_POINT_WIDTH, env);
		T[][][] gcUserProfile = env.newTArray(inputLength, D, 0);
		T[][][] gcItemProfile = env.newTArray(inputLength, D, 0);
		T[] gcInputIsU = NetworkUtil.read(masterIs, inputLength, env);
		T[] gcInputIsV = NetworkUtil.read(masterIs, inputLength, env);
		for (int j = 0; j < inputLength; j++) {
			gcUserProfile[j] = NetworkUtil.read(masterIs, D, AlsNode.FIX_POINT_WIDTH, env);
		}
		for (int j = 0; j < inputLength; j++) {
			gcItemProfile[j] = NetworkUtil.read(masterIs, D, AlsNode.FIX_POINT_WIDTH, env);
		}
		Object[] ret = new Object[8];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
		ret[2] = gcInputIsVertex;
		ret[3] = gcInputRating;
		ret[4] = gcUserProfile;
		ret[5] = gcItemProfile;
		ret[6] = gcInputIsU;
		ret[7] = gcInputIsV;
	    return ret;
	}

	@Override
	public <T> void compute(int machineId, Machine machine, CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[] isVertex = (T[]) ((Object[]) machine.input)[2];
		T[][] rating = (T[][]) ((Object[]) machine.input)[3];
		T[][][] userProfile = (T[][][]) ((Object[]) machine.input)[4];
		T[][][] itemProfile = (T[][][]) ((Object[]) machine.input)[5];
		T[] isU = (T[]) ((Object[]) machine.input)[6];
		T[] isV = (T[]) ((Object[]) machine.input)[7];
		final IntegerLib<T> lib = new IntegerLib<>(env);
		final FixedPointLib<T> flib = new FixedPointLib<>(env, AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET);

		AlsNode<T>[] aa = (AlsNode<T>[]) Array.newInstance(AlsNode.class, u.length);
		for (int i = 0; i < aa.length; i++) {
			aa[i] = new AlsNode<T>(u[i], v[i], isVertex[i], rating[i], userProfile[i], itemProfile[i], isU[i], isV[i], env);
		}

//		print(machineId, env, aa);
		new ScatterToEdges<T>(env, machine, false /* isEdgeIncoming */) {

			@Override
			public void writeToEdge(GraphNode<T> vertexNode,
					GraphNode<T> edgeNode, T isVertex) {
				AlsNode<T> vertex = (AlsNode<T>) vertexNode;
				AlsNode<T> edge = (AlsNode<T>) edgeNode;
				edge.up = lib.mux(vertex.up, edge.up, isVertex);
			}
		}.setInputs(aa).compute();

		for (int it = 0; it < ITERATIONS; it++) {
			new ScatterToEdges<T>(env, machine, true /* isEdgeIncoming */) {

				@Override
				public void writeToEdge(GraphNode<T> vertexNode,
						GraphNode<T> edgeNode, T isVertex) {
					AlsNode<T> vertex = (AlsNode<T>) vertexNode;
					AlsNode<T> edge = (AlsNode<T>) edgeNode;
					edge.vp = lib.mux(vertex.vp, edge.vp, isVertex);
				}
			}.setInputs(aa).compute();

			// compute values for up assuming vp; edge values
			for (int i = 0; i < aa.length; i++) {
				aa[i].solveU(env);
			}

			// gather values for user profiles
			new GatherFromEdges<T>(env, machine, false /* isEdgeIncoming */, new AlsNode<>(env)) {

				@Override
				public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode)
						throws IOException {
					AlsNode<T> agg = (AlsNode<T>) aggNode;
					AlsNode<T> b = (AlsNode<T>) bNode;
					AlsNode<T> ret = new AlsNode<>(env);
					for (int i = 0; i < D; i++) {
						ret.vp[i] = flib.add(agg.vp[i], b.vp[i]);
						for (int j = 0; j < D; j++) {
							ret.M[i][j] = flib.add(agg.M[i][j], b.M[i][j]);
						}
					}
					return ret;
				}

				@Override
				public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
					AlsNode<T> agg = (AlsNode<T>) aggNode;
					AlsNode<T> vertex = (AlsNode<T>) bNode;
					vertex.vp = lib.mux(vertex.vp, agg.vp, vertex.isU);
					vertex.M = lib.mux(vertex.M, agg.M, vertex.isU);
					for (int i = 0; i < D; i++) {
						vertex.M[i][i] = flib.add(vertex.M[i][i], flib.publicValue(MU));
					}
				}
			}.setInputs(aa).compute();

			// compute P-1 A
			for (int i = 0; i < aa.length; i++) {
				T[][][] rref = getRowReducedMatrix(env, aa, i, true /* isItem */, flib);
				for (int j = 0; j < D; j++) {
					aa[i].up[j] = lib.mux(aa[i].up[j], rref[j][D], aa[i].isU);
				}
			}

			// scatter user profiles
			new ScatterToEdges<T>(env, machine, false /* isEdgeIncoming */) {

				@Override
				public void writeToEdge(GraphNode<T> vertexNode,
						GraphNode<T> edgeNode, T isVertex) {
					AlsNode<T> vertex = (AlsNode<T>) vertexNode;
					AlsNode<T> edge = (AlsNode<T>) edgeNode;
					edge.up = lib.mux(vertex.up, edge.up, isVertex);
				}
			}.setInputs(aa).compute();
			
			// compute values for vp assuming up; edge values
			for (int i = 0; i < aa.length; i++) {
				aa[i].solveV(env);
			}

			// gather values for item profiles
			new GatherFromEdges<T>(env, machine, true /* isEdgeIncoming */, new AlsNode<>(env)) {

				@Override
				public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode)
						throws IOException {
					AlsNode<T> agg = (AlsNode<T>) aggNode;
					AlsNode<T> b = (AlsNode<T>) bNode;
					AlsNode<T> ret = new AlsNode<>(env);
					for (int i = 0; i < D; i++) {
						ret.up[i] = flib.add(agg.up[i], b.up[i]);
						for (int j = 0; j < D; j++) {
							ret.M[i][j] = flib.add(agg.M[i][j], b.M[i][j]);
						}
					}
					return ret;
				}

				@Override
				public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
					AlsNode<T> agg = (AlsNode<T>) aggNode;
					AlsNode<T> vertex = (AlsNode<T>) bNode;
					vertex.up = lib.mux(vertex.up, agg.up, vertex.isV);
					vertex.M = lib.mux(vertex.M, agg.M, vertex.isV);
					for (int i = 0; i < D; i++) {
						vertex.M[i][i] = flib.add(vertex.M[i][i], flib.publicValue(MU));
					}
				}
			}.setInputs(aa).compute();

			// compute P-1 A for items
			for (int i = 0; i < aa.length; i++) {
				T[][][] rref = getRowReducedMatrix(env, aa, i, false /* isItem */, flib);
				for (int j = 0; j < D; j++) {
					aa[i].vp[j] = lib.mux(aa[i].vp[j], rref[j][D], aa[i].isV);
				}
			}
		}
		new SortGadget<>(env, machine).setInputs(aa, GraphNode.vertexFirstComparator(env)).compute();
		print(machineId, env, aa);
	}

	private <T> T[][][] getRowReducedMatrix(CompEnv<T> env, AlsNode<T>[] aa, int i, boolean isItem, ArithmeticLib<T> flib) {
		T[][][] inp = env.newTArray(D, D + 1, AlsNode.FIX_POINT_WIDTH);
		for (int j = 0; j < D; j++) {
			for (int k = 0; k < D; k++) {
				inp[j][k] = aa[i].M[j][k];
			}
			if (isItem) {
				inp[j][D] = aa[i].vp[j];
			} else {
				inp[j][D] = aa[i].up[j];
			}
		}
		DenseMatrixLib<T> dmLib = new DenseMatrixLib<>(env, flib);
		T[][][] rref = dmLib.rref(inp);
		return rref;
	}

	private <T> void print(int machineId, final CompEnv<T> env, AlsNode<T>[] alsNode) throws IOException, BadLabelException, InterruptedException {
		FixedPointLib<T> lib = new FixedPointLib<>(env, AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET);
		if (machineId == 1) {
			Thread.sleep(1000);
		} else {
			System.out.println();
		}
		for (int i = 0; i < alsNode.length; i++) {
			int a = Utils.toInt(env.outputToAlice(alsNode[i].u));
			int b = Utils.toInt(env.outputToAlice(alsNode[i].v));
			double r = lib.outputToAlice(alsNode[i].rating);
			double c2 = lib.outputToAlice(alsNode[i].up[0]);
			double c3 = lib.outputToAlice(alsNode[i].up[1]);
			double d = lib.outputToAlice(alsNode[i].vp[0]);
			double d2 = lib.outputToAlice(alsNode[i].vp[1]);
			boolean isU = env.outputToAlice(alsNode[i].isU);
			boolean isV = env.outputToAlice(alsNode[i].isV);
			env.os.flush();
			if (Party.Alice.equals(env.party)) {
				System.out.format("%d: %d, %d \t %.2f \t %.4f \t %.4f \t %.4f \t %.4f \t %b \t %b\n", machineId, a, b, r, c2, c3, d, d2, isU, isV);
//				System.out.println(machineId + ": " + a + ", " + b + "\t" + r + "\t" +  c2 + "\t" + d + "\t" + e);
			}
	    }
	}
}

