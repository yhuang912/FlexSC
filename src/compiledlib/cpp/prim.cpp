#include <queue>
#include <iostream>
#include <algorithm>
#include <vector>
using namespace std;


int graph[10][3];
pair<int, pair<int , int>> nextEdge(int next) {
   return pair<int, pair<int ,int>>(graph[next][0], pair<int ,int>(graph[next][1], graph[next][2]));
}

struct compare  
{  
   bool operator()(const pair<int, int>& l, const pair<int, int>& r)  
   {  
      return l.first > r.first;  
   }  
};

void prim(){
   bool explored[10];
   for(int i = 0; i < 10; ++i) explored[i] = false;
   priority_queue<pair<int, int>, vector<pair<int, int>>, compare> pq;
   
   pq.push(pair<int, int>(0, 0));
   int next = 0;
int res = 0;
   bool traversingNode = false;
   for(int i = 0;i < 20; ++i) {
      if(not traversingNode){
         pair<int,int>t = pq.top(); pq.pop();
cout << t.first<<" "<<t.second<<endl;
         if(not explored[t.second]) {
            explored[t.second] = true;
            traversingNode = true;
            res += t.first;
            next = t.second;
         }
      }
      else {
         pair<int, pair<int, int>> e = nextEdge(next);
         next = e.second.first;
         if(next == -1) {
            traversingNode = false;
         } else {
            pq.push(pair<int, int>(e.second.second, e.first));
         }
      }
   }
cout << res<<endl;
}
void init() {
   graph[0][0] = 1;graph[0][1] = 6;graph[0][2] = 10;
   graph[1][0] = 3;graph[1][1] = 8;graph[1][2] = 1;
   graph[2][0] = 1;graph[2][1] = 9;graph[2][2] = 1;
   graph[3][0] = 4;graph[3][1] = 11;graph[3][2] = 1;
   graph[4][0] = -1;graph[4][1] = -1;graph[4][2] = -1;
   graph[6][0] = 2;graph[6][1] = 7;graph[6][2] = 3;
   graph[7][0] = -1;graph[7][1] = -1;graph[7][2] = -1; 
   graph[8][0] = -1;graph[8][1] = -1;graph[8][2] = -1;
   graph[9][0] = 3;graph[9][1] = -1;graph[9][2] = -1;
   graph[11][0] = -1;graph[11][1] = -1;graph[11][2] = -1;
}
int main() {
   init();
   prim();
}

/*
pq.insert(0, 0);
while priorityQueue not empty {
   (w, d) = priorityQueue.deleteMin();
   if( if  d is not explored ) {
      res += w;
      for (ww, dd) in vertices adjacent to d  {
             priorityQueue.insert(ww, dd);
      }
   }
}

//==================================
traversingNode  = false;

while priorityQueue not empty {
   if(not traversingNode) {
      (w,  d) = priorityQueue.deleteMin();
      if( d is not explored ) {
         traversingNode = true;
         res += w;
         next = d;
      }
   }
   else {
      (w, next, d) = nextedgenode(next);
      priorityQueue.insert(w, d);
   }
}

*/

