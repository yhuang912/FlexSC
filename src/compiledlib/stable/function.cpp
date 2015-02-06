int@log(n+1) countOnes@n(int@n x) {
  if(n==1) return x;
  int@log(n/2+1) first = countOnes@(n/2)(x$0~n/2$);
  int@log(n-n/2+1) second = countOnes@(n-n/2)(x$n/2~n$);
  int@log(n+1) r = first + second;
  return r;
}
 
int@log(n) leadingZero@n(int@n x) {
      int@n y = 0;
      int1 one = 1;
      for(public int32 i=n-2; i>=0; i=i-1)
            if(x$i$==0 && y$i+1$==1)
                  y$i$ = 1;

}
 
