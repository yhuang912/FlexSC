//Preamble
#define PUSH 0
#define POP 1
typedef ints_ = secure int;
typedef intp_ = public int;
typedef intr_ = rnd;
typedef NonRecursiveORAM@m<T> = native CircuitOram;
intr_@m RND(intp_32 m) = native intLib.randBools;
phantom StackNode@m<T> NonRecursiveORAM@m<T>.readAndRemove(int@m id, rnd@m pos) = native conditionalReadAndRemove;
phantom void NonRecursiveORAM@m<T>.add(int@m id, rnd@m pos, StackNode@m<T> node) = native conditionalPutBack;

struct BoolArray{int@1024 data;};

struct StackNode@m<T> {
   intr_@m next;
   T data;
};

struct Stack@m<T> {
   intr_@m root;
   ints_@m size;
   NonRecursiveORAM@m< StackNode@m<T> > oram;
};

T Stack@m<T>.stack_op(T operand, ints_1 op) {
   T ret;
   if (op == POP) { // extract root
      StackNode@m<T> r = this.oram.readAndRemove(this.size, this.root);
      this.root = r.next;
      this.size = this.size - 1;
      ret = r.data;
   } else { // push operand onto the stack
      StackNode@m<T> node = 
         StackNode@m{T} ( next = this.root,
               data = operand);
      this.root = RND(m);
      this.size  = this.size + 1;
      this.oram.add(this.size, this.root, node);
   }
   return ret;
}
void Stack@m<T>.push(T operand, ints_1 op) {
   if(op == 1) { // push operand onto the stack
      StackNode@m<T> node = 
         StackNode@m{T} ( next = this.root,
               data = operand);
      this.root = RND(m);
      this.size  = this.size + 1;
      this.oram.add(this.size, this.root, node);
   }
}
T Stack@m<T>.pop(ints_1 op) {
   T ret;
   if (op == 1) { // extract root
      StackNode@m<T> r = this.oram.readAndRemove(this.size, this.root);
      this.root = r.next;
      this.size = this.size - 1;
      ret = r.data;
   }
   return ret;
}

