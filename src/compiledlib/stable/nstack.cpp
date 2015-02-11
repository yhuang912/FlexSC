//Preamble
typedef _SecureStorage@m<T> = native CircuitOram;
rnd@m RND(public int32 m) = native intLib.randBools;
dummy T _SecureStorage@m<T>.remove(int@m id, int@m pos) = native conditionalReadAndRemove;
dummy void _SecureStorage@m<T>.add(int@m id, int@m pos, T node) = native conditionalPutBack;
struct Pointer@m{
   int@m index;
   rnd@m pos;
};


struct SecureStorage@m<T>{
   _SecureStorage@m<T> oram;
};

dummy void SecureStorage@m<T>.add(Pointer@m p, T data) {
   this.oram.add(p.index, p.pos, data);
}

dummy T SecureStorage@m<T>.remove(Pointer@m p) {
   return this.oram.remove(p.index, p.pos);
}


struct StackNode@m<T> {
   Pointer@m next;
   T data;
};

struct Stack@m<T> {
   Pointer@m root;
   SecureStorage@m< StackNode@m<T> > store;
};

dummy void Stack@m<T>.push(T operand) {
  StackNode@m<T> node = 
  StackNode@m{T} (this.root, operand);
//this line does not compile
//  this.root = Pointer@m(this.root.index+1, RND(m));

//  this.secure_store.add(this.root, node);
}

dummy void Stack@m<T>.pop() {
   //   StackNode@m<T> r = 
   Pointer@m root = Pointer@m(0, RND(m));
   StackNode@m<T> res = this.store.remove(root);
   //   this.root = r.next;
   //   return r.data;
}
