package ast.type;

public class ASTVoidType extends ASTType {
	
	private static ASTVoidType inst = null;
	
	public static ASTVoidType get() {
		if(inst == null)
			inst = new ASTVoidType();
		return inst;
	}
	
	private ASTVoidType() {}
	
	@Override
	public String toString(int indent) {
		return toString();
	}

	public String toString() {
		return "void";
	}

	@Override
	public boolean match(ASTType type) {
		return type instanceof ASTVoidType;
	}

	@Override
	public String shortName() {
		return toString();
	}
}
