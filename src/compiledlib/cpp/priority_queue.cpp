#define true 0
#define false 1
#define PUSH 0
#define POP 1
#define logN 10

typedef intp_ = public int;
typedef ints_ = secure int;
typedef intr_ = affine int;
typedef key_t = ints_32;
typedef PORAM<T> = native CircuitOram;

intr_32 RND(intp_32 bit) = native lib.randBools;
dummy PriorityQueueNode<T> PORAM<T>.poram_retrieve(ints_32 id, intr_32 pos) = native conditionalReadAndRemove;
dummy void PORAM<T>.poram_write(ints_32 id, intr_32 pos, PriorityQueueNode<T> node) = native conditionalPutBack;

struct NodeId {
   ints_32 id;
   intr_32 pos;
};

struct KeyValue<T> {
   key_t key;
   T value;
};

struct PriorityQueueNode<T> {
   NodeId left;
   NodeId right;
   KeyValue<T> keyvalue;
};

struct PriorityQueue<T> {
   NodeId root;
   ints_32 size;
   PORAM< PriorityQueueNode<T> > poram;
};

KeyValue<T> PriorityQueue<T>.pqueue_op(key_t key, T operand, ints_2 op) {
   KeyValue<T> ret;
   KeyValue<T> d = KeyValue{T}(key = key, value = operand);
   if(op == POP){
      ints_2 insert_dummy_bit = false;
      ints_2 pop_dummy_bit = true;
   }
   else {
      insert_dummy_bit = true;
      pop_dummy_bit = false;
   }
   ret = this.extractMax(pop_dummy_bit);
   this.insert(d, insert_dummy_bit);
   return ret;
}

ints_32 PriorityQueue<T>.right_shift(ints_32 id, intp_32 s) {
   ints_32 ret = id;
   for(intp_32 i = 0; i < s; i = i + 1)
      ret = ret >> 1;
   return ret;
}

int2 PriorityQueue<T>.should_go_left(ints_32 id, intp_32 level) {
   ints_2 ret;
   if(level >= logN-1)
      ret = true;
   else {
      ints_32 res = 0;
      ints_32 tmpid = id;
      for(intp_32 i = 0; i < logN; i = i + 1)
      {
         if(tmpid == 1)
            res = i;
         tmpid = tmpid >> 1;
      }

      tmpid = id;
      ints_32 toshift = res - level;
      for(i = 0; i < logN; i = i +1)
      {
         if(toshift > 0){
            tmpid  = tmpid >> 1;
            toshift = toshift - 1;
         }
      }
      if(id == 1)
         ret = true;
      else if(tmpid & 1 == 0)
         ret = true;
      else ret = false;

   }
   return ret;
}

KeyValue<T> PriorityQueue<T>.extractMax(ints_2 dummy_bit) {
   KeyValue<T> ret;
   PriorityQueueNode<T> last_node = this.get_last(this.root, 1, dummy_bit);
   ints_2 newdummy_bit = false;
   if(dummy_bit == true) {

      this.root.id = 1;
      this.size = this.size - 1;
      if(this.size > 0) {
         if(this.size > 0 && dummy_bit == true)
            newdummy_bit = true;
         else newdummy_bit = false;

         this.root.pos = last_node.left.pos;
        //   ret = last_node.keyvalue;
         PriorityQueueNode<T> root_node = this.poram.poram_retrieve(this.root.id, this.root.pos);
         ret = root_node.keyvalue;
         root_node.keyvalue = last_node.keyvalue;
         this.root.pos = this.heapify(this.root.id, root_node, 1, newdummy_bit);
      }
      else {
         ret = last_node.keyvalue;
         this.root.pos = RND(32);
      }
   }
   return ret;
}

//top_node is RARed, heapify the rest and put back top_node;
intr_32 PriorityQueue<T>.heapify(ints_32 top_id, PriorityQueueNode<T> top_node, intp_32 level, ints_2 dummy_bit) {
   intr_32 ret;
   ints_2 newdummy_bit = false;
   ints_2 go_left = false;
   intr_32 child_iter;
   ints_32 id_to_use;
   intr_32 pos_to_use;
   PriorityQueueNode<T> node_to_use;
   if(level < logN) {
      if(dummy_bit == true && top_id <= this.size) {
         if(top_id <= this.size >>1) {
            PriorityQueueNode<T> left_node, right_node;
            left_node = this.poram.poram_retrieve(top_node.left.id, top_node.left.pos);
            right_node = this.poram.poram_retrieve(top_node.right.id, top_node.right.pos);
            if(left_node.keyvalue.key > right_node.keyvalue.key) {
               if(left_node.keyvalue.key > top_node.keyvalue.key) {
                  KeyValue<T> tmp_kv = top_node.keyvalue;
                  top_node.keyvalue = left_node.keyvalue;
                  left_node.keyvalue = tmp_kv;
                  go_left = true;
               }
            } else {
               if(right_node.keyvalue.key > top_node.keyvalue.key) {
                  tmp_kv = top_node.keyvalue;
                  top_node.keyvalue = right_node.keyvalue;
                  right_node.keyvalue = tmp_kv;
                  go_left = false;
               }
            }
            if(go_left == true) {
               node_to_use = left_node;
               id_to_use = top_node.left.id;
            }
            else {
               node_to_use = right_node;
               id_to_use = top_node.right.id;
            }
            if(dummy_bit == true && (top_id <= this.size>>1))
               newdummy_bit = true;
            else newdummy_bit = false;
            child_iter = this.heapify(id_to_use, node_to_use, level+1, newdummy_bit);
            id_to_use = top_node.left.id;
            node_to_use = left_node;
            if(go_left == true) {
               top_node.left.pos = child_iter;
               top_node.right.pos = RND(32);
               id_to_use = top_node.right.id;
               pos_to_use = top_node.right.pos;
               node_to_use = right_node;
            } else {
               top_node.right.pos = child_iter;
               top_node.left.pos = RND(32);
               pos_to_use = top_node.left.pos;
            }
            this.poram.poram_write(id_to_use, pos_to_use, node_to_use);
         }
         if(top_id <= this.size) {
            ret = RND(32);
            this.poram.poram_write(top_id, ret, top_node);
         }
      }
   }
   return ret;
}

//return ret: ret.left is new top;
PriorityQueueNode<T> PriorityQueue<T>.get_last(NodeId top, intp_32 level, ints_2 dummy_bit) {
   PriorityQueueNode<T> ret;
   ints_2 newdummy_bit = false;
   if(level < logN) {
      if(dummy_bit == true && top.id <= this.size) {
         PriorityQueueNode<T> node = this.poram.poram_retrieve(top.id, top.pos);
         if(top.id == this.size) {
            ret = node;
         } else {
            PriorityQueueNode<T> child;
            ints_2 go_left = this.should_go_left(this.size, level);
            NodeId next = node.right;
            if(go_left == true) {
               next = node.left;
            }

            if(dummy_bit == true && top.id < this.size)
               newdummy_bit = true;
            else newdummy_bit = false;
            child = this.get_last(next, level+1, newdummy_bit);
            if(go_left == true) node.left.pos = child.left.pos;
            else node.right.pos = child.left.pos;
            top.pos = RND(32);
            this.poram.poram_write(top.id, top.pos, node);
            child.left.pos = top.pos;
            ret = child;
         }
      }
   }
   return ret;
}

void PriorityQueue<T>.insert(KeyValue<T> kv, ints_2 dummy_bit) {
   KeyValue<T> parent_kv;
   parent_kv.key = 1000;
   if(dummy_bit == true) {
      this.size = this.size + 1;
      this.root.pos = this.insert_internal(kv, parent_kv, this.root.id, this.root.pos, 1, dummy_bit);
   }
}

intr_32 PriorityQueue<T>.insert_internal(KeyValue<T> kv, KeyValue<T> parent_kv,
      ints_32 iter_id, intr_32 iter_pos, intp_32 level, ints_2 dummy_bit) {
   intr_32 ret;
   ints_2 newdummy_bit = false;
   if(level < logN) {
      if(dummy_bit == true && iter_id <= this.size) {
         PriorityQueueNode<T> node;
         if(iter_id == this.size) {//reached leaf
            node.keyvalue = kv;
            if(parent_kv.key < kv.key)
               node.keyvalue = parent_kv;
         }
         else {
            node = this.poram.poram_retrieve(iter_id, iter_pos);
            ints_2 go_left = this.should_go_left(this.size, level);
            NodeId next = node.right;
            if(go_left == true) {
               next = node.left;
            }

            if(dummy_bit == true && iter_id < this.size)
               newdummy_bit = true;
            else newdummy_bit = false;
            intr_32 newId = this.insert_internal(kv, node.keyvalue, next.id, next.pos, level+1, newdummy_bit);
            if(node.keyvalue.key < kv.key) {
               node.keyvalue = kv;
               if(kv.key > parent_kv.key) {
                  node.keyvalue = parent_kv;
               }
            }
            if(go_left == true) node.left.pos = newId;
            else node.right.pos = newId;
         }
         node.left.id = iter_id<<1;
         node.right.id = node.left.id +1;
         ret = RND(32);
         this.poram.poram_write(iter_id, ret, node);
      }
   }
   return ret;
}
