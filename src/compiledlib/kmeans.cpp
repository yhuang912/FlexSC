#define Di 2
#define k 1000
struct KMeans{
};
struct Pair<V1, V2> {
   V1 first;
   V2 second;
};

struct Int {
   int32 data;
};

struct Point {
   int32[public Di] cor;
};

int32 KMeans.distance(Point p1, Point p2) {
   int32 ret = 0;
   for(public int32 i = 0; i < Di; i = i + 1) {
      ret = ret + (p1.cor[i]-p2.cor[i])*(p1.cor[i]-p2.cor[i]);
   }
   return ret;
}

Pair<Int, Pair<Point, Int> > KMeans.map(Point p, Point[public k] center) {
   int32 dist = this.distance(p, center[0]);
   int32 id = 0;
   for (public int32 i = 1; i < k; i = i + 1) {
      int32 new_dist = this.distance(p, center[i]);
      if(dist > new_dist) {
         dist = new_dist;
         id = i;
      } 
   }
   return Pair{Int, Pair<Point, Int> }(Int(id), Pair{Point, Int}(p, Int(1)));
}

Pair<Point, Int> KMeans.reduce(int32 id, Pair<Point, Int> val1, Pair<Point, Int> val2) {
   Point added;
   for(public int32 i = 0; i < Di; i = i +1) {
      added.cor[i] = val1.first.cor[i] + val2.first.cor[i];
   }
   return Pair{Point, Int}(added, Int(val1.second.data+val2.second.data));
}
