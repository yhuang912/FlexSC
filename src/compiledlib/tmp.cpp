#define BUCKETSIZE 3
#define STASHSIZE 33
#define true 1
#define false 0

struct Block@n<T> {
   int@n id;
   int@n pos;
   T data;
   int1 isDummy;
};

struct CircuitOram@n<T> {
   Block@n<T>[public 1<<n+1][public BUCKETSIZE] buckets;
   Block@n<T>[public STASHSIZE] stash;
   public int32 cnt;
};

phantom T CircuitOram@n<T>.ReadAndRemove(int@n id, rnd@n pos) {
   public int32 p_pos = p_pos;
   public int32 lvl = (1 << n) - 1;
   T res;
   for(public int32 i = lvl + p_pos; i>=0; i=(i-1)/2) {
      public int32 idx = lvl + p_pos;
      for(public int32 j=0; j<BUCKETSIZE; j=j+1) {
         if(this.buckets[i][j].isDummy == false) {
            if(this.buckets[i][j].id == id) {
               res = this.buckets[i][j].data;
               this.buckets[i][j].isDummy = true;
            }
         }
      }
   }
   for(public int32 i=0; i<STASHSIZE; i=i+1) {
      if( this.stash[i].isDummy == false) {
         if(this.stash[i].id == id) {
            res = this.stash[i].data;
            this.stash[i].isDummy = true;
         }
      }
   }
   return res;
}
