package com.appcomsci.mips.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appcomsci.mips.memory.MipsInstructionSet;

public class CpuBuilder {
	private static final String CPU_FILE_NAME = "cpu.txt";

	private static final String lineSeparator = System.getProperty("line.separator");
	private static final String fileSeparator = System.getProperty("file.separator");
	
	/** The main text of the CPU program */
	private List<String> text;
	/** The main text of the wrapper */
	private List<String> wrapper;
	private List<Map.Entry<String, List<String>>> actions;
	
	/** This constructor takes the CPU template from the classpath
	 * @throws IOException
	 */
	public CpuBuilder() throws IOException {
		// Look for cpu.txt inside jar file (we hope)
		InputStream is = getClass().getResourceAsStream(CPU_FILE_NAME);
		if(is == null) // Did it get slightly mislaid?
			is = getClass().getResourceAsStream(fileSeparator+CPU_FILE_NAME);
		if(is == null) // Really mislaid.  Oops.
			throw new FileNotFoundException("Could not find " + CPU_FILE_NAME + " in classpath");
		setup(new InputStreamReader(is));
	}
	
	/**
	 * This constructor reads the CPU template from a Reader.
	 * Use InputStreamReader to bridge the gap from InputStreams
	 * @param rdr The reader containing the CPU template
	 * @throws IOException
	 */
	public CpuBuilder(Reader rdr) throws IOException {
		setup(rdr);
	}
	
	/**
	 * This constructor reads the CPU template from a File.
	 * @param f The file containing the CPU template.
	 * @throws IOException
	 */
	public CpuBuilder(File f) throws IOException {
		setup(Files.readAllLines(f.toPath(),StandardCharsets.US_ASCII));
	}
	
	/**
	 * Constructor setup
	 * @param rdr A reader 
	 * @throws IOException
	 */
	private void setup(Reader rdr) throws IOException {
		BufferedReader br = new BufferedReader(rdr);
		List<String> rawLines = new ArrayList<String>();
		String s;
		while((s=br.readLine()) != null)
			rawLines.add(s);
		setup(rawLines);
	}
	
	/**
	 * Constructor setup
	 * @param rawLines A list of lines from the CPU template file
	 */
	private void setup(List<String>rawLines) {
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
			} else if(s.startsWith("%WRAPPER")) {
				wrapper = new ArrayList<String>();
				current = wrapper;
			} else {
				current.add(s);
			}
		}
	}
	
	/**
	 * Generate actions for operations of type "type".  The actions are a large if statement of the form
	 * <br>
	 * if(op_type == some_type) {
	 * &nbsp;&nbsp;// Actions for type some_type
	 * <br>
	 * &nbsp;&nbsp;if(appropriate_var == something) {
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;actions
	 * <br>
	 * &nbsp;&nbsp;} else if(var == something_else) {
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;other actions
	 * <br>
	 * &nbsp;&nbsp;}
	 * <br>
	 * } else if(op_type == some_other_type) {
	 * &nbsp;&nbsp;// Actions for some_other_type
	 * <br>
	 * }
	 * <br>
	 * @param sb Emit code here
	 * @param codeWritten Has any code been written to the outer "if" yet?  Used to decide
	 * 				whether or not to add an "else"
	 * @param operations The set of operations to implement
	 * @param type The general class of operations (REGIMM, FUNCT, regular, memory read or write)
	 * @return False if no code has been written to the outer "if" yet.  True otherwise.
	 * 				This constitutes an updated value for codeWritten.
	 */
	private boolean emitActions(StringBuilder sb, boolean codeWritten, Set<String>operations, MipsInstructionSet.OperationType type) {
		if(operations.size() == 0)
			return codeWritten;
		
		String varName = null;
		// Decide which variable to check for the operation
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
		sb.append("\t}");
		sb.append(lineSeparator);
		return true;
	}
	
	public void buildWrapper(Set<MipsInstructionSet.Operation>operations, String className, File f) throws FileNotFoundException {
		PrintStream w = new PrintStream(f);
		buildWrapper(operations, className, w);
	}	
	
	public void buildWrapper(Set<MipsInstructionSet.Operation>operations, String className, PrintStream w) {
		StringBuilder sb = new StringBuilder();
		buildWrapper(operations, className, sb);
		w.print(sb.toString());
	}
	
	public void buildWrapper(Set<MipsInstructionSet.Operation>operations, String className, StringBuilder sb) {
		for(String s:wrapper) {
			if(s.startsWith("%OPCODES")) {
				// Write out list of operations
				for(MipsInstructionSet.Operation o : operations) {
					sb.append("\t\t\"");
					sb.append(o.toString());
					sb.append("\",");
					sb.append(lineSeparator);
				}
			} else if(s.contains("%CLASS")) {
				String parts[] = s.split("%CLASS");
				for(int i = 0; i < parts.length-1; i++) {
					sb.append(parts[i]);
					sb.append(className);
				}
				sb.append(parts[parts.length-1]);
				sb.append(lineSeparator);
			} else {
				sb.append(s);
				sb.append(lineSeparator);
			}
		}
	}
	
	/** Build a CPU
	 * 
	 * @param operations The set of operations to be implemented
	 * @param f Write the CPU program to this file
	 * @throws FileNotFoundException
	 */
	
	public void buildCpu(Set<MipsInstructionSet.Operation>operations, String className, File f) throws FileNotFoundException {
		PrintStream w = new PrintStream(f);
		buildCpu(operations, className, w);
	}
	
	/** Build a CPU
	 * 
	 * @param operations The set of operations to be implemented
	 * @param w Write the CPU program here
	 */
	
	public void buildCpu(Set<MipsInstructionSet.Operation>operations, String className, PrintStream w) {
		StringBuilder sb = new StringBuilder();
		buildCpu(operations, className, sb);
		w.print(sb.toString());
	}

	/**
	 * Build a CPU
	 * @param operations The set of operations to be implemented
	 * @return The text of the CPU
	 */
	public void buildCpu(Set<MipsInstructionSet.Operation>operations, String className, StringBuilder sb) {

		// Build sets of ops by type.
		// Also keep track of whether there were any multiplies or divides
		
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
			} else if(s.contains("%CLASS")) {
				String parts[] = s.split("%CLASS");
				for(int i = 0; i < parts.length-1; i++) {
					sb.append(parts[i]);
					sb.append(className);
				}
				sb.append(parts[parts.length-1]);
				sb.append(lineSeparator);
			} else {
				sb.append(s);
				sb.append(lineSeparator);
			}
		}
	}

	/** Main program for testing
	 * 
	 * @param args A list of operations to be implemented.  If empty, the complete set of operations
	 *		defined by MipsInstructionSet.Operation.values() will be implemented.
	 */
	public static void main(String args[]) {
		Set<MipsInstructionSet.Operation> operations = new HashSet<MipsInstructionSet.Operation>();
		if(args.length == 0) {
			// No args means do them all
			for(MipsInstructionSet.Operation op: MipsInstructionSet.Operation.values())
				operations.add(op);
		} else {
			for(String s:args) {
				MipsInstructionSet.Operation op = MipsInstructionSet.Operation.valueOf(s);
				if(op == null) {
					System.err.println("Invalid operation: " + s);
				} else {
					operations.add(op);
				}
			}
		}
		if(operations.size() == 0) {
			System.err.println("No valid operations, giving up");
			System.exit(1);
		}
		try {
			CpuBuilder bldr;
			/*
			File f = new File(CPU_FILE_NAME);
			if(!f.exists()) {
				System.err.println("No " + CPU_FILE_NAME);
				return;
			}
			bldr = new CpuBuilder(new InputStreamReader(new FileInputStream(f)));
			*/
			bldr = new CpuBuilder();
			StringBuilder code = new StringBuilder();
			bldr.buildCpu(operations, "Cpu", code);
			System.out.print(code.toString());
			
			StringBuilder wrapper = new StringBuilder();
			bldr.buildWrapper(operations, "Cpu", wrapper);
			System.out.print(wrapper.toString());
		} catch(FileNotFoundException e) {
			System.err.println("No " + CPU_FILE_NAME + " despite existence check");
		} catch(IOException e) {
			System.err.println("Error reading " + CPU_FILE_NAME + ": " + e);
		}
	}
}
