package ast;

import java.util.ArrayList;
import java.util.List;

import util.Pair;
import ast.type.ASTFunctionType;
import ast.type.ASTType;
import ast.type.ASTVariableType;

public abstract class ASTFunction extends AST {
	public List<String> bitParameter;
	public List<String> typeVariables;
	public List<Pair<ASTType, String>> inputVariables;
	public String name;
	public ASTType returnType;

	public ASTType baseType;
	
	public boolean isDummy;
	
	public ASTFunction(boolean isDummy, String name, ASTType returnType, 
			ASTType baseType, List<String> bitParameter, List<String> typeVariables, List<Pair<ASTType, String>> inputs) {
		this.bitParameter = bitParameter;
		this.isDummy = isDummy;
		this.name = name;
		this.returnType = returnType;
		this.baseType = baseType;
		this.inputVariables = inputs;
		this.typeVariables = typeVariables;
	}
	
	public ASTFunctionType getType() {
		List<ASTType> inputs = new ArrayList<ASTType>();
		for(int i=0; i<inputVariables.size(); ++i)
			inputs.add(inputVariables.get(i).left);
		
		ASTFunctionType ret = new ASTFunctionType(returnType, name, inputs, true);
		if(typeVariables != null)
			for(int i=0; i<typeVariables.size(); ++i)
				ret.typeParameter.add(new ASTVariableType(typeVariables.get(i)));
		return ret;
	}
}
