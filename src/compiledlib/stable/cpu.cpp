//#include <cbmc-gc.h>
#define OP_CODE_R 1
#define OP_CODE_I 2
#define OP_CODE_OTHERS 3
#define true 1
#define false 0
#define PRINT 0

// OP_CODE_I
#define OP_ADDIU 9
#define OP_ANDI 12
#define OP_LUI 15

#define OP_BNE 5
#define OP_BEQ 4
#define OP_BAL 1
#define OP_JAL 3

//OP_CODE_R
#define FUNCT_SLL 0
#define FUNCT_SRL 2
#define FUNCT_SLLV 6
#define FUNCT_JR 8
#define FUNCT_JALR 9
#define FUNCT_ADD 20
#define FUNCT_ADDU 33
#define FUNCT_SUBU 35
#define FUNCT_OR 37
#define FUNCT_XOR 38
#define FUNCT_SLT 42

struct CPU{};
int32 SLL(int32 m, int32 n) = native intLib.leftPrivateShift;
int32 SRL(int32 m, int32 n) = native intLib.rightPrivateShift;


int2 CPU.checkType(int32 opcode) {
   int2 ret;
   if (opcode == 0)
      ret = OP_CODE_R;
   else if (opcode ==  0x09|| opcode == 0x0C || opcode == 0x0f)
      ret = OP_CODE_I;
   else ret = OP_CODE_OTHERS;
   return ret;
}
int32 CPU.function(secure int32[32] reg, secure int32 inst, secure int32 pc) {
   int32 op = inst >> 26;
   int32 rt = ((inst << 11)>>27);
   int32 rs = ((inst << 6) >> 27);
   int32 rd = ((inst << 16)>>27);
   int32 shamt = ((inst << 21)>>27);
   int32 unsignExt = ((inst << 16)>>16);
   int32 zeroExt = unsignExt;
   int32 funct = (inst << 26) >> 26;

   if (unsignExt >> 15 != 0)
      unsignExt = unsignExt + 0xffff0000;

   //printf("op: %d, inst: %d", op, ((inst << 26)>>26));

   int32 reg_rs_r = reg[rs];
   int32 reg_rt_r = reg[rt];
   int32 reg_rd_w = reg[rd];
   int32 reg_rt_w = reg_rt_r;

   int2 op_type = this.checkType(op);
   if(op_type == OP_CODE_I) {
      if (op == OP_ADDIU) {
         reg_rt_w = reg_rs_r + (unsignExt);
      } else if (op == OP_ANDI) {
         reg_rt_w = reg_rs_r & zeroExt;
      } else if (op == OP_LUI) {
	reg_rt_w = (zeroExt << 16);
      }
   }
   else if (op_type == OP_CODE_R) {//R type
      if (funct == FUNCT_ADDU) {
         reg_rd_w = reg_rs_r + reg_rt_r;
      } else if (funct == FUNCT_XOR) {
         reg_rd_w = reg_rs_r ^ reg_rt_r;
      } else if (funct == FUNCT_SLT) {
         if (reg_rs_r < reg_rt_r) reg_rd_w = 1;
         else reg_rd_w = 0;
      } else if (funct == FUNCT_SUBU) {
         reg_rd_w = reg_rs_r - reg_rt_r;
} else if (funct == FUNCT_SRL){
      reg_rd_w = SRL(reg_rt_r, shamt);//(reg_rt >> shamt);    
    } else if (funct == FUNCT_SLL){
      reg_rd_w = SLL(reg_rt_r, shamt);//(reg_rt << shamt);    
      } else if (funct == FUNCT_OR){
         reg_rd_w = (reg_rt_r | reg_rs_r);    
      }       
   }
   else {
//      int32 reg_31 = reg[31];
int32 oldPC = pc;
      if (op == OP_JAL ){
//         reg_31 = pc + 8;
         pc = ((inst << 6) >> 6);
	 
      } 
      else if ( op == OP_BAL) {
//         reg_31 = pc+ 8;
         if (reg_rt_r >= 0)
            pc = pc + (unsignExt << 2);
         // Dnote: I think we don't want this line?  Is this an unconditional branch?
         //pc = pc + 4;
      }

      if(op == OP_JAL || op == OP_BAL)
         reg[31] = oldPC + 8;
   }
reg[rd] = reg_rd_w;
reg[rt] = reg_rt_w;

   // then process pc
   if (op == 0 && funct == FUNCT_JR) {
      pc = reg_rs_r;
   } else if (op == 3) { // OP_JAL
      pc = (inst << 6) >> 6;
   } else if ((op == OP_BNE && reg_rs_r != reg_rt_r) || (op == OP_BEQ && reg_rs_r == reg_rt_r)) { //OP_BNE and OP_BEQ
      pc = pc + 4 +(unsignExt << 2);
   } else
      pc = pc + 4;

   return pc;
}
