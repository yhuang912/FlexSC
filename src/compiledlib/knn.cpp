#define N 1000
#define Di 2
#define k 4
struct kNN{
};
void sort(int32[public N] k, int32[public N] v) = native intLib.sort;

struct Point {
   int32[public Di] cor;
};

int32 kNN.distance(Point p1, Point p2) {
   int32 ret = 0;
   for(public int32 i = 0; i < Di; i = i + 1) {
      ret = ret + (p1.cor[i]-p2.cor[i])*(p1.cor[i]-p2.cor[i]);
   }
   return ret;
}
int32 kNN.function(Point[public N] data, int32[public N]labels, Point query) {
   int32[public N] dist;
   for(public int32 i = 0; i < N; i = i + 1) {
      dist[i] = this.distance(data[i], query);
   }
   sort(dist, labels);
   int32 ret;
   for(public int32 i = 0; i < k; i = i + 1) {
      ret = ret + labels[i];
   }
   return ret;
}
