#define PUSH 0
#define POP 1

typedef ints_ = secure int;
typedef intp_ = public int;
typedef intr_ = rnd;
typedef PORAM@m = native CircuitOram;
intr_@m RND(intp_32 m) = native lib.randBools;
dummy IntStackNode@m PORAM@m@n.poram_retrieve(int@m id, rnd@m pos) = native conditionalReadAndRemove;
dummy void PORAM@m.poram_write(int@m id, int@m pos, IntStackNode@m node) = native conditionalPutBack;

struct BoolArray{int32 data;};
struct IntStackNode@m {
   intr_@m next;
   int@m data;
};

struct IntStack@m {
   intr_@m root;
   ints_@m size;
   PORAM@m poram;
};

dummy int@m IntStack@m.stack_op(int@m operand, int1 op, int1 dum) {
   int@m ret;
   if (op == POP && dum == 0) { // extract root
      IntStackNode@m r = this.poram.poram_retrieve(this.size, this.root);
      this.root = r.next;
      this.size = this.size - 1;
      ret = r.data;
   } else if (dum == 0) { // push operand onto the stack
      IntStackNode@m node = 
         IntStackNode@m ( next = this.root,
               data = operand);
      this.root = RND(m);
      this.size  = this.size + 1;
      this.poram.poram_write(this.size, this.root, node);
   }
   return ret;
}

typedef AORAM@m<K, V> = native CircuitOram;
dummy AVLNode@m<K, V> AORAM@m<K, V>.aoram_retrieve(int@m id, rnd@m pos) = native conditionalReadAndRemove;
dummy void AORAM@m<K, V>.aoram_write(int@m id, int@m pos, AVLNode@m<K, V> node) = native conditionalPutBack;

struct Int@m {
	ints_@m val;
};

struct AVLId@m {
	ints_@m id;
	rnd@m pos;
};

struct AVLNode@m<K, V> {
	K key;
	V value;
	AVLId@m left, right;
	int@(m+1) lDepth, rDepth;
};

struct AVLTree@m<K, V> {
	 ints_@m total;
	 AVLId@m root;
	 AORAM<K, V> poram;
	 IntStack@m IDs;
};

void AVLTree@m<K, V>.init() {
	this.total = 0;
	this.root = AVLId@m(id = 0, pos = RND(m));
	for(public int@m i = 1; i < 1<<m; i = i + 1) {
		this.IDs.stack_op(i, PUSH);
	} 
}

void AVLTree@m<K, V>.insert(K key, V value, int2 cmp(K, K)) {
	AVLNode@m<K, V> tnodes, p;
	AVLId@m now = this.root, go, a, b, c;
	AVLId@m[public 3*m/2] left;
	K[public 3*m/2] keys;
	V[public 3*m/2] values;
	int@(m+1)[public 3*m/2] depth;
	int2[public 3*m/2] ids, cres;
	int2 ind = 0;
	int@m id;
	rnd@m pos;
	int@m hpos;
		
	K k, kp; V v, vp;
	int@(m+1) dl, dr;
	AVLId@m lf, rf;
	
	id, pos = now;
	if(id == 0) ind = 0;
	now = AVLId@m(id = id, pos = pos);
	
	K k;
	for(intp_32 i = 0; i<3*m/2; i = i + 1) {
		ids[i] = ind;
		if(ind == 0) {
			id, pos = now;
			tnodes = this.poram.aoram_retrieve(id, pos);
			this.IDs.stack_op(id, PUSH);
			keys[i], values[i], ld, rd, lf, rf = 
				tnodes.(key, value, lDepth, rDepth, left, right);
		} else if (ind == 1) {
			keys[i] = key;
			values[i] = value;
			depth[i] = 0-1;
			left[i] = AVLId@m(id = 0, pos = RND(m));
			ind = 2;
		} else {
			depth[i] = 0-1;
			keys[i] = keys[i-1];
		}
		cres[i] = cmp(keys[i], key);
		if(ind == 0) {
			if(cres[i] > 0) {
				depth[i] = dr;
				go = lf;
				left[i] = rf;
			} else {
				depth[i] = dl;
				go = rf;
				left[i] = lf;
			}
			now = go;
			if(now.id == 0) {
				ind = 1;
			}
		}
	}
	
	int@(m+1) nowDepth = 0-1;
	int@(m+1) ld, rd, dc, dap, dbp;
	AVLId@m nid = AVLId@m(id = 0, pos = RND(m)), next, ll, rr, l;
	for(i = 3*m/2 - 1; i>=0; i = i - 1) {
		if(ids[i] == 0) {
			id = this.IDs.stack_op(0, POP);
			pos = RND(m);
			int@m hpos = pos;
			next = AVLId@m(id = id, pos = pos);
			if(cres[i] >= 0) {
				ld = nowDepth;
				rd = depth[i];
				ll = nid;
				rr = left[i];
			} else {
				ld = depth[i];
				rd = nowDepth;
				ll = left[i];
				rr = nid;
			}
			nid = next;
			this.poram.aoram_write(id, pos,
				AVLNode@m{K, V}(key = keys[i], value = values[i],
					left = ll, right = rr,
					lDepth = ld, rDepth = rd));
			if (depth[i] > nowDepth) {
				nowDepth = depth[i];
			}
			nowDepth = nowDepth + 1;
			if(rd - ld >= 2) {
				id, pos = nid;
				tnodes = this.poram.aoram_retrieve(id, pos);
				this.IDs.stack_op(id, PUSH);
				k, v, dc, c, l = 
					tnodes.(key, value, lDepth, left, right);
					
				id, pos = l;
				p = this.poram.aoram_retrieve(id, pos);
				this.IDs.stack_op(id, PUSH);
				kp, vp, dap, dbp, a, b = 
					p.(key, value, lDepth, rDepth, left, right);

				id = this.IDs.stack_op(0, POP);
				pos = RND(m);
				int@m hpos = pos;
				this.poram.aoram_write(id, pos,
					AVLNode@m{K, V}(key = k, value = v,
						left = c, right = a,
						lDepth = dc, rDepth = dap));
				int@(m+1) dp = dc;
				if(dap > dp) dp = dap;
				dp = dp + 1;
				next = AVLId@m(id = id, pos = pos);
				
				id = this.IDs.stack_op(0, POP);
				pos = RND(m);
				int@m hpos = pos;
				this.poram.aoram_write(id, pos,
					AVLNode@m{K, V}(key = kp, value = vp,
						left = next, right = b,
						lDepth = dp, rDepth = dbp));
				nid = AVLId@m(id = id, id = pos);
				nowDepth = dp;
			} else if (ld - rd >= 2) {
				id, pos = nid;
				tnodes = this.poram.aoram_retrieve(id, pos);
				this.IDs.stack_op(id, PUSH);
				k, v, dc, c, l = 
					tnodes.(key, value, rDepth, right, left);
					
				id, pos = l;
				p = this.poram.aoram_retrieve(id, pos);
				this.IDs.stack_op(id, PUSH);
				kp, vp, dap, dbp, a, b = 
					p.(key, value, lDepth, rDepth, left, right);

				id = this.IDs.stack_op(0, POP);
				pos = RND(m);
				int@m hpos = pos;
				this.poram.aoram_write(id, pos,
					AVLNode@m{K, V}(key = k, value = v,
						left = b, right = c,
						lDepth = dbp, rDepth = dc));
				dp = dc;
				if(dbp > dp) dp = dbp;
				dp = dp + 1;
				next = AVLId@m(id = id, pos = pos);
				
				id = this.IDs.stack_op(0, POP);
				pos = RND(m);
				int@m hpos = pos;
				this.poram.aoram_write(id, pos,
					AVLNode@m{K, V}(key = kp, value = vp,
						left = a, right = next,
						lDepth = dap, rDepth = dp));
				nid = AVLId@m(id = id, pos = pos);
				nowDepth = dp;
			}
		}
	}
	this.root = nid;
}


V AVLTree@m<K, V>.search(K key, V value, int2 cmp(K, K)) {
	AVLNode@m<K, V> tnodes, p;
	AVLId@m now = this.root, go, a, b, c;
	AVLId@m[public 3*m/2] left;
	K[public 3*m/2] keys;
	V[public 3*m/2] values;
	int@(m+1)[public 3*m/2] depth;
	int2[public 3*m/2] ids, cres;
	int2 ind = 0;
	int@m id;
	rnd@m pos;
	
	K k, kp; V v, vp;
	int@(m+1) dl, dr;
	AVLId@m lf, rf, go;
	
	V ret;
	
	for(intp_32 i = 0; i<3*m/2; i = i + 1) {
		ids[i] = ind;
		if(ind == 0) {
			id, pos = now;
			tnodes = this.poram.aoram_retrieve(id, pos);
			this.IDs.stack_op(id, PUSH);
			keys[i], values[i], ld, rd, lf, rf = 
				tnodes.(key, value, lDepth, rDepth, left, right);
		} else {
			depth[i] = 0-1;
			keys[i] = keys[i-1];
		}
		cres[i] = cmp(keys[i], key);
		if(ind == 0) {
			if(cres[i] > 0) {
				depth[i] = dr;
				go = lf;
				left[i] = rf;
			} else {
				depth[i] = dl;
				go = rf;
				left[i] = lf;
			}
			if(cres[i] == 0) {
				ret = value;
				ind = 1;
			}
			now = go;
			if(now.id == 0)
				ind = 1;
		}
	}
	
	int@(m+1) nowDepth = 0-1;
	int@(m+1) ld, rd;
	AVLId@m nid = now, next, ll, rr;
	for(i = 3*m/2 - 1; i>=0; i = i - 1) {
		if(ids[i] == 0) {
			id = this.IDs.stack_op(0, POP);
			pos = RND(m);
			int@m hpos = pos;
			next = AVLId@m(id = id, pos = pos);
			if(cres[i] >= 0) {
				ld = nowDepth;
				rd = depth[i];
				ll = nid;
				rr = left[i];
			} else {
				ld = depth[i];
				rd = nowDepth;
				ll = left[i];
				rr = nid;
			}
			nid = next;
			this.poram.aoram_write(id, pos,
				AVLNode@m{K, V}(key = keys[i], value = values[i],
					left = ll, right = rr,
					lDepth = ld, rDepth = rd));
			if (depth[i] > nowDepth) {
				nowDepth = depth[i];
			}
			nowDepth = nowDepth + 1;
		}
	}
	this.root = nid;
	return ret;
}

