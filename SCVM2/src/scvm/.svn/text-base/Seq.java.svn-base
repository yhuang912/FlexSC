package scvm;

public class Seq extends SCVMCode {
	public SCVMCode s1, s2;

	private Seq(SCVMCode s1, SCVMCode s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public static SCVMCode seq(SCVMCode s1, SCVMCode s2) {
		if(s1 instanceof Skip)
			return s2;
		if(s2 instanceof Skip)
			return s1;
		return new Seq(s1, s2);
	}
	
	@Override
	public String toString(int indent) {
		return s1.toString(indent)+s2.toString(indent);
	}

	public String toString() {
		return toString(0);
	}
	
	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new Seq(s1.clone(withTypeDef), s2.clone(withTypeDef));
		ret.withTypeDef = withTypeDef;
		return ret;
	}
}
