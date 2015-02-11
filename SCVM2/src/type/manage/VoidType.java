package type.manage;

public class VoidType extends Type {
	
	private static VoidType inst = null;
	
	public static VoidType get() {
		if(inst == null)
			inst = new VoidType();
		return inst;
	}
	
	private VoidType() {
		super("void");
	}
	
	public String toString(int indent) {
		return toString();
	}

	public String toString() {
		return "void";
	}

	@Override
	public VariableConstant getBits() {
		return new Unknown();
	}

	@Override
	public Label getLabel() {
		return Label.Pub;
	}
}
