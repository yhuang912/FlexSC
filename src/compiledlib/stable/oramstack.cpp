#define PUSH 0
#define POP 1
typedef ints_ = int;

struct OramStack<T> {
  ints_32 size;
  T[1000] data;
};

T OramStack<T>.stack_op(T operand, ints_1 op) {
  T ret;
  if (op == POP) {
     ret = this.data[this.size];
     this.size = this.size - 1;
  } 
  else {
     this.size = this.size + 1;
     this.data[this.size] = operand;
  }
  return ret;
}
