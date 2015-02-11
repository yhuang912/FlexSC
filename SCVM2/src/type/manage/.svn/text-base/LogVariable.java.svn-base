package type.manage;

public class LogVariable extends VariableConstant {
	public VariableConstant exp;
	
	public LogVariable(VariableConstant exp) {
		this.exp = exp;
	}
	
	public String toString() {
		return "Utils.logFloor("+exp+")";
	}
	
	public boolean equals(VariableConstant obj) {
		if(!(obj instanceof LogVariable))
			return false;
		return exp == ((LogVariable)obj).exp;
	}

	@Override
	public boolean isConstant(int value) {
		return false;
	}


}
