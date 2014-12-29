/**
 * 
 */
package com.appcomsci.mips.memory;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants and bit picking for the MIPS instruction set
 * 
 * Reference: MIPS32(TM) Architecture for Programmers
 *            Volume II: The MIPS32(TM) Instruction Set
 *            Document Number: MD00086
 *            Revision 2.00
 *            June 9, 2003
 *            
 * MIPS32 is a trademark of MIPS Technologies, Inc
 * 
 * @author Allen McIntosh
 *
 */
public class MipsInstructionSet {
	// Some opcodes, expressed as small integers
	
	public static final int OP_FUNCT  = 0x00;
	public static final int OP_REGIMM = 0x01;
	public static final int OP_J      = 0x02;
	public static final int OP_JAL    = 0x03; // Link
	public static final int OP_BEQ    = 0x04;
	public static final int OP_BNE    = 0x05;
	public static final int OP_BLEZ   = 0x06;
	public static final int OP_BGTZ   = 0x07;
	
	// Function codes
	public static final int OP_JR    = 0x08;
	public static final int OP_JALR  = 0x09;
	
	// IMM codes
	public static final int OP_BLTZ   = 0x00;
	public static final int OP_BGEZ   = 0x01;
	public static final int OP_BLTZAL = 0x10; // Link
	public static final int OP_BGEZAL = 0x11; // Link
	
	// Constants for bit picking functions
	
	public static final int OP_SHIFT = 26;
	public static final int OP_MASK = 0x3f;
	
	public static final int OP_REGIMM_CODE_SHIFT = 16;
	public static final int OP_REGIMM_CODE_MASK = 0x1f;
	
	public static final int SRC_REG_SHIFT = 20;
	public static final int SRC_REG_MASK  = 0x1f;
	
	public static final int FUNCT_SHIFT = 0;
	public static final int FUNCT_MASK = 0x3f;
	
	public static final int OFFSET_SHIFT = 0;
	public static final int OFFSET_MASK = 0xffff;
	
	public static final int INSTR_INDEX_SHIFT = 0;
	public static final int INSTR_INDEX_MASK = 0x03ffffff;
	
	public static final int RETURN_REG = 0x1e;
	
	/** Address presumed to be a branch to itself */
	public static final long SPIN_ADDRESS = 0x0;
	public static final long NOP = 0x0;
	
	/**
	 * Pull an op out of instruction as a small integer between 0 and 127 inclusive
	 * @param instruction The original instruction
	 * @return The op
	 */
	public static int getOp(long instruction) {
		return (int)((instruction>>OP_SHIFT)&OP_MASK);
	}
	
	/**
	 * Pull an op out of instruction as a small integer between 0 and 127 inclusive
	 * @param instruction The original instruction
	 * @return The op
	 */
	public static int getOp(int instruction) {
		return (instruction>>OP_SHIFT)&OP_MASK;
	}
	
	/**
	 * Pull the IMM code out of an instruction as a small integer between 0 and 31 inclusive
	 * @param instruction The original instruction
	 * @return The IMM code
	 */
	public static int getRegImmCode(long instruction) {
		return (int)((instruction>>OP_REGIMM_CODE_SHIFT)&OP_REGIMM_CODE_MASK);
	}
	
	/**
	 * Pull the IMM code out of an instruction as a small integer between 0 and 31 inclusive
	 * @param instruction The original instruction
	 * @return The IMM code
	 */
	public static int getRegImmCode(int instruction) {
		return (instruction>>OP_REGIMM_CODE_SHIFT)&OP_REGIMM_CODE_MASK;
	}
	
	/** Pull the function code out of an instruction as a small integer between 0 and 63 inclusive
	 * @param instruction The original instruction
	 * @return The function code
	 */
	public static int getFunct(long instruction) {
		return (int)((instruction>>FUNCT_SHIFT)&FUNCT_MASK);
	}
	
	/** Pull the function code out of an instruction as a small integer between 0 and 63 inclusive
	 * @param instruction The original instruction
	 * @return The function code
	 */
	public static int getFunct(int instruction) {
		return (int)((instruction>>FUNCT_SHIFT)&FUNCT_MASK);
	}
	
	public static int getSrcReg(long instruction) {
		return (int)((instruction>>SRC_REG_SHIFT)&SRC_REG_MASK);
	}
	
	public static int getSrcReg(int instruction) {
		return (instruction>>SRC_REG_SHIFT)&SRC_REG_MASK;
	}
	
	public static int getOffset(long instruction) {
		return (int)((instruction>>OFFSET_SHIFT)&OFFSET_MASK);
	}
	
	public static int getOffset(int instruction) {
		return (instruction>>OFFSET_SHIFT)&OFFSET_MASK;
	}
	
	public static int getInstrIndex(long instruction) {
		return (int)((instruction>>INSTR_INDEX_SHIFT)&INSTR_INDEX_MASK);
	}
	
	public static int getInstrIndex(int instruction) {
		return (instruction>>INSTR_INDEX_SHIFT)&INSTR_INDEX_MASK;
	}
	
	// An enum that covers the interesting MIPS instructions
	
	public enum Operation {
		FUNCT(0),
		REGIMM(1 << OP_SHIFT),
		J(2 << OP_SHIFT),
		JAL(3 << OP_SHIFT),
		BEQ(4 << OP_SHIFT),
		BNE(5 << OP_SHIFT),
		BLEZ(6 << OP_SHIFT),
		BGTZ(7 << OP_SHIFT),
		ADDI(8 << OP_SHIFT),
		ADDIU(9 << OP_SHIFT),
		SLTI(10 << OP_SHIFT),
		SLTIU(11 << OP_SHIFT),
		ANDI(12 << OP_SHIFT),
		ORI(13 << OP_SHIFT),
		XORI(14 << OP_SHIFT),
		LUI(15 << OP_SHIFT),
		BEQL(20 << OP_SHIFT),	// Obsolete
		BNEL(21 << OP_SHIFT),	// Obsolete
		BLEZL(22 << OP_SHIFT),	// Obsolete
		BGTZL(23 << OP_SHIFT),	// Obsolete
		LB(32 << OP_SHIFT),
		LH(33 << OP_SHIFT),
		LWL(34 << OP_SHIFT),
		LW(35 << OP_SHIFT),
		LBU(36 << OP_SHIFT),
		LHU(37 << OP_SHIFT),
		LWR(38 << OP_SHIFT),
		SB(40 << OP_SHIFT),
		SH(41 << OP_SHIFT),
		SWL(42 << OP_SHIFT),
		SW(43 << OP_SHIFT),
		SLL(0 | (OP_FUNCT<<OP_SHIFT)),
		SRL(2 | (OP_FUNCT<<OP_SHIFT)),
		SRA(3 | (OP_FUNCT<<OP_SHIFT)),
		SLLV(4 | (OP_FUNCT<<OP_SHIFT)),
		SRLV(6 | (OP_FUNCT<<OP_SHIFT)),
		SRAV(7 | (OP_FUNCT<<OP_SHIFT)),
		JR(8 | (OP_FUNCT<<OP_SHIFT)),
		JALR(9 | (OP_FUNCT<<OP_SHIFT)),
		MOVZ(10 | (OP_FUNCT<<OP_SHIFT)),
		MOVN(11 | (OP_FUNCT<<OP_SHIFT)),
		MULT(24 | (OP_FUNCT<<OP_SHIFT)),
		MULTU(25 | (OP_FUNCT<<OP_SHIFT)),
		DIV(26 | (OP_FUNCT<<OP_SHIFT)),
		DIVU(27 | (OP_FUNCT<<OP_SHIFT)),
		ADD(32 | (OP_FUNCT<<OP_SHIFT)),
		ADDU(33 | (OP_FUNCT<<OP_SHIFT)),
		SUB(34 | (OP_FUNCT<<OP_SHIFT)),
		SUBU(35 | (OP_FUNCT<<OP_SHIFT)),
		AND(36 | (OP_FUNCT<<OP_SHIFT)),
		OR(37 | (OP_FUNCT<<OP_SHIFT)),
		XOR(38 | (OP_FUNCT<<OP_SHIFT)),
		NOR(39 | (OP_FUNCT<<OP_SHIFT)),
		SLT(42 | (OP_FUNCT<<OP_SHIFT)),
		SLTU(43 | (OP_FUNCT<<OP_SHIFT)),
		BLTZ(0 | (OP_REGIMM<<OP_SHIFT)),
		BGEZ(1 | (OP_REGIMM<<OP_SHIFT)),
		;
		
		private static Map<Long, Operation> opMap;
		static {
			opMap = new HashMap<Long, Operation>();
			for(Operation o: values()) {
				opMap.put(o.value, o);
			};
		}
		
		public static Operation valueOf(long op) {
			return opMap.get(op);
		}
		
		private final long value;
		private Operation(long value) {
			this.value = value;
		}
	}
}
