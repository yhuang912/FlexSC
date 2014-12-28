int sfe_main(int a, int b) { 
  int ret = 0;
  if(a>=b) 
    ret = func_a(a,b); 
  else 
    ret = func_b(a,b); 
  
  return ret; 
}

int func_a(int a, int b) { return a + b; }
int func_b(int a, int b) { return a - b; }
int main(int argc, char **argv) { sfe_main(1,2); return 0; }
