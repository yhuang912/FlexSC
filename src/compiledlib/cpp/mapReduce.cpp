typedef ints_ = int;
typedef intp_ = public int;
struct Pair<K, V> {
   K k;
   V v;
};

typedef ints_1 compare<T>(T, T) = native Comparator;
void sort<T>(T[public 10] arr, ints_1 dir, ints_1 cmp(T, T)) = native lib.sort;

struct mapReduce<I, K, V >{};

ints_1 mapReduce<I, K, V>.map_reduce(
      I[public 100] data,
      intp_32 length,
      Pair<K, V> map(I), 
      Pair<K, V> reduce(K, Pair<V, V>),
      ints_1 cmp(Pair<K, V>, Pair<K, V>)
      )
{
   Pair<K, V>[public 100] d2;
   for(intp_32 i = 0; i < length; i = i + 1) {
      d2[i] = map(data[i]);
   }
   sort(d2, 1, cmp);

   ints_1 res = 1;
   return res;
}


