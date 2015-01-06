package com.appcomsci.mips.cpu;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import com.appcomsci.mips.memory.MipsInstructionSet;

public class CpuBuilder {
	
	private static boolean emitActions(StringBuilder sb, boolean codeWritten, Set<String>operations, MipsInstructionSet.OperationType type) {
		if(operations.size() == 0)
			return codeWritten;
		
		String varName = null;
		switch(type) {
		case I:
		case J:
		case MR:
		case MW:
			varName = "op";
			break;
		case FUNCT:
			varName = "funct";
			break;
		case REGIMM:
			varName = "rt";	// The regimm bits live here
			break;
		default:
			varName = "?";
			break;
		}

		sb.append("\t");
		if(codeWritten)
			sb.append("else ");
		sb.append("if(op_type == ");
		switch(type) {
		case I:
			sb.append("OP_CODE_I");
			break;
		case FUNCT:
			sb.append("OP_CODE_R");
			break;
		case REGIMM:
			sb.append("OP_CODE_REGIMM");
			break;
		case J:
			sb.append("OP_CODE_J");
			break;
		default:
			sb.append("?");
		}
		sb.append(") {");
		sb.append(lineSeparator);

		boolean actionsWritten = false;

		// Output actions in order give in input file
		for(Map.Entry<String, List<String>>e: actions) {
			String op = e.getKey();
			if(operations.contains(op)) {
				sb.append("\t\t");
				if(actionsWritten)
					sb.append("else ");
				actionsWritten = true;
				sb.append("if(");
				sb.append(varName);
				sb.append(" == OP_");
				sb.append(op);
				sb.append(") {");
				sb.append(lineSeparator);
				for(String x:e.getValue()) {
					sb.append("\t\t");
					sb.append(x);
					sb.append(lineSeparator);
				}
				sb.append("\t\t}");
				sb.append(lineSeparator);
			}
		}
		switch(type) {
		case I:
			sb.append("\t\treg[rt] = reg_rt;");
			sb.append(lineSeparator);
			break;
		case FUNCT:
			sb.append("\t\treg[rd] = reg_rd;");
			sb.append(lineSeparator);
			break;
		}
		sb.append("\t}");
		sb.append(lineSeparator);
		return true;
	}

	public static String build(Set<MipsInstructionSet.Operation>operations) {
		
		StringBuilder sb = new StringBuilder();

		boolean needMult = false;
		Set<String> I_ops = new HashSet<String>();
		Set<String> R_ops = new HashSet<String>();
		Set<String> REGIMM_ops = new HashSet<String>();
		Set<String> J_ops = new HashSet<String>();
		for(MipsInstructionSet.Operation o : operations) {
			switch(o.getType()) {
			case I:
			case MR:	// Lump these in with I's
			case MW:
				I_ops.add(o.toString());
				break;
			case FUNCT:
				R_ops.add(o.toString());
				break;
			case REGIMM:
				REGIMM_ops.add(o.toString());
				break;
			case J:
				J_ops.add(o.toString());
			}
			switch(o) {
			case MFHI:
			case MTHI:
			case MFLO:
			case MTLO:
			case MULT:
			case MULTU:
			case DIV:
			case DIVU:
				needMult = true;
				break;
			default:		// Shut compiler up
				break;
			}
		}
		for(String s:text) {
			if(s.startsWith("%CHECK_TYPE")) {
				if(I_ops.size() > 0) {
					sb.append("\telse if(");
					sb.append(lineSeparator);
					sb.append("\t\t");
					boolean codeWritten = false;
					for(String o: I_ops) {
						if(codeWritten)
							sb.append(" || ");
						codeWritten = true;
						sb.append("op == OP_");
						sb.append(o.toString());
					}
					sb.append(lineSeparator);
					sb.append("\t)");
					sb.append(lineSeparator);
					sb.append("\t\tret = OP_CODE_I;");
					sb.append(lineSeparator);
				}
				if(J_ops.size() > 0) {
					sb.append("\telse if(");
					sb.append(lineSeparator);
					sb.append("\t\t");
					boolean codeWritten = false;
					for(String o: J_ops) {
						if(codeWritten)
							sb.append(" || ");
						codeWritten = true;
						sb.append("op == OP_");
						sb.append(o.toString());
					}
					sb.append(lineSeparator);
					sb.append("\t)");
					sb.append(lineSeparator);
					sb.append("\t\tret = OP_CODE_J;");
					sb.append(lineSeparator);
					
				}
			} else if(s.startsWith("%HILO_REG")) {
				if(needMult)
					sb.append("\t, secure int32[2] hiLo");
			} else if(s.startsWith("%ACTIONS")) {
				boolean codeWritten = false;
				codeWritten = emitActions(sb, codeWritten, I_ops, MipsInstructionSet.OperationType.I);
				codeWritten = emitActions(sb, codeWritten, J_ops, MipsInstructionSet.OperationType.J);
				codeWritten = emitActions(sb, codeWritten, R_ops, MipsInstructionSet.OperationType.FUNCT);
				emitActions(sb, codeWritten, REGIMM_ops, MipsInstructionSet.OperationType.REGIMM);
			} else {
				sb.append(s);
				sb.append(lineSeparator);
			}
		}
		return sb.toString();
	}

	static {
		setup();
	}
	
	static String lineSeparator = System.getProperty("line.separator");
	static List<String> text;
	static List<Map.Entry<String, List<String>>> actions;
	static void setup() {
			lineSeparator = "\n";
		
		File f = new File("cpu.txt");
		if(!f.exists()) {
			System.err.println("No cpu.txt");
			return;
		}
		List<String> rawLines = null;
		try {
			rawLines = Files.readAllLines(f.toPath(),StandardCharsets.US_ASCII);
		} catch(IOException e) {
			System.err.println("IO Exception: " + e);
			return;
		}
		text = new ArrayList<String>();
		actions = new ArrayList<Map.Entry<String, List<String>>>();
		Set<String>mnemonicSet = new HashSet<String>();
		List<String> current = text;
		for(String s:rawLines) {
			if(s.startsWith("%OP_")) {
				// Need to check for duplicates!
				String mnemonic = s.substring(4);
				if(mnemonicSet.contains(mnemonic)) {
					System.err.println("Warning: duplicate actions for " + mnemonic);
				}
				current = new ArrayList<String>();
				actions.add(new AbstractMap.SimpleImmutableEntry<String, List<String>>(mnemonic, current));
			} else {
				current.add(s);
			}
		}
	}
	public static void main(String args[]) {
		Set<MipsInstructionSet.Operation> s = new HashSet<MipsInstructionSet.Operation>();
		for(MipsInstructionSet.Operation i: MipsInstructionSet.Operation.values())
			s.add(i);
		String code = build(s);
		System.out.print(code);
	}
}
