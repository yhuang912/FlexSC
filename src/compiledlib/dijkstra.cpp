#define V_E 100
#define INT_MAX 100000
#define false 0
#define true 1

typedef priority_queue = native PriorityQueue;
phantom Pair<Int, Int> priority_queue.pop() = native pop;
phantom void priority_queue.push(int32 k, int32 v) = native push;


struct Pair<V1, V2> {
   V1 first;
   V2 second;
};
struct Int{int32 data;};

struct Dijkstra {
   int32[V_E][public 3] graph;
   int32[V_E] dis;
};


void Dijkstra.funct() {
   for(public int32 i = 0; i < V_E; i = i + 1)
      this.dis[i] = INT_MAX;
   priority_queue pq;
   int32 zero = 0;
   pq.push(zero, zero);
   this.dis[0] = 0;

   int1 traversingNode = false;
   int32 next = 0;
   int32 currentDis;
   for(public int32 i = 0; i < 2*V_E; i = i + 1) {
      if(traversingNode == false) {
         Pair<Int, Int> t = pq.pop();
         if(this.dis[t.second.data] >= t.first.data) {
            traversingNode = true;
            next = t.second.data;
            this.dis[t.second.data] = t.first.data;
            currentDis = t.first.data;
         }
      }
      else {
         int32[3] e = this.graph[next];
         next = e[1];
         if(next == 0-1)
            traversingNode = false;
         else {
            int32 dist = currentDis + e[2];
            int32 key1 = dist, val1 = e[0];
            pq.push(key1, val1);
         }
      }
   }
}
