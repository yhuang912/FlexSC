#define true 1
#define false 0
#define M 1000
typedef intp_ = public int;
typedef ints_ = secure int;

void sort<T>(T[public 10] arr, ints_1 dir, ints_1 com(T, T)) = native lib.sort;
struct sp_node{
   ints_32 x;
   ints_32 y;
   ints_32 v;
};

ints_1 comp(sp_node a, sp_node b){
   ints_1 result = false;
   if(a.x > b.x)
      result = true;
   else if (a.x < b.x)
      result = false;
   else {
      if(a.y >= b.y)
         result = true;
      else result = false;
   }
   return result;
}

struct spmatrix{
   sp_node[public 1000] nodes;
   ints_32 length;
};

spmatrix spmatrix.add(sp_node[public 1000] a, sp_node[public 1000] b) {
   sort(a, 1, comp);
}

