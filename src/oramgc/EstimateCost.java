package oramgc;

import java.util.ArrayList;
import java.util.Arrays;

import oramgc.EstimateCost.KaiminOram;
import oramgc.EstimateCost.recursiveBoost;
import sun.misc.Sort;

public class EstimateCost {

	public EstimateCost() {
		// TODO Auto-generated constructor stub
	}


	
	public class ORAM {
		long lengthOfPos, lengthOfIden, lengthOfData, logN, capacity, N;
		public ORAM(long N, long dataSize, long capacity){
			this.N = N;
			lengthOfData = dataSize;
			logN = 1; long a = 1;while(a <= N){a<<=1;logN++;}
			lengthOfIden = logN;
			lengthOfPos = logN;
			this.capacity = capacity;
		}

		public long readAndRemove() {
			return logN*readAndRemoveBucket(capacity);
		}
		
		public  long eq(long a){return a;}
		public  long mux(long a){return a;}

		public  long readAndRemoveBucket(long cap) {
			return cap * (// there are totally capacity number of blocks
					eq(lengthOfIden) + 2* mux(lengthOfData+lengthOfPos+lengthOfIden));//each block, we need to compare iden if it is the block we are
			//looking for, depending on this, we update result and block.
		}

		public  long pop(long cap) {
			return cap * (// there are totally capacity number of blocks
					eq(lengthOfIden) + 2* mux(lengthOfData+lengthOfPos+lengthOfIden) + 4);//each block, we need to compare iden if it is the block we are
			//looking for, depending on this, we update result and block.
		}

		public  long add(long cap) {
			return cap * (// there are totally capacity number of blocks
					2*eq(lengthOfIden) + mux(lengthOfData+lengthOfPos+lengthOfIden) + 4);//each block, we need to compare iden if it is the block we are
			//looking for, depending on this, we update result and block.
		}

		public  long sort(long n, long lengthOfKey, long lengthOfData) {
			return n*log(n)*(eq(lengthOfKey) + 2*mux(lengthOfData))/4;
		}

		public  long log(long n) {
			return (long) (Math.log(n)/Math.log(2));
		}
		public ORAM newInstance(long n, long l){
			return null;
		}
		public long access(){
			return readAndRemove();
		}
	}
	

	public class Treeoram extends ORAM{
		public Treeoram(long N, long dataSize) {
			super(N, dataSize,  (long) (1.5*Math.log(N)/Math.log(2)) );
		}
		public long Evict() {
			return (logN-1)*(pop(capacity) +
					2*add(capacity));
		}
		public long access() {
			return readAndRemove()+add(capacity)+2*Evict();
		}
		public Treeoram newInstance(long n, long l){
			return new Treeoram(n, l);
		}
	}

	public class Pathoram extends ORAM{
		long stashSize = 89;
		public Pathoram(long N, long dataSize) {
			super(N, dataSize, 4);
		}
		
		public long readAndRemove(){
			return super.readAndRemove()+readAndRemoveBucket(stashSize);//readand remove blocks from tree and stash
		}

		public long access() {
			return  readAndRemove()
					+ (stashSize + logN*capacity) * (lengthOfIden+(lengthOfIden*log(lengthOfIden)))  //compute deepeest level to push for each block
					+ logN*capacity*(stashSize+logN*capacity)*(eq(lengthOfIden)+mux(lengthOfData)+lengthOfPos+lengthOfIden)+mux(lengthOfIden)+
					sort(logN*capacity+stashSize, lengthOfIden, lengthOfData+lengthOfIden+lengthOfPos);
		}
		public Pathoram newInstance(long n, long l){
			return new Pathoram(n, l);
		}
	}
	
	public class PathoramAdv extends ORAM{
		long stashSize = 89;
		public PathoramAdv(long N, long dataSize) {
			super(N, dataSize, 4);
		}
		
		public long readAndRemove(){
			return super.readAndRemove()+readAndRemoveBucket(stashSize);//readand remove blocks from tree and stash
		}

		public long access() {
			return  readAndRemove()
					+ (stashSize + logN*capacity) * (lengthOfIden+(lengthOfIden*log(lengthOfIden)))  //compute deepeest level to push for each block
					//+ logN*capacity*(stashSize+logN*capacity)*(eq(lengthOfIden)+mux(lengthOfData)+lengthOfPos+lengthOfIden)+mux(lengthOfIden)+
					+3*sort(2*logN*capacity+stashSize, lengthOfIden, lengthOfData+lengthOfIden+lengthOfPos)
					+ (2*logN*capacity+stashSize)*5;
		}
		public PathoramAdv newInstance(long n, long l){
			return new PathoramAdv(n, l);
		}
	}
	
	public class KaiminOram extends ORAM {
		long stashSize = 122;
		public KaiminOram(long N, long dataSize) {
			super(N, dataSize, 6);
		}

		public long readAndRemove() {
			return super.readAndRemove()+readAndRemoveBucket(stashSize);//readand remove blocks from tree and stash
		}
		
		public long putBack() {
			return pop(stashSize) + add(capacity);
		}
		
		public long flush() {
			return (logN*capacity) * (lengthOfIden+(lengthOfIden*log(lengthOfIden)))  //compute deepeest level to push for each block
			      + (logN-1)*(
			    		  pop(capacity) + add(capacity)
			    		  + add(logN) // overflow
			    		  +logN*logN//to count if there is overflow
			    		  + sort(logN+stashSize, lengthOfIden, lengthOfData+lengthOfIden+lengthOfPos)//merge tmp logN cache to stash
			    		  );
		}
		
		public long dequeue() {
			return (long) (putBack() + 1.5*flush()); 
		}
		public long access() {
			return readAndRemove() + dequeue()*2;
		}
		public KaiminOram newInstance(long n, long l){
			return new KaiminOram(n, l);
		}
	}
	
	public class recursiveBoost<T extends ORAM> {
		ArrayList<T> orams;
		T o;
		public recursiveBoost(T o, long N, long dataSize) throws InstantiationException, IllegalAccessException {
			this.o = o;
			orams = new ArrayList<>();
			T oram = (T) o.newInstance(N, dataSize);
			long clientStorage = N * oram.lengthOfIden;
			orams.add(oram);
			long cutoff = 1024;
			while(clientStorage >= cutoff) {
				long newDataSize = oram.lengthOfIden*2;
				long newN = oram.N/2;
				oram = (T) o.newInstance(newN, newDataSize);
				orams.add(oram);
				clientStorage = oram.N * oram.lengthOfIden;
			}
		}
		
		public long access(){
			long res = 0;
			for(T it : orams)
				res += it.access();
			return res;
		}
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		EstimateCost e = new EstimateCost();
		long N = 1<<20;
		long dataSize = 128;
		KaiminOram kaimin = e.new KaiminOram(N, dataSize);
		Pathoram pathoram = e.new Pathoram(N, dataSize);
		PathoramAdv pathoram2 = e.new PathoramAdv(N, dataSize);
		Treeoram treeoram = e.new Treeoram(N, dataSize);
		System.out.println(kaimin.readAndRemove()+"\t\t"+(kaimin.access()-kaimin.readAndRemove())+"\t\t"+kaimin.access());
		System.out.println(pathoram.readAndRemove()+"\t\t"+(pathoram.access()-pathoram.readAndRemove())+"\t\t"+pathoram.access());
		System.out.println(pathoram2.readAndRemove()+"\t\t"+(pathoram2.access()-pathoram2.readAndRemove())+"\t\t"+pathoram2.access());
		System.out.println(treeoram.readAndRemove()+"\t\t"+(treeoram.access()-treeoram.readAndRemove())+"\t\t"+treeoram.access());
		
		recursiveBoost<PathoramAdv> o = e.new recursiveBoost<PathoramAdv>(pathoram2, N, dataSize);
		System.out.print(o.access());
	}

}
