//Preamble
#define PUSH 0
#define POP 1
typedef NonRecursiveORAM<T> = native CircuitOram;
//rnd@m RND(public int32 m) = native intLib.randBools;
dummy bsNode NonRecursiveORAM<T>.readAndRemove(int32 id, int32 pos) = native conditionalReadAndRemove;
dummy void NonRecursiveORAM<T>.add(int32 id, int32 pos, bsNode node) = native conditionalPutBack;

typedef NonRecursiveORAM2<T> = native CircuitOram;
dummy BArray NonRecursiveORAM2<T>.readAndRemove(int32 id, int32 pos) = native conditionalReadAndRemove;
dummy void NonRecursiveORAM2<T>.add(int32 id, int32 pos, BArray node) = native conditionalPutBack;

struct BArray{int32 data;};
struct bsNode  {
   int32[public 8]keys;
   int32[public 8]pos;
};

struct bs {
   NonRecursiveORAM< bsNode >[public 4] oram;
   int32[public 64]ownkeys;
   int32[public 64]ownposes;
   NonRecursiveORAM2< Array > baseoram;
};


// 1 2 3 4 5
// 2 3 4 5 6
int32 bs.search(int32 key) {
   int32 touse = 0;
   for(public int32 i = 0; i < 64; i = i + 1 ) {
      if(key == this.ownkeys[i])
         touse = this.ownposes[i];
   }

   bsNode[public 4] nodes;
   int32[public 6] rnds;
   for(public int32 i = 0; i < 4; i = i + 1) {
      nodes[i] = this.oram[i].readAndRemove(key, touse);
      for(public int32 j = 0; j < 8; j = j + 1) {
         if(key == nodes[i].keys[j]) {
            touse = nodes[i].pos[j];
            nodes[i].pos[j] = rnds[i+1];
         }
      }
      this.oram[i].add(key, rnds[i], nodes[i]);
   }
//   Array ret = this.baseoram.readAndRemove(key, touse);
//   this.baseoram.add(key, rnds[5], ret); 
//   return ret.data;
}
