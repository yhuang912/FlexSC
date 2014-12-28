/**
 * 
 */
package com.appcomsci.mips.memory;

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
	
	public static int getOp(long instruction) {
		return (int)((instruction>>OP_SHIFT)&OP_MASK);
	}
	
	public static int getOp(int instruction) {
		return (instruction>>OP_SHIFT)&OP_MASK;
	}
	
	public static int getRegImmCode(long instruction) {
		return (int)((instruction>>OP_REGIMM_CODE_SHIFT)&OP_REGIMM_CODE_MASK);
	}
	
	public static int getRegImmCode(int instruction) {
		return (instruction>>OP_REGIMM_CODE_SHIFT)&OP_REGIMM_CODE_MASK;
	}
	
	public static int getFunct(long instruction) {
		return (int)((instruction>>FUNCT_SHIFT)&FUNCT_MASK);
	}
	
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
}
