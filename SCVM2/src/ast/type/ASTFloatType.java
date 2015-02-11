package ast.type;

import ast.expr.ASTConstantExpression;
import ast.expr.ASTExpression;

public class ASTFloatType extends ASTType {
	
	private ASTExpression bit;
	private ASTLabel lab;
	
	public ASTExpression getBits() {
		return bit;
	}

	public ASTLabel getLabel() {
		return lab;
	}
	
	public static ASTFloatType get(ASTExpression bit, ASTLabel lab) {
		return new ASTFloatType(bit, lab);
	}
	
	private ASTFloatType(ASTExpression bit, ASTLabel lab) {
		this.bit = bit;
		this.lab = lab;
	}
	
	public String toString(int indent) {
		return toString();
	}
	
	public String toString() {
		if(bit == null)
			return lab.toString() + " " + "float";
		else
			return lab.toString() + " " + "float"+(bit instanceof ASTConstantExpression ? bit.toString() : "@("+bit.toString()+")");
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ASTFloatType))
			return false;
		ASTFloatType other = (ASTFloatType)obj;
		return bit.equals(other.bit) && lab == other.lab;
	}

	@Override
	public boolean match(ASTType type) {
		if(!(type instanceof ASTFloatType))
			return false;
		ASTFloatType it = (ASTFloatType)type;
		if(bit.equals(it.bit) || bit == null || it.bit == null)
			return true;
		return false;
	}

	@Override
	public String shortName() {
		return toString();
	}
}
