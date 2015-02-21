int@(2*n) karatsubaMult@n(int@n x, int@n y) {
   if(n < 18) {
      return x*y;
   }
   else {
      int@(n - n/2) a = x$n/2~n$;
      int@(n/2) b = x$0~n/2$;
      int@(n - n/2) c = y$n/2~n$;
      int@(n/2) d = y$0~n/2$;

      int@(2*(n - n/2)) term1 = karatsubaMult@(n - n/2)(a, c);
      int@(2*(n/2)) term2 = karatsubaMult@(n/2)(b, d);

      int@(n - n/2 + 1) a_plus_b = a + b;
      int@(n - n/2 + 1) c_plus_d = c + d;
      int@(2*(n - n/2 + 1)) crossTerm = karatsubaMult@(n - n/2 + 1)(a_plus_b, c_plus_d);

      int @(2*n) padTerm1 = term1;
      int @(2*n) padTerm2 = term2;
      int @(2*n) padTerm3 = crossTerm;
      int@(2*n) sum = (padTerm1<<(n/2*2)) + padTerm2 + ((padTerm3 - padTerm1 - padTerm2)<<(n/2));
      return sum;
   }
}
