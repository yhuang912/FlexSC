#define delta 10
#define width 1000
int64_s RND(int32_s bit) = native lib.randBools;
typedef int32_p = public int32;
typedef int64_p = public int64;
typedef int32_s = int32;
typedef int64_s = int64;

struct count_sketch {
   int64_s[public 10][public 4] hash_seed; 
   int64_s[public 10][1000]sketch;
};

void count_sketch.init() {
   for(int32_p i = 0; i < 10; i = i + 1)
      for(int32_p j = 0; j < 4; j = j + 1) 
         this.hash_seed[i][j] = RND(64);
}

//mode a big prime fast
int64_s count_sketch.fast_mod(int64_s v) {
   return ((v >> 31) + v) & 2147483647;
}

int64_s count_sketch.hash(int32_p row_number, int64_s element) {
   int64_s h = this.hash_seed[0][0]+this.hash_seed[row_number][1];
   return this.fast_mod(h) % 1000;
}

int64_s count_sketch.hash2(int32_p row_number, int64_s element) {
   int64_s h = this.hash_seed[0][2]+this.hash_seed[row_number][3];
   return this.fast_mod(h) & 1;
}


void count_sketch.insert(int64_s element, int64_s frequency) {
   for(int32_p i = 0; i < 10; i = i + 1) {
      int64_s pos = this.hash(i, element);
      int64_s g = this.hash2(i, element);
      if(g == 0)
         this.sketch[i][pos] = this.sketch[i][pos]+frequency;
      else
         this.sketch[i][pos] = this.sketch[i][pos]-frequency;
   }
}

int64_s count_sketch.query(int64_s element) {
   return element;
}
