package ast.type;

import ast.expr.ASTConstantExpression;
import ast.expr.ASTExpression;

public class ASTRndType extends ASTType {
	
	private ASTExpression bit;
	private ASTLabel lab;
	
	public ASTExpression getBits() {
		return bit;
	}

	public ASTLabel getLabel() {
		return lab;
	}
	
	public static ASTRndType get(ASTExpression bit, ASTLabel lab) {
		return new ASTRndType(bit, lab);
	}
	
	private ASTRndType(ASTExpression bit, ASTLabel lab) {
		this.bit = bit;
		this.lab = lab;
	}
	
	public String toString(int indent) {
		return toString();
	}
	
	public String toString() {
		if(bit == null)
			return lab.toString() + " " + "rnd";
		else
			return lab.toString() + " " + "rnd"+(bit instanceof ASTConstantExpression ? bit.toString() : "@("+bit.toString()+")");
	}
	
//	public int hashCode() {
//		return bit. * ASTLabel.getLabelNumber() + lab.getId();  
//	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ASTRndType))
			return false;
		ASTRndType other = (ASTRndType)obj;
		return bit.equals(other.bit) && lab == other.lab;
	}

	@Override
	public boolean match(ASTType type) {
		if(!(type instanceof ASTRndType))
			return false;
		ASTRndType it = (ASTRndType)type;
		if(bit.equals(it.bit) || bit == null || it.bit == null)
			return true;
		return false;
	}

	@Override
	public String shortName() {
		return toString();
	}
}
