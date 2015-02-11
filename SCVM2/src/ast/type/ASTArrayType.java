package ast.type;

import ast.expr.ASTExpression;

/**
 * Defines the type of arrays. It includes member variables "int size, bit;" which define 
 * the size of the array and the bit-width of the members of the array.
 */
public class ASTArrayType extends ASTType {
	public ASTExpression size;
	public ASTType type;
	public ASTLabel lab;
	
	public ASTArrayType(ASTType type, ASTExpression s, ASTLabel lab) {
		this.lab = lab;
		this.type = type;
		this.size = s;
	}

	@Override
	public String toString(int indent) {
		return toString();
	}
	
	public String toString() {
		return type.toString()+"["+lab.toString()+" "+size+"]";
	}

	@Override
	public boolean match(ASTType type) {
		if(!(type instanceof ASTArrayType))
			return false;
		ASTArrayType ty = (ASTArrayType)type;
		if(!size.equals(ty.size))
			return false;
		if(!this.type.match(ty.type))
			return false;
		return true;
	}

	@Override
	public String shortName() {
		return this.type.shortName()+"["+lab.toString()+" "+size+"]";
	}
}
