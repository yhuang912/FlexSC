#define PUSH 0
#define POP 1

typedef intr_ = rnd;
typedef intp_ = public int;
typedef ints_ = int;
typedef NonRecursiveORAM@m<T> = native CircuitOram;
intr_@m RND(intp_32 m) = native lib.randBools;
dummy QueueNode@m<T> NonRecursiveORAM@m<T>.oram_retrieve(ints_@m id, intr_@m pos) = native conditionalReadAndRemove;
dummy void NonRecursiveORAM@m<T>.oram_write(ints_@m id, intr_@m pos, QueueNode@m<T> node) = native conditionalPutBack;

struct QueueNode@m<T> {
  intr_@m next;
  T data;
};

struct Queue@m<T> {
  intr_@m front_pos;
  intr_@m back_pos;
  ints_@m front_id;
  ints_@m back_id;
  NonRecursiveORAM@m< QueueNode@m<T> > oram;
};

//f                   b
//1 -> 2 -> 3 -> 4 -> ?
T Queue@m<T>.queue_op(T operand, ints_1 op) {
  T ret;
  QueueNode@m<T> node;
  if (op == POP) {
     node = this.oram.oram_retrieve(this.front_id, this.front_pos);
     this.front_id = this.front_id + 1;
     this.front_pos = node.next;
     ret = node.data;
  } 
  else {
     intr_@m tmp = this.back_pos;
     this.back_pos = RND(32);
     node = QueueNode@m{T} ( next = this.back_pos,
         	               data = operand);
     this.oram.oram_write(this.back_id, tmp, node);
     this.back_id = this.back_id + 1;
  }
  return ret;
}
