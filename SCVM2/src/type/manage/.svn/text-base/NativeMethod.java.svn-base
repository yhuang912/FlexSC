package type.manage;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class NativeMethod extends Method {

	public String nativeName;
	
	public NativeMethod(Type baseType, Type returnType, String name,
			String nativeName,
			List<Pair<Type, String>> para) {
		super(baseType, returnType, name, new ArrayList<String>(), para, null);
		if(!(baseType instanceof NativeType) && baseType != null) {
			throw new RuntimeException("native method must be a native function as well!");
		}
		this.nativeName = nativeName;
	}
}
