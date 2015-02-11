package type.manage;

public class Unknown extends VariableConstant {
	public String toString() {
		return "unknown";
	}
	
	public boolean equals(VariableConstant other) {
		return other instanceof Unknown;
	}

	@Override
	public boolean isConstant(int value) {
		return false;
	}
}
