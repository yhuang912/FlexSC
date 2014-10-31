#include <queue>
#include <iostream>
#include <algorithm>
#include <vector>
using namespace std;


int graph[10][3];
int dis[10];
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

void dijkstra() {
   for(int i = 0; i < 10; ++i)
      dis[i] = 1000000;

   priority_queue<pair<int, int>, vector<pair<int, int>>, compare> pq;

   pq.push(pair<int,int>(0, 0));
   dis[0] = 0;

   bool traversingNode = false;
   int next = 0;
   int currentDis;
   for(int i = 0; i < 20; ++i) {
      if(not traversingNode) {
         pair<int, int> t = pq.top();
         pq.pop();
         if(dis[t.second] >= t.first) {
            traversingNode = true;
            next = t.second;
            dis[t.second] = t.first;
            currentDis = t.first;
         }
      }
      else {
         pair<int, pair<int ,int>> e = nextEdge(next);
         next = e.second.first;
         cout << "nextEdge"<<e.first<<" "<<e.second.first<<endl;
         if(e.second.first == -1)
            traversingNode = false;
         else {
            int dist = currentDis + e.second.second;
            pq.push(pair<int, int>( dist, e.first ));
         }
      }
   }
cout <<" "<<endl;
   for(int i = 0; i < 5; ++i)
      cout << dis[i]<<endl;
}

void init() {
   graph[0][0] = 1;graph[0][1] = 6;graph[0][2] = 10;
   graph[1][0] = 3;graph[1][1] = 8;graph[1][2] = 1;
   graph[2][0] = 1;graph[2][1] = 9;graph[2][2] = 1;
   graph[3][0] = 4;graph[3][1] = 11;graph[3][2] = 1;
   graph[4][0] = -1;graph[4][1] = -1;graph[4][2] = -1;
   graph[6][0] = 2;graph[6][1] = 7;graph[6][2] = 1;
   graph[7][0] = -1;graph[7][1] = -1;graph[7][2] = -1; 
   graph[8][0] = -1;graph[8][1] = -1;graph[8][2] = -1;
   graph[9][0] = 3;graph[9][1] = -1;graph[9][2] = -1;
   graph[11][0] = -1;graph[11][1] = -1;graph[11][2] = -1;
}
int main() {
   init();
   dijkstra();
}

/*
while priorityQueue not empty {
   (v,d) = priorityQueue.deleteMin();
   if( distance[v] >= d ) {
      for w in vertices adjacent to v  {
          dist = distance[v] + weight(v,w);
          if dist < distance[w] {
             distance[w] = dist;
             previous[w] = v;
             priorityQueue.insert(w,dst);
        }
      }
   }
}

//==================================
traversingNode  = false;

while priorityQueue not empty {
   if(not traversingNode) {
      (v,d) = priorityQueue.deleteMin();
      if( distance[v] >= d ) {
         traversingNode = true;
         next = v;
      }
   }
   else {
      (w, next) = nextedgenode(next);
      dist = distance[v] + weight(v,w);
      if dist < distance[w] {
         distance[w] = dist;
         previous[w] = v;
         priorityQueue.insert(w,dst);
      }  
   }
}

*/
