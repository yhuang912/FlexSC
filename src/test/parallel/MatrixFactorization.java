package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import ot.IncorrectOtUsageException;
import test.Utils;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import circuits.IntegerLib;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;
import flexsc.Party;
import gc.BadLabelException;

public class MatrixFactorization<T> implements ParallelGadget<T> {

	public static int ITERATIONS = 10;
	public static double GAMMA = 0.0001;
	public static double LAMBDA = 0.001; 
	public static double MU = 0.001;

	private Object[] getInput(int inputLength) throws IOException {
		int[] u = new int[inputLength];
		int[] v = new int[inputLength];
		boolean[] isVertex = new boolean[inputLength];
		double[] rating = new double[inputLength];
		BufferedReader br = new BufferedReader(new FileReader("mf.in"));
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			u[i] = Integer.parseInt(split[0]);
			v[i] = Integer.parseInt(split[1]);
			isVertex[i] = (Integer.parseInt(split[2]) == 1);
			rating[i] = Double.parseDouble(split[3]);
		}
		br.close();
		boolean[][] ua = new boolean[u.length][];
		boolean[][] va = new boolean[v.length][];
		boolean[] isVertexa = new boolean[isVertex.length];
		boolean[][] ratinga = new boolean[rating.length][];
		for (int i = 0; i < u.length; i++) {
			ua[i] = Utils.fromInt(u[i], GraphNode.VERTEX_LEN);
			va[i] = Utils.fromInt(v[i], GraphNode.VERTEX_LEN);
			isVertexa[i] = isVertex[i];
			ratinga[i] = Utils.fromFixPoint(rating[i], MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
		}
		Object[] ret = new Object[4];
		ret[0] = ua;
		ret[1] = va;
		ret[2] = isVertexa;
		ret[3] = ratinga;
		return ret;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException, IncorrectOtUsageException {
		T[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[] tIsV = env.newTArray(inputLength /* number of entries in the input */);
		T[][] trating = env.newTArray(inputLength /* number of entries in the input */, 0);
		if (isGen) {
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			tIsV = env.inputOfBob(new boolean[tIsV.length]);
			for(int i = 0; i < trating.length; ++i)
				trating[i] = env.inputOfBob(new boolean[MFNode.FIX_POINT_WIDTH]);
		} else {
			Object[] input = getInput(inputLength);
			boolean[][] u = (boolean[][]) input[0];
			boolean[][] v = (boolean[][]) input[1];
			boolean[] isV = (boolean[]) input[2];
			boolean[][] rating = (boolean[][]) input[3];
			for (int i = 0; i < tu.length; ++i) {
				tu[i] = env.inputOfBob((boolean[]) u[i]);
			}
			for (int i = 0; i < tv.length; ++i) {
				tv[i] = env.inputOfBob((boolean[]) v[i]);
			}
			tIsV = env.inputOfBob(isV);
			for (int i = 0; i < trating.length; i++) {
				trating[i] = env.inputOfBob((boolean[]) rating[i]);
			}
		}
		Object[] inputU = new Object[machines];
		Object[] inputV = new Object[machines];
		Object[] inputIsVertex = new Object[machines];
		Object[] inputRating = new Object[machines];

		for(int i = 0; i < machines; ++i) {
			inputU[i] = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			inputV[i] = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
			inputIsVertex[i] = Arrays.copyOfRange(tIsV, i * tIsV.length / machines, (i + 1) * tIsV.length / machines);
			inputRating[i] = Arrays.copyOfRange(trating, i * trating.length / machines, (i + 1) * trating.length / machines);
		}
		Object[] input = new Object[4];
		input[0] = inputU;
		input[1] = inputV;
		input[2] = inputIsVertex;
		input[3] = inputRating;
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
		for (int i = 0; i < machines; i++) {
			T[][] gcInputU = (T[][]) inputU[i];
			T[][] gcInputV = (T[][]) inputV[i];
			T[] gcInputIsVertex = (T[]) inputIsVertex[i];
			T[][] gcInputRating = (T[][]) inputRating[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
			NetworkUtil.send(os[i], gcInputU, env);
			NetworkUtil.send(os[i], gcInputV, env);
			NetworkUtil.send(os[i], gcInputIsVertex, env);
			NetworkUtil.send(os[i], gcInputRating, env);
			os[i].flush();
		}
//		System.out.println(((T[][]) inputRating[0])[0].length);
	}

	@Override
	public Object readInputFromMaster(int inputLength,
			InputStream masterIs,
			CompEnv<T> env) throws IOException {
		T[][] gcInputU = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[][] gcInputV = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[] gcInputIsVertex = NetworkUtil.read(masterIs, inputLength, env);
		T[][] gcInputRating = NetworkUtil.read(masterIs, inputLength, MFNode.FIX_POINT_WIDTH, env);
		Object[] ret = new Object[4];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
		ret[2] = gcInputIsVertex;
		ret[3] = gcInputRating;
	    return ret;
	}

	@Override
	public <T> void compute(int machineId, Machine machine, CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[] isVertex = (T[]) ((Object[]) machine.input)[2];
		T[][] rating = (T[][]) ((Object[]) machine.input)[3];

		MFNode<T>[] aa = (MFNode<T>[]) Array.newInstance(MFNode.class, u.length);
		T[] temp = env.inputOfAlice(Utils.fromFixPoint(1, MFNode.FIX_POINT_WIDTH, MFNode.OFFSET));
		for (int i = 0; i < aa.length; i++) {
			aa[i] = new MFNode<T>(u[i], v[i], isVertex[i], rating[i], env);
		}

//		print(machineId, env, aa);
		for (int it = 0; it < ITERATIONS; it++) {
			// scatter user profiles
			new ScatterToEdges<T>(env, machine, false /* isEdgeIncoming */) {
	
				@Override
				public void writeToEdge(GraphNode<T> vertexNode,
						GraphNode<T> edgeNode, T isVertex) {
					MFNode<T> vertex = (MFNode<T>) vertexNode;
					MFNode<T> edge = (MFNode<T>) edgeNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					edge.userProfile = lib.mux(vertex.userProfile, edge.userProfile, isVertex);
				}
			}.setInputs(aa).compute();

			// scatter item profiles
			new ScatterToEdges<T>(env, machine, true /* isEdgeIncoming */) {
	
				@Override
				public void writeToEdge(GraphNode<T> vertexNode,
						GraphNode<T> edgeNode, T isVertex) {
					MFNode<T> vertex = (MFNode<T>) vertexNode;
					MFNode<T> edge = (MFNode<T>) edgeNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					edge.itemProfile = lib.mux(vertex.itemProfile, edge.itemProfile, isVertex);
				}
			}.setInputs(aa).compute();

			// compute gradient
			new ComputeGradient<T>(env, machine)
				.setInputs(aa)
				.compute();

//			printResult(machineId, env, aa);
			// update item profiles
			new GatherFromEdges<T>(env, machine, true /* isEdgeIncoming */, new MFNode<>(env, true /* identity */)) {
	
				@Override
				public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
					MFNode<T> agg = (MFNode<T>) aggNode;
					MFNode<T> b = (MFNode<T>) bNode;
					FixedPointLib<T> fixedPointLib = new FixedPointLib<T>(env,
							MFNode.FIX_POINT_WIDTH,
							MFNode.OFFSET);
					MFNode<T> ret = new MFNode<>(env);
					for (int i = 0; i < ret.itemProfile.length; i++) {
						ret.itemProfile[i] = fixedPointLib.add(agg.itemProfile[i], b.itemProfile[i]);
//						ret.userProfile[i] = b.userProfile[i];
					}
					return ret;
				}

				@Override
				public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
					MFNode<T> agg = (MFNode<T>) aggNode;
					MFNode<T> b = (MFNode<T>) bNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					FixedPointLib<T> flib = new FixedPointLib<>(env,
							MFNode.FIX_POINT_WIDTH,
							MFNode.OFFSET);
					for (int i = 0; i < agg.itemProfile.length; i++) {
						T[] edgeNodeAgg = flib.add(agg.itemProfile[i], b.itemProfile[i]);
						T[] twoGammaLambda = flib.publicValue(2 * MatrixFactorization.GAMMA * MatrixFactorization.MU);
						T[] reg = flib.multiply(twoGammaLambda, b.itemProfile[i]);
						T[] total = flib.add(edgeNodeAgg, reg);
						// add 2 gamma lambta s6,k to agg
						b.itemProfile[i] = lib.mux(b.itemProfile[i], total, b.isVertex);
					}
				}
			}.setInputs(aa).compute();

			// update user profiles
			new GatherFromEdges<T>(env, machine, false /* isEdgeIncoming */, new MFNode<>(env, true /* identity */)) {
	
				@Override
				public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
					MFNode<T> agg = (MFNode<T>) aggNode;
					MFNode<T> b = (MFNode<T>) bNode;
					FixedPointLib<T> fixedPointLib = new FixedPointLib<T>(env,
							MFNode.FIX_POINT_WIDTH,
							MFNode.OFFSET);
					MFNode<T> ret = new MFNode<>(env);
					for (int i = 0; i < ret.userProfile.length; i++) {
						ret.userProfile[i] = fixedPointLib.add(agg.userProfile[i], b.userProfile[i]);
					}
					return ret;
				}
	
				@Override
				public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
					MFNode<T> agg = (MFNode<T>) aggNode;
					MFNode<T> b = (MFNode<T>) bNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					FixedPointLib<T> flib = new FixedPointLib<>(env,
							MFNode.FIX_POINT_WIDTH,
							MFNode.OFFSET);
					for (int i = 0; i < agg.userProfile.length; i++) {
						T[] edgeNodeAgg = flib.add(agg.userProfile[i], b.userProfile[i]);
						T[] twoGammaLambda = flib.publicValue(2 * MatrixFactorization.GAMMA * MatrixFactorization.LAMBDA);
						T[] reg = flib.multiply(twoGammaLambda, b.userProfile[i]);
						T[] total = flib.add(edgeNodeAgg, reg);
						b.userProfile[i] = lib.mux(b.userProfile[i], total, b.isVertex);
					}
				}
			}.setInputs(aa).compute();
		}

		new SortGadget<>(env, machine)
			.setInputs(aa, GraphNode.vertexFirstComparator(env))
			.compute();
		printOnlyResult(machineId, env, aa);
	}

	private <T> void printResult(int machineId, final CompEnv<T> env, MFNode<T>[] mfNode) throws IOException, BadLabelException {
		FixedPointLib<T> lib = new FixedPointLib<>(env, MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
		for (int i = 0; i < mfNode.length; i++) {
			int a = Utils.toInt(env.outputToAlice(mfNode[i].u));
			int b = Utils.toInt(env.outputToAlice(mfNode[i].v));
			double r = lib.outputToAlice(mfNode[i].rating);
			//Utils.toFloat(env.outputToAlice(mfNode[i].userProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
//			double d = lib.outputToAlice(mfNode[i].itemProfile[0]);//Utils.toFloat(env.outputToAlice(mfNode[i].itemProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
			boolean e = env.outputToAlice(mfNode[i].isVertex);
			env.os.flush();
			double c2[] = new double[MFNode.D];
			double d[] = new double[MFNode.D];
			for (int j = 0; j < MFNode.D; j++) {
				c2[j] = lib.outputToAlice(mfNode[i].userProfile[j]);
				d[j] = lib.outputToAlice(mfNode[i].itemProfile[j]);
			}
			if (Party.Alice.equals(env.party)) {
//				System.out.println(machineId + ": " + a + ", " + b + "\t" + r + "\t" +  c2 + "\t" + d + "\t" + e);
				System.out.print(machineId + ": " + a + ", " + b + "\t" + r + "\t");
				for (int j = 0; j < MFNode.D; j++) {
					System.out.print(c2[j] + "\t");
				}
				for (int j = 0; j < MFNode.D; j++) {
					System.out.print(d[j] + "\t");
				}
				System.out.println();
			}
	    }
	}

	private <T> void printOnlyResult(int machineId, final CompEnv<T> env, MFNode<T>[] mfNode) throws IOException, BadLabelException {
		FixedPointLib<T> lib = new FixedPointLib<>(env, MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
		for (int i = 0; i < mfNode.length; i++) {
			int a = Utils.toInt(env.outputToAlice(mfNode[i].u));
			int b = Utils.toInt(env.outputToAlice(mfNode[i].v));
			double r = lib.outputToAlice(mfNode[i].rating);
			//Utils.toFloat(env.outputToAlice(mfNode[i].userProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
//			double d = lib.outputToAlice(mfNode[i].itemProfile[0]);//Utils.toFloat(env.outputToAlice(mfNode[i].itemProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
			boolean e = env.outputToAlice(mfNode[i].isVertex);
			env.os.flush();
			double c2[] = new double[MFNode.D];
			double d[] = new double[MFNode.D];
			for (int j = 0; j < MFNode.D; j++) {
				c2[j] = lib.outputToAlice(mfNode[i].userProfile[j]);
				d[j] = lib.outputToAlice(mfNode[i].itemProfile[j]);
			}
			if (Party.Alice.equals(env.party) && e) {
//				System.out.println(machineId + ": " + a + ", " + b + "\t" + r + "\t" +  c2 + "\t" + d + "\t" + e);
				System.out.print(machineId + ": " + a + ", " + b + "\t" + r + "\t");
				if (i < 4) {
					for (int j = 0; j < MFNode.D; j++) {
						System.out.print(c2[j] + "\t");
					}
				} else {
					for (int j = 0; j < MFNode.D; j++) {
						System.out.print(d[j] + "\t");
					}
				}
				System.out.println();
			}
	    }
	}

	private <T> void print(int machineId, final CompEnv<T> env, MFNode<T>[] mfNode) throws IOException, BadLabelException {
		FixedPointLib<T> lib = new FixedPointLib<>(env, MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
		for (int i = 0; i < mfNode.length; i++) {
			int a = Utils.toInt(env.outputToAlice(mfNode[i].u));
			int b = Utils.toInt(env.outputToAlice(mfNode[i].v));
			double r = lib.outputToAlice(mfNode[i].rating);
			double c2 = lib.outputToAlice(mfNode[i].userProfile[0]);//Utils.toFloat(env.outputToAlice(mfNode[i].userProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
			double d = lib.outputToAlice(mfNode[i].itemProfile[0]);//Utils.toFloat(env.outputToAlice(mfNode[i].itemProfile[0]), MFNode.FIX_POINT_WIDTH, MFNode.OFFSET);
			boolean e = env.outputToAlice(mfNode[i].isVertex);
			env.os.flush();
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + a + ", " + b + "\t" + r + "\t" +  c2 + "\t" + d + "\t" + e);
			}
	    }
	}
}