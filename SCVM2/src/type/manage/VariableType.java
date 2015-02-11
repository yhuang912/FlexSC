package type.manage;

public class VariableType extends Type {

	public VariableType(String name) {
		super(name);
	}

	@Override
	public VariableConstant getBits() {
		return new Unknown();
	}

	@Override
	public Label getLabel() {
		return Label.Secure;
	}

}
