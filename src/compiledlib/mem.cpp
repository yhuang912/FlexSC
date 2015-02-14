#define OP_LW 35
#define OP_SW 43
#define OP_LB 32
typedef OMap = native SecureMap;
int32 OMap.read(int32 id) = native read;
struct MEM{};

int32 MEM.getInst(OMap instBank, int32 pc, public int32 pcOffset){
	//int32 index = (pc-pcOffset) >> 2;
	int32 newInst = instBank.read(pc);
	return newInst;
}

void MEM.func(int32[32]reg,
      int32[2048]mem,
      int32 inst,
      public int32 dataOffset){

   //int32 index = (pc-pcOffset) >> 2;
   //int32 newInst = mem[index];

   int32 rt = (inst << 11)>>27;
   int32 rs = (inst << 6) >> 27;
   int32 unsignExt = ((inst << 16)>>16);
   if (unsignExt >> 15 == 1)
      unsignExt = unsignExt + 0xffff0000;
   int32 op = (inst >> 26);

   int32 tmpAddress = reg[rs] + unsignExt - dataOffset;
   int32 tmpindex = (tmpAddress)>>2;
   int32 mem_tmp_r = mem[tmpindex];
   int32 reg_rt_r = reg[rt];
   int32 mem_tmp_w = mem_tmp_r;
   int32 reg_rt_w = reg_rt_r;
   if(op == OP_LW)
      reg_rt_w = mem_tmp_r;
   else if(op == OP_SW){
      mem_tmp_w = reg_rt_r;
   } else if(op == OP_LB){
	   int32 tempRT = mem_tmp_r;
	   int32 byteShiftTwo = ((tmpAddress << 30) >> 31);
	   int32 byteShiftOne = ((tmpAddress << 31) >> 31);
	   if (byteShiftTwo != 0 && byteShiftOne != 0)
		   tempRT = ((tempRT << 24) >> 24);
	   else if (byteShiftTwo != 0 && byteShiftOne == 0)
		   tempRT = ((tempRT << 16) >> 24);
	   else if (byteShiftTwo == 0 && byteShiftOne != 0)
	   		   tempRT = ((tempRT << 8) >> 24);
	   else if (byteShiftTwo == 0 && byteShiftOne == 0)
	   		   tempRT = (tempRT >> 24);
	   reg_rt_w = tempRT;
   }
   reg[rt] = reg_rt_w;
   mem[tmpindex] = mem_tmp_w;

   //return newInst;
}
