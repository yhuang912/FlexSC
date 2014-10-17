#define OP_LW 35
#define OP_SW 43
struct MEM{};

int32 MEM.function(int32[32]reg, int32[2048]mem, int32 pc, int32 inst, public int INPUT_A_pcOffset, public int INPUT_A_dataOffset){

   int32 index = (pc-INPUT_A_pcOffset) >> 2;
   int32 newInst = mem[index];

   int32 rt = (inst << 11)>>27;
   int32 rs = ((inst << 6) >> 27);
   int32 unsignExt = ((inst << 16)>>16);
   if (unsignExt >> 15 == 1)
      unsignExt = unsignExt + 0xffff0000;
   int32 op = (inst >> 26);

   if(op == OP_LW)
      reg[rt] = mem[(reg[rs] + unsignExt - INPUT_A_dataOffset)>>2];
   else if(op == OP_SW) 
      mem[(reg[rs] + unsignExt - INPUT_A_dataOffset)>>2] = reg[rt]; 
   return newInst;
}




