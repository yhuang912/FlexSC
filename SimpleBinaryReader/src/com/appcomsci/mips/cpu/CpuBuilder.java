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
	
	private static void emitActions(StringBuilder sb, Set<String>operations, String name) {
		if(operations.size() == 0)
			return;

		boolean codeWritten = sb.length() > 0;
		sb.append("\t");
		if(codeWritten)
			sb.append("else ");
		sb.append("if(op_type == ");
		sb.append(name);
		sb.append(") {\n");

		codeWritten = false;

		// Output actions in order give in input file
		for(Map.Entry<String, List<String>>e: actions) {
			String op = e.getKey();
			if(operations.contains(op)) {
				sb.append("\t\t");
				if(codeWritten)
					sb.append("else ");
				codeWritten = true;
				sb.append("if(op == OP_");
				sb.append(op);
				sb.append(") {\n");
				for(String x:e.getValue()) {
					sb.append("\t\t");
					sb.append(x);
					sb.append("\n");
				}
				sb.append("\t\t}\n");
			}
		}
		sb.append("\t}\n");
	}

	public static String build(Set<MipsInstructionSet.Operation>operations) {

		boolean needMult = false;
		Set<String> I_ops = new HashSet<String>();
		Set<String> R_ops = new HashSet<String>();
		Set<String> REGIMM_ops = new HashSet<String>();
		for(MipsInstructionSet.Operation o : operations) {
			switch(o.getType()) {
			case I:
				I_ops.add(o.toString());
				break;
			case R:
				R_ops.add(o.toString());
				break;
			case REGIMM:
				REGIMM_ops.add(o.toString());
				break;
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
			}
		}
		for(String s:text) {
			if(s.startsWith("%CHECK_TYPE")) {
				if(I_ops.size() > 0) {
					StringBuilder sb = new StringBuilder();
					System.out.println("\telse if(");
					for(String o: I_ops) {
						if(sb.length() > 0)
							sb.append(" || ");
						sb.append("op == OP_");
						sb.append(o.toString());
					}
					System.out.print("\t\t");
					System.out.println(sb.toString());
					System.out.println("\t)");
					System.out.println("\t\tret = OP_CODE_I;");
				}
			} else if(s.startsWith("%HILO_REG")) {
				if(needMult)
					System.out.println("\t, secure int32[2] hiLo");
			} else if(s.startsWith("%ACTIONS")) {
				StringBuilder sb = new StringBuilder();
				emitActions(sb, I_ops, "OP_CODE_I");
				emitActions(sb, R_ops, "OP_CODE_R");
				emitActions(sb, REGIMM_ops, "OP_CODE_REGIMM");
				System.out.print(sb.toString());
			} else {
				System.out.println(s);
			}
		}
		return null;
	}

	static {
		setup();
	}
	
	static List<String> text;
	// May not need the map
	static Map<String, List<String>> actionMap;
	static List<Map.Entry<String, List<String>>> actions;
	static void setup() {
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
		actionMap = new HashMap<String, List<String>>();
		actions = new ArrayList<Map.Entry<String, List<String>>>();
		List<String> current = text;
		for(String s:rawLines) {
			if(s.startsWith("%OP_")) {
				String mnemonic = s.substring(4);
				current = new ArrayList<String>();
				actionMap.put(mnemonic, current);
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
		build(s);
	}
}
