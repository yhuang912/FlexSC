package ast.type;

import ast.expr.ASTConstantExpression;
import ast.expr.ASTExpression;

public class ASTIntType extends ASTType {

	private ASTExpression bit;
	private ASTLabel lab;

	public ASTExpression getBits() {
		return bit;
	}

	public ASTLabel getLabel() {
		return lab;
	}

	public static ASTIntType get(int bit, ASTLabel lab) {
		return ASTIntType.get(new ASTConstantExpression(bit), lab);
	}

	public static ASTIntType get(ASTExpression bit, ASTLabel lab) {
		return new ASTIntType(bit, lab);
	}

	private ASTIntType(ASTExpression bit, ASTLabel lab) {
		this.bit = bit;
		this.lab = lab;
	}

	public String toString(int indent) {
		return toString();
	}

	public String toString() {
		if(bit == null)
			return lab.toString() + " " + "int";
		else
			return lab.toString() + " " + "int"+(bit instanceof ASTConstantExpression ? bit.toString() : "@("+bit.toString()+")");
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof ASTIntType))
			return false;
		ASTIntType other = (ASTIntType)obj;
		return bit.equals(other.bit) && lab == other.lab;
	}

	@Override
	public boolean match(ASTType type) {
		if(type instanceof ASTRndType) {
			ASTRndType it = (ASTRndType)type;
			if(bit == null || it.getBits() == null || bit.equals(it.getBits()))
				return true;
		} else if(type instanceof ASTIntType) {
			ASTIntType it = (ASTIntType)type;
			if(this.lab == ASTLabel.Pub && it.lab == ASTLabel.Pub)
					return true;
//			if(bit == null || it.bit == null || bit.equals(it.bit))
			return true;
		}
		return false;
	}

	@Override
	public String shortName() {
		return toString();
	}
}
