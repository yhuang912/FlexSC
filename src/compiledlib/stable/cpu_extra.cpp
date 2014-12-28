//#include <cbmc-gc.h>
#define true 1
#define false 0
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
int1 CPU.checkTerminate(secure int32 inst) {
   secure int1 res = false;
   if(inst == 0x100007C0)
      res = true;
   return res;
}
int32 CPU.function(secure int32[32] reg, secure int32 inst, secure int32 pc) {
   int32 op = inst >> 26;
   int32 rt = ((inst << 11)>>27);
   int32 rs = ((inst << 6) >> 27);
   int32 rd = ((inst << 16)>>27);
   int32 reg_rs, reg_rt, reg_rd;
   int32 unsignExt = ((inst << 16)>>16);
   int32 zeroExt = unsignExt;
   int32 funct = 0;

   if (unsignExt >> 15 != 0)
      unsignExt = unsignExt + 0xffff0000;

   //printf("op: %d, inst: %d", op, ((inst << 26)>>26));

   reg_rs = reg[rs];
   reg_rt = reg[rt];
   if (op == OP_ADDIU) {
      reg[rt] = reg_rs + (unsignExt);
   } else if (op == OP_JAL ){
      reg[31] = pc + 8;
	  pc = ((inst << 6) >> 6);
   } else if ( op == OP_BAL) {
	   reg[31] = pc+ 8;
	   if (reg_rt >= 0)
	   	    pc = pc + (unsignExt << 2);
	   // Dnote: I think we don't want this line?  Is this an unconditional branch?
	   //pc = pc + 4;
   } else if (op == OP_ANDI) {
      reg[rt] = reg_rs & zeroExt;
   } else if (op == 0) {
      funct = (inst << 26) >> 26;
      if (funct == OP_ADDU) {
         reg_rd = reg_rs + reg_rt;
      } else if (funct == OP_XOR) {
         reg_rd = reg_rs ^ reg_rt;
      } else if (funct == OP_SLT) {
         if (reg_rs < reg_rt) reg_rd = 1;
         else reg_rd = 0;
      } else if (funct == OP_SUBU) {
         reg_rd = reg_rs - reg_rt;
      }
      reg[rd] = reg_rd;
   }

   // then process pc

   reg_rs = reg[rs];
   res_rt = reg[rt];
   if (op == 0 && funct == OP_JR) {
      pc = reg_rs;
   } else if (op == 3) { // OP_JAL
      pc = (inst << 6) >> 6;
   } else if ((op == 5 && reg_rs != reg_rt) || (op == 4 && reg_rs == reg_rt)) { //OP_BNE and OP_BEQ
      pc = pc + (unsignExt << 2);
   } else
      pc = pc + 4;

   return pc;
}
