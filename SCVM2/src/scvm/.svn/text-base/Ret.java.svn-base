package scvm;


public class Ret extends SCVMCode {
	public Variable exp;

	public Ret(Variable exp) {
		this.exp = exp;
	}
	
	@Override
	public String toString() {
		return "return "+exp+";";
	}

	@Override
	public String toString(int indent) {
		return this.indent(indent)+toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		return new Ret(exp);
	}

	
}
