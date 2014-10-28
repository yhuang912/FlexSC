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
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import flexsc.PMCompEnv.Statistics;
import gc.BadLabelException;

public class Als<T> implements ParallelGadget<T> {

	public static int ITERATIONS = 1;
	public static double GAMMA = 0.0002;
	public static double MU = 0.02;
	public static int USERS = 0;
	public static int ITEMS = 0;
	public static final int D = 2;

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
			Machine.RAND = new double[MatrixFactorization.RAND_LIM];
			BufferedReader reader = new BufferedReader(new FileReader("rand.out"));
			for (int i2 = 0; i2 < MatrixFactorization.RAND_LIM; i2++) {
				Machine.RAND[i2] = Double.parseDouble(reader.readLine());
			}
			reader.close();
			int[] u1 = new int[inputLength];
			int[] v1 = new int[inputLength];
			boolean[] isVertex1 = new boolean[inputLength];
			double[] rating1 = new double[inputLength];
			double[][] userProfile1 = new double[inputLength][D];
			double[][] itemProfile1 = new double[inputLength][D];
			boolean[] isU1 = new boolean[inputLength];
			boolean[] isV1 = new boolean[inputLength];
			BufferedReader br = new BufferedReader(new FileReader("in/als" + inputLength + ".in"));
			USERS = Integer.parseInt(br.readLine());
			ITEMS = Integer.parseInt(br.readLine());
			for (int i1 = 0; i1 < inputLength; i1++) {
				String readLine = br.readLine();
				String[] split = readLine.split(" ");
				u1[i1] = Integer.parseInt(split[0]);
				v1[i1] = Integer.parseInt(split[1]);
				isVertex1[i1] = (Integer.parseInt(split[2]) == 1);
				rating1[i1] = Double.parseDouble(split[3]);
				for (int j1 = 0; j1 < D; j1++) {
					userProfile1[i1][j1] = MatrixFactorization.getRandom();
				}
				for (int j3 = 0; j3 < D; j3++) {
					itemProfile1[i1][j3] = MatrixFactorization.getRandom();
				}
				if (i1 < USERS) {
					isU1[i1] = true;
					isV1[i1] = false;
				} else if (i1 < USERS + ITEMS) {
					isU1[i1] = false;
					isV1[i1] = true;
				} else {
					isU1[i1] = false;
					isV1[i1] = false;
				}
			}
			br.close();
			for (int i = 0; i < tu.length; ++i) {
				tu[i] = env.inputOfBob(Utils.fromInt(u1[i], GraphNode.VERTEX_LEN));
			}
			for (int i = 0; i < tv.length; ++i) {
				tv[i] = env.inputOfBob(Utils.fromInt(v1[i], GraphNode.VERTEX_LEN));
			}
			tIsVertex = env.inputOfBob(isVertex1);
			for (int i = 0; i < trating.length; i++) {
				trating[i] = env.inputOfBob(Utils.fromFixPoint(rating1[i], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
			}
			for(int i = 0; i < tUserProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tUserProfile[i][j] = env.inputOfBob(Utils.fromFixPoint(userProfile1[i][j], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
				}
			}
			for(int i = 0; i < tItemProfile.length; ++i) {
				for (int j = 0; j < D; j++) {
					tItemProfile[i][j] = env.inputOfBob(Utils.fromFixPoint(itemProfile1[i][j], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
				}
			}
			tIsU = env.inputOfBob(isU1);
			tIsV = env.inputOfBob(isV1);
		}
		Object[][] input = new Object[8][machines];
		for(int i = 0; i < machines; ++i) {
			input[0][i] = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			input[1][i] = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
			input[2][i] = Arrays.copyOfRange(tIsVertex, i * tIsVertex.length / machines, (i + 1) * tIsVertex.length / machines);
			input[3][i] = Arrays.copyOfRange(trating, i * trating.length / machines, (i + 1) * trating.length / machines);
			input[4][i] = Arrays.copyOfRange(tUserProfile, i * tUserProfile.length / machines, (i + 1) * tUserProfile.length / machines);
			input[5][i] = Arrays.copyOfRange(tItemProfile, i * tItemProfile.length / machines, (i + 1) * tItemProfile.length / machines);
			input[6][i] = Arrays.copyOfRange(tIsU, i * tIsU.length / machines, (i + 1) * tIsU.length / machines);
			input[7][i] = Arrays.copyOfRange(tIsV, i * tIsV.length / machines, (i + 1) * tIsV.length / machines);
		}
		return input;
	}

	@Override
	public void sendInputToMachines(int inputLength, int machines,
			boolean isGen, CompEnv<T> env, OutputStream[] os)
			throws IOException, IncorrectOtUsageException {
		T[][] tu = env.newTArray(inputLength, 0);
		T[][] tv = env.newTArray(inputLength, 0);
		T[] tIsVertex = env.newTArray(inputLength);
		T[][] trating = env.newTArray(inputLength, 0);
		T[][][] tUserProfile = env.newTArray(inputLength, D, 0);
		T[][][] tItemProfile = env.newTArray(inputLength, D, 0);
		T[] tIsU = env.newTArray(inputLength);
		T[] tIsV = env.newTArray(inputLength);
		if (isGen) {
			for(int i2 = 0; i2 < tu.length; ++i2)
				tu[i2] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			for(int i3 = 0; i3 < tv.length; ++i3)
				tv[i3] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			tIsVertex = env.inputOfBob(new boolean[tIsVertex.length]);
			for(int i8 = 0; i8 < trating.length; ++i8)
				trating[i8] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
			for(int i9 = 0; i9 < tUserProfile.length; ++i9) {
				for (int j3 = 0; j3 < D; j3++) {
					tUserProfile[i9][j3] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
				}
			}
			for(int i1 = 0; i1 < tItemProfile.length; ++i1) {
				for (int j2 = 0; j2 < D; j2++) {
					tItemProfile[i1][j2] = env.inputOfBob(new boolean[AlsNode.FIX_POINT_WIDTH]);
				}
			}
			tIsU = env.inputOfBob(new boolean[tIsVertex.length]);
			tIsV = env.inputOfBob(new boolean[tIsVertex.length]);
		} else {
			Machine.RAND = new double[MatrixFactorization.RAND_LIM];
			BufferedReader reader = new BufferedReader(new FileReader("rand.out"));
			for (int i21 = 0; i21 < MatrixFactorization.RAND_LIM; i21++) {
				Machine.RAND[i21] = Double.parseDouble(reader.readLine());
			}
			reader.close();
			int[] u1 = new int[inputLength];
			int[] v1 = new int[inputLength];
			boolean[] isVertex1 = new boolean[inputLength];
			double[] rating1 = new double[inputLength];
			double[][] userProfile1 = new double[inputLength][D];
			double[][] itemProfile1 = new double[inputLength][D];
			boolean[] isU1 = new boolean[inputLength];
			boolean[] isV1 = new boolean[inputLength];
			BufferedReader br = new BufferedReader(new FileReader("in/als" + inputLength + ".in"));
			USERS = Integer.parseInt(br.readLine());
			ITEMS = Integer.parseInt(br.readLine());
			for (int i11 = 0; i11 < inputLength; i11++) {
				String readLine = br.readLine();
				String[] split = readLine.split(" ");
				u1[i11] = Integer.parseInt(split[0]);
				v1[i11] = Integer.parseInt(split[1]);
				isVertex1[i11] = (Integer.parseInt(split[2]) == 1);
				rating1[i11] = Double.parseDouble(split[3]);
				for (int j11 = 0; j11 < D; j11++) {
					userProfile1[i11][j11] = MatrixFactorization.getRandom();
				}
				for (int j31 = 0; j31 < D; j31++) {
					itemProfile1[i11][j31] = MatrixFactorization.getRandom();
				}
				if (i11 < USERS) {
					isU1[i11] = true;
					isV1[i11] = false;
				} else if (i11 < USERS + ITEMS) {
					isU1[i11] = false;
					isV1[i11] = true;
				} else {
					isU1[i11] = false;
					isV1[i11] = false;
				}
			}
			br.close();
			for (int i6 = 0; i6 < tu.length; ++i6) {
				tu[i6] = env.inputOfBob(Utils.fromInt(u1[i6], GraphNode.VERTEX_LEN));
			}
			for (int i7 = 0; i7 < tv.length; ++i7) {
				tv[i7] = env.inputOfBob(Utils.fromInt(v1[i7], GraphNode.VERTEX_LEN));
			}
			tIsVertex = env.inputOfBob(isVertex1);
			for (int i12 = 0; i12 < trating.length; i12++) {
				trating[i12] = env.inputOfBob(Utils.fromFixPoint(rating1[i12], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
			}
			for(int i = 0; i < tUserProfile.length; ++i) {
				for (int j1 = 0; j1 < D; j1++) {
					tUserProfile[i][j1] = env.inputOfBob(Utils.fromFixPoint(userProfile1[i][j1], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
				}
			}
			for(int i = 0; i < tItemProfile.length; ++i) {
				for (int j4 = 0; j4 < D; j4++) {
					tItemProfile[i][j4] = env.inputOfBob(Utils.fromFixPoint(itemProfile1[i][j4], AlsNode.FIX_POINT_WIDTH, AlsNode.OFFSET));
				}
			}
			tIsU = env.inputOfBob(isU1);
			tIsV = env.inputOfBob(isV1);
		}
		for(int i = 0; i < machines; ++i) {
			T[][] gcInputU = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			T[][] gcInputV = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
			T[] gcInputIsVertex = Arrays.copyOfRange(tIsVertex, i * tIsVertex.length / machines, (i + 1) * tIsVertex.length / machines);
			T[][] gcInputRating = Arrays.copyOfRange(trating, i * trating.length / machines, (i + 1) * trating.length / machines);
			T[][][] gcInputUserProfile = Arrays.copyOfRange(tUserProfile, i * tUserProfile.length / machines, (i + 1) * tUserProfile.length / machines);
			T[][][] gcInputItemProfile = Arrays.copyOfRange(tItemProfile, i * tItemProfile.length / machines, (i + 1) * tItemProfile.length / machines);
			T[] gcInputIsU = Arrays.copyOfRange(tIsU, i * tIsU.length / machines, (i + 1) * tIsU.length / machines);
			T[] gcInputIsV = Arrays.copyOfRange(tIsV, i * tIsV.length / machines, (i + 1) * tIsV.length / machines);
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
		long startTime = System.nanoTime();
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
		long endTime = System.nanoTime();
		if (Mode.REAL.equals(env.getMode())) {
			System.out.println(machineId + "," + machine.totalMachines + ","  + machine.inputLength + "," + (endTime - startTime)/1000000000.0 + "," + "Total time" + "," + env.getParty().name());
		} else if (Mode.COUNT.equals(env.mode)) {
			Statistics a = ((PMCompEnv) env).statistic;
			a.finalize();
			Thread.sleep(1000 * machineId);
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + a.andGate + "," + a.NumEncAlice);
		}
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

