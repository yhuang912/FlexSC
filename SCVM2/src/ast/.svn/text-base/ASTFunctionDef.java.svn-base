package ast;

import java.util.ArrayList;
import java.util.List;

import util.Pair;
import ast.stmt.ASTStatement;
import ast.type.ASTType;

public class ASTFunctionDef extends ASTFunction {
	
	public ASTFunctionDef(boolean isDummy, String name, ASTType returnType, ASTType baseType,
			List<String> bitParameter,
			List<String> typeVariables,
			List<Pair<ASTType, String>> inputs) {
		super(isDummy, name, returnType, baseType, bitParameter, typeVariables, inputs);
		this.body = null;
		this.localVariables = new ArrayList<Pair<ASTType, String>>();
	}

	public List<Pair<ASTType, String>> localVariables;
	public List<ASTStatement> body;
	
	
	public String toString(int indent) {
		return toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.returnType.toString()+" "+name);
		for(String bit : this.bitParameter) {
			sb.append("@"+bit);
		}
		sb.append("(");
		boolean flag = true;
		for(int i=0; i<inputVariables.size(); ++i) {
			if(!flag) sb.append(", ");
			else flag = false;
			sb.append(inputVariables.get(i).left+" "+inputVariables.get(i).right);
		}
		sb.append(") {\n");
		for(int i=0; i<localVariables.size(); ++i) {
			sb.append(this.indent(1));
			sb.append(localVariables.get(i).left+" "+localVariables.get(i).right+";\n");
		}
		for(int i=0; i<body.size(); ++i) {
			sb.append(body.get(i).toString(1));
		}
		sb.append("}\n");
		
		return sb.toString();
	}
}
