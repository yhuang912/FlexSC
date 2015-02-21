#define INT_MAX 1<<30
#define V_E 100
#define false 0
#define true 1

typedef priority_queue = native PriorityQueue;
phantom Pair<Int, Int> priority_queue.pop() = native pop;
phantom void priority_queue.push(int32 k, int32 v) = native push;


struct Pair<V1, V2> {
   V1 first;
   V2 second;
};

struct Prim{
   int32[V_E][3] graph;
   int32[V_E] dis;
};

struct Int{int32 data;};

void Prim.funct() {
   int1[10] explored;
   for(public int32 i = 0; i < 10; i = i + 1)
      explored[i] = false;
   priority_queue<Int, Int> pq;

   int32 zero = 0;
   pq.push(zero, zero);
   int32 next = 0;
   int32 res = 0;
   int1 traversingNode = false;
   for(public int32 i = 0;i < 2*V_E; i = i + 1) {
      if(traversingNode == false){
         Pair<Int,Int>t = pq.pop();
         if(explored[t.second.data] == false) {
            explored[t.second.data] = true;
            traversingNode = true;
            res = res + t.first.data;
            next = t.second.data;
         }
      }
      else {
         int32[3] e = this.graph[next];
         next = e[1];
         if(next == 0-1) {
            traversingNode = false;
         } else {
            int32 key1 = e[2], val1 = e[0];
            pq.push(key1, val1);
         }
      }
   }
}
