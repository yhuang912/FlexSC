//Preamble
typedef _SecureStorage@m<T> = native CircuitOram;
int@m RND(public int32 m) = native intLib.randBools;
phantom T _SecureStorage@m<T>.remove(int@m id, int@m pos) = native conditionalReadAndRemove;
phantom void _SecureStorage@m<T>.add(int@m id, int@m pos, T node) = native conditionalPutBack;
struct Pointer@m{
   int@m index;
   int@m pos;
};

struct SecureStorage@m<T>{
   _SecureStorage@m<T> oram;
};

phantom void SecureStorage@m<T>.add(Pointer@m p, T data) {
   this.oram.add(p.index, p.pos, data);
}

phantom T SecureStorage@m<T>.remove(Pointer@m p) {
   this.oram.remove(p.index, p.pos);
}
Pointer@m SecureStorage@m<T>.newPointer(int@m index) {
   return Pointer@m(index, RND(m));
}


struct StackNode@m<T> {
   Pointer@m next;
   T data;
};

struct Stack@m<T> {
   Pointer@m root;
   SecureStorage@m< StackNode@m<T> > store;
};

void Stack@m<T>.push(T data) {
   StackNode@m<T> node = 
      StackNode@m{T} (this.root, data);
   this.root = this.store.newPointer(this.root.index+1);
   this.store.add(this.root, node);
}

T Stack@m<T>.pop() {
   StackNode@m<T> res = this.store.remove(this.root);
   this.root = res.next;
   return res.data;
}
