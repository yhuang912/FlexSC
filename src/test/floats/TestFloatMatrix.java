package test.floats;

import java.util.Random;

import objects.Float.Represention;
import gc.GCEva;
import gc.GCGen;

import org.junit.Test;

import circuits.FloatMatrixLib;



public class TestFloatMatrix {
	class GenRunnable extends network.Server implements Runnable {
		double[][] f;
		int lengthv, lengthp;
		
		GenRunnable (double[][] f, int lengthv, int lengthp) {
			this.f = f;
			this.lengthv = lengthv;
			this.lengthp = lengthp;
		}
		
		public void run() {
			try {
				listen(54321);
				GCGen gen = new GCGen(is, os);
				
				Represention[][] m = new Represention[f.length][f[0].length];
				
				for(int i = 0; i < f.length; ++i){
					for(int j = 0; j < f[0].length; ++j)
						if(f[i][j] != 9999999)
							m[i][j] = gen.inputOfGen(f[i][j], lengthv, lengthp);
						else
							m[i][j] = gen.inputOfEva(lengthv, lengthp);
							
				}
								
//				GCFloat[][] m2 = new GCFloat[f.length][f[0].length];
//				for(int i = 0; i < f.length; ++i)
//					for(int j = 0; j < f[0].length; ++j)
//						m2[i][j] = m[i][j].toGCFloat();
//				
				
				/*
				GCFloat res = new FloatMatrixLib(gen).determinant(m);//createSubMatrix(m, 0, 0);
				os.flush();
				
				boolean[] bv = new boolean[res.v.length];
				boolean[] bp = new boolean[res.p.length];
				for (int i = 0; i < lengthv; i++)
					bv[i] = gen.outputToGen(res.v[i]);
				for (int i = 0; i < lengthp; i++)
					bp[i] = gen.outputToGen(res.p[i]);
				boolean bs = gen.outputToGen(res.s);
				boolean bz = gen.outputToGen(res.z);		
		
				Float result = new Float(bv,bp,bs,bz);
				System.out.print(result.toDouble()+" ");
				*/
				
				Represention[][] res = new FloatMatrixLib(gen).rref(m);	//createSubMatrix(m, 0, 0);
				double[][] r = new double[res.length][res[0].length];
				for(int i = 0 ; i < r.length; ++i)
					for(int j = 0; j < r[0].length; ++j)
						r[i][j] = gen.outputToGen(res[i][j]);
				
				for(int i = 0; i < res.length; ++i){
					for(int j = 0; j < res[0].length; ++j){
						System.out.print(r[i][j] + " ");
					}
					System.out.print("\n");
				}
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		double[][] f;
		int lengthv,lengthp;
		
		EvaRunnable (double[][] f, int lengthv, int lengthp) {
			this.f = f;
			this.lengthv = lengthv;
			this.lengthp = lengthp;
		}
		
		public void run() {
			try {
				connect("localhost", 54321);
				GCEva eva = new GCEva(is, os);
				Represention[][] m = new Represention[f.length][f[0].length];
				
				for(int i = 0; i < f.length; ++i){
					for(int j = 0; j < f[0].length; ++j)
						//if(f[i][j] != null)
						//	m[i][j] = new FloatGC(eva, f[i][j], lengthv, lengthp);
						//else
							m[i][j] = eva.inputOfGen(lengthv, lengthp);
							
				}
				
				/*
				GCFloat res = new FloatMatrixLib(gen).determinant(m);//createSubMatrix(m, 0, 0);
				os.flush();
				
				boolean[] bv = new boolean[res.v.length];
				boolean[] bp = new boolean[res.p.length];
				for (int i = 0; i < lengthv; i++)
					bv[i] = gen.outputToGen(res.v[i]);
				for (int i = 0; i < lengthp; i++)
					bp[i] = gen.outputToGen(res.p[i]);
				boolean bs = gen.outputToGen(res.s);
				boolean bz = gen.outputToGen(res.z);		
		
				Float result = new Float(bv,bp,bs,bz);
				System.out.print(result.toDouble()+" ");
				*/
				
				Represention[][] res = new FloatMatrixLib(eva).rref(m);  //createSubMatrix(m, 0, 0);
				
				for(int i = 0 ; i < res.length; ++i)
					for(int j = 0; j < res[0].length; ++j)
						eva.outputToGen(res[i][j]);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void test1Case(int a, int b) throws Exception {
		
		double [][]f = {
		        // x = 1, y = 2, z = 3
				{ 1,  2, 3, 1,0,0 },  // 1x + 2y + 3z = 14
		        { 1, -1, 1,  0,1,0 },  // 1x - 1y + 1z = 2
		        { 4, -2, 1,  0,0,1 }
		    };
		
		double[][] s = rref(f);

		for(int i = 0; i < s.length; ++i){
			for(int j = 0; j < s[0].length; ++j){
				System.out.print(s[i][j]+" ");
			}
			System.out.print("\n");
		}
		GenRunnable gen = new GenRunnable(f, 23, 9);
		EvaRunnable eva = new EvaRunnable(f, 23, 9);
		

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(1);
		tEva.start();
		tGen.join();
		
		
	}
	public static double[][] rref(double[][] mat)
	{
	    double[][] rref = new double[mat.length][mat[0].length];

	    /* Copy matrix */
	    for (int r = 0; r < rref.length; ++r)
	    {
	        for (int c = 0; c < rref[r].length; ++c)
	        {
	            rref[r][c] = mat[r][c];
	        }
	    }

	    for (int p = 0; p < 3/*rref.length*/; ++p)
	    {
	        /* Make this pivot 1 */
	        double pv = rref[p][p];
	        if (pv != 0)
	        {
	            double pvInv = 1.0 / pv;
	            for (int i = 0; i < rref[p].length; ++i)
	            {
	                rref[p][i] *= pvInv;
	            }
	        }

	        /* Make other rows zero */
	        for (int r = 0; r < rref.length; ++r)
	        {
	            if (r != p)
	            {
	                double f = rref[r][p];
	                for (int i = 0; i < rref[r].length; ++i)
	                {
	                    rref[r][i] -= f * rref[p][i];
	                }
	            }
	        }
	    }

	    return rref;
	}
	@Test
	public void runThreads() throws Exception {
		System.out.println("Testing ADD-ing numbers...");
		Random rng = new Random();
		
		//for (int i = 0; i < 1000; i++)
		test1Case(rng.nextInt()%100000, rng.nextInt()%100000);
	}
}