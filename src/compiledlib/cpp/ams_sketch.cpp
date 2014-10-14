#define delta 10
#define width 1000

typedef intp_ = public int;
typedef ints_ = secure int;

ints_64 RND(intp_32 bit) = native lib.randBools;

struct ams_sketch{
   ints_64[public 10][public 6] hash_seed; 
   ints_64[public 10][1000]sketch;
};

void ams_sketch.init() {
   for(intp_32 i = 0; i < 10; i = i + 1)
      for(intp_32 j = 0; j < 6; j = j + 1) 
         this.hash_seed[i][j] = RND(64);
}

//mode a big prime fast
ints_64 ams_sketch.fast_mod(ints_64 v) {
   return ((v >> 31) + v) & 2147483647;
}

ints_64 ams_sketch.hash2(intp_32 row_number, ints_64 element) {
   ints_64 h = this.hash_seed[0][0]+this.hash_seed[row_number][1];
   return this.fast_mod(h) % 1000;
}

ints_64 ams_sketch.hash4(intp_32 row_number, ints_64 element) {
   ints_64 h = this.hash_seed[row_number][2];
   h = this.fast_mod(h*element+this.hash_seed[row_number][3]);
   h = this.fast_mod(h*element+this.hash_seed[row_number][4]);
   h = this.fast_mod(h*element+this.hash_seed[row_number][5]);
   return (h & 1);
}

void ams_sketch.insert(ints_64 element, ints_64 frequency) {
   for(intp_32 i = 0; i < 10; i = i + 1) {
      ints_64 pos = this.hash2(i, element);
      ints_64 h4 = this.hash4(i, element);
      if(h4 == 0)
         this.sketch[i][pos] = this.sketch[i][pos]+frequency;
      else
         this.sketch[i][pos] = this.sketch[i][pos]-frequency;
   }
}

ints_64 ams_sketch.query(ints_64 element) {
   int64[public 10] res;
   for(intp_32 i = 0; i < 10; i = i + 1) {
      res[i] = 0;
      for(intp_32 j = 0; j < 1000; j = j + 1) {
         res[i] = res[i] + this.sketch[i][j]*this.sketch[i][j];
      }
   }

   for(i = 0; i < 10; i = i + 1) {
      for(j = 0; j < 10; j = j + 1) {
         if(res[i] < res[j]) {
            res[i] = res[i] ^ res[j];
            res[j] = res[i] ^ res[j];
            res[i] = res[i] ^ res[j];
         }
      }
   }
   return res[5];
}
