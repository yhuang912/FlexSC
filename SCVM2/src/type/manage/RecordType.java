package type.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scvm.BopExp.Op;
import ast.type.ASTRecType;

public class RecordType extends Type {

	public String name;
	public Label lab;
	public List<Type> typeParameter;
	public Map<String, Type> fields;
	public List<VariableConstant> bits;
	
	public RecordType(String name, Label lab, List<VariableConstant> bits) {
		super(name);
		this.name = name;
		this.lab = lab;
		this.fields = new HashMap<String, Type>();
		this.typeParameter = new ArrayList<Type>();
		this.bits = bits;
	}

	public RecordType(ASTRecType type, List<VariableConstant> bits) {
		this(type.name, Label.get(type.lab), bits);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("struct ");
		sb.append(name);
		if(this.typeParameter.size() > 0) {
			sb.append("<");
			for(int i=0; i<typeParameter.size(); ++i) {
				if(i > 0)
					sb.append(",");
				sb.append(typeParameter.get(i).toString());
			}
			sb.append(">");
		}
		if(this.fields != null) {
			sb.append(" {\n");
			for(Map.Entry<String, Type> ent : fields.entrySet()) {
				sb.append("\t"+ent.getValue().name+" "+ent.getKey()+"\n");
			}
			sb.append("};\n");
		}
		return sb.toString();
	}

	
	public String fullSpec() {
		StringBuffer sb = new StringBuffer();
		sb.append("struct ");
		sb.append(name);
		if(this.typeParameter.size() > 0) {
			sb.append("<");
			for(int i=0; i<typeParameter.size(); ++i) {
				if(i > 0)
					sb.append(",");
				sb.append(typeParameter.get(i).toString());
			}
			sb.append(">");
		}
		sb.append(" {\n");
		if(this.fields != null) {
			for(Map.Entry<String, Type> ent : fields.entrySet()) {
				sb.append("\t"+ent.getValue().name+" "+ent.getKey()+"\n");
			}
		}
		for(Method method : this.getMethods()) {
			sb.append(method.toString());
			sb.append("\n");
		}
		sb.append("};\n");
		return sb.toString();
	}
	
	@Override
	public VariableConstant getBits() {
		VariableConstant sum = new Constant(0);
		for(Map.Entry<String, Type> ent : fields.entrySet()) {
			if(ent.getValue().getBits() == null)
				return null;
			VariableConstant bits = ent.getValue().getBits();
			if(bits instanceof Unknown)
				return bits;
			sum = new BOPVariableConstant(sum, Op.Add, bits);
		}
		return sum;
	}

	@Override
	public Label getLabel() {
		return lab;
	}

}
