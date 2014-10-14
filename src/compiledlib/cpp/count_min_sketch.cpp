#define delta 10
#define width 1000
typedef intp_ = public int;
typedef ints_ = secure int;
ints_64 RND(intp_32 bit) = native lib.randBools;

struct count_min_sketch {
   ints_64[public 10][public 2] hash_seed; 
   ints_64[public 10][1000]sketch;
};

void count_min_sketch.init() {
   for(intp_32 i = 0; i < delta; i = i + 1)
      for(intp_32 j = 0; j < 2; j = j + 1) 
         this.hash_seed[i][j] = RND(64);
}

//mode a big prime fast
ints_64 count_min_sketch.fast_mod(ints_64 v) {
   return ((v >> 31) + v) & 2147483647;
}

ints_64 count_min_sketch.hash(intp_32 row_number, ints_64 element) {
   ints_64 h = this.hash_seed[row_number][0]*element+this.hash_seed[row_number][1];
   return this.fast_mod(h) % width;
}

void count_min_sketch.insert(ints_64 element, ints_64 frequency) {
   for(intp_32 i = 0; i < delta; i = i + 1) {
      ints_64 pos = this.hash(i, element);
      this.sketch[i][pos] = this.sketch[i][pos]+frequency;
   }
}

ints_64 count_min_sketch.query(ints_64 element) {
   ints_64 minimum = 1<<31;
   for(intp_32 i = 0; i < delta; i = i + 1) {
      ints_64 pos = this.hash(i, element);
      ints_64 s = this.sketch[i][pos];
      if(s < minimum)
         minimum = s;
   }
   return minimum;
}
