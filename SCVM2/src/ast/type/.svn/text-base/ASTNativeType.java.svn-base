package ast.type;

import java.util.List;

public class ASTNativeType extends ASTType {

	public String name;
	public List<String> bitVariables;
	
	public ASTNativeType(String name, List<String> bits) {
		this.name = name;
		this.bitVariables = bits;
	}
	
	@Override
	public String toString(int indent) {
		return toString();
	}
	
	public String toString() {
		return "native " + name;
	}

	@Override
	public boolean match(ASTType type) {
		if(!(type instanceof ASTNativeType))
			return false;
		ASTNativeType nt = (ASTNativeType)type;
		
		return nt.name.equals(this.name);
	}

	@Override
	public String shortName() {
		return toString();
	}
	
	public boolean instance(ASTType type) {
		return this.match(type);
	}
}
