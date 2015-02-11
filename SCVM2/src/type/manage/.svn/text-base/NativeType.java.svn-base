package type.manage;

import java.util.ArrayList;
import java.util.List;

public class NativeType extends Type {

	public String nativeName;
	public List<VariableType> typeVariables;
	
	public NativeType(String name, String nativeName, Method... methods) {
		super(name, methods);
		this.nativeName = nativeName;
		this.typeVariables = new ArrayList<VariableType>();
	}

	@Override
	public VariableConstant getBits() {
		return null;
	}

	@Override
	public Label getLabel() {
		return Label.Secure;
	}

}
