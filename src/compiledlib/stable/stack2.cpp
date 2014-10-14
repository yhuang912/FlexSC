#define PUSH 0
#define POP 1

typedef PORAM<T> = native CircuitOram;
rnd12 RND(public int12 bit) = native lib.randBools;
dummy StackNode<T> PORAM<T>.poram_retrieve(int12 id, rnd12 pos) = native conditionalReadAndRemove;
dummy void PORAM<T>.poram_write(int12 id, rnd12 pos, StackNode<T> node) = native conditionalPutBack;

struct StackNode<T> {
  rnd12 next;
  T data;
};

struct Stack<T> {
  rnd12 root;
  int12 size;
  PORAM< StackNode<T> > poram;
};

T Stack<T>.stack_op(T operand, int2 op) {
  T ret;
  if (op == POP) { // extract root
     StackNode<T> r = this.poram.poram_retrieve(this.size, this.root);
     this.root = r.next;
     this.size = this.size - 1;
     ret = r.data;
  } else { // push operand onto the stack
     StackNode<T> node = 
        StackNode{T} ( next = this.root,
                       data = operand);
     this.root = RND(12);
     this.size  = this.size + 1;
     this.poram.poram_write(this.size, this.root, node);
  }
  return ret;
}

