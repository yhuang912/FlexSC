#include <string.h>

int main(int argc, char **argv) { 
  char a[] = "aaaaaaaaaa";
  char b[] = "bbbbbbbbbb"; 
  sfe_main(a,b); 
  return 0; 
}

int sfe_main(char * a, char * b){
  int i,j = 0;
  int aLen = 10;
  int bLen = 10;
  int C[aLen][bLen];
  for (i = 0; i < aLen; i++){
    C[i][0] = 0;
  }
  for (i = 0; i < bLen; i++){
    C[0][i] = 0;
  }
  for (i = 1; i < aLen; i++){
    for (j = 1; j< bLen; j++){
      if (a[i] == b[j])
	C[i][j] = C[i-1][j-1] + 1;
      else
	C[i][j] = (C[i][j-1] >= C[i-1][j]) ? C[i][j-1] : C[i-1][j];
    }
  }
  return C[aLen][bLen];
}

/*function backtrack(C[0..m,0..n], X[1..m], Y[1..n], i, j)
    if i = 0 or j = 0
        return ""
    else if  X[i] = Y[j]
        return backtrack(C, X, Y, i-1, j-1) + X[i]
    else
        if C[i,j-1] > C[i-1,j]
            return backtrack(C, X, Y, i, j-1)
        else
            return backtrack(C, X, Y, i-1, j)
*/
