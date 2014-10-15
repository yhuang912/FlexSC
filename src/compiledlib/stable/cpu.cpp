#define PRINT 0
#define OP_ADDIU 9
#define OP_JAL 3
#define OP_ADDU 33
#define OP_JR 8
#define OP_SLT 42
#define OP_BNE 5
#define OP_ADD 20
#define OP_BEQ 4
#define OP_ANDI 12
#define OP_XOR 38
#define OP_BAL 1
#define OP_SUBU 35

struct CPU{};

int32 CPU.function(secure int32[32] reg, secure int32 inst, secure int32 pc) {
	int32 i = 0;
	int32 op = inst >> 26;
	int32 rt = ((inst << 11)>>27);
	int32 rs = ((inst << 6) >> 27);
	int32 rd = ((inst << 16)>>27);
	int32 unsignExt = ((inst << 16)>>16);
	int32 zeroExt = unsignExt;
	int32 funct = 0;

   int32[64] b;
   for(int i = 0; i < 32; ++i)
      b[i] = reg[i];
	if (unsignExt >> 15 != 0)
		unsignExt = unsignExt + 0xffff0000;

	//printf("op: %d, inst: %d", op, ((inst << 26)>>26));

	if (op == OP_ADDIU) {
	  reg[rt] = reg[rs] + (unsignExt);
	} else if (op == OP_JAL || op == OP_BAL) {
	  reg[31] = pc + 8;
	} else if (op == OP_ANDI) {
	  reg[rt] = reg[rs] & zeroExt;
	} else if (op == 0) {
		funct = (inst << 26) >> 26;
 		if (funct == OP_ADDU) {
	    	reg[rd] = reg[rs] + reg[rt];
	    } else if (funct == OP_XOR) {
	    	reg[rd] = reg[rs] ^ reg[rt];
	    } else if (funct == OP_SLT) {
	    	if (reg[rs] < reg[rt]) reg[rd] = 1;
	    	else reg[rd] = 0;
	    } else if (funct == OP_SUBU) {
	    	reg[rd] = reg[rs] - reg[rt];
	    }
	}
	
	// then process pc 
	
	if (op == OP_JR) {
	    pc = reg[rs];
	} else if (op == 3) { // OP_JAL
		pc = (inst << 6) >> 6;
	} else if ((op == 5 && reg[rs] != reg[rt]) || (op == 4 && reg[rs] == reg[rt])) {
		pc = pc + (unsignExt << 2);
	} else
		pc = pc + 4;

	return pc;
	
}
