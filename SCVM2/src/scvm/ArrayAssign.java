package scvm;

import type.manage.Label;

public class ArrayAssign extends SCVMCode {
	
	public Label lab;
	
	public Variable name;

	public Variable idx;

	public Variable value;
	
	public ArrayAssign(Label lab, Variable arr, Variable idx, Variable value) {
		this.lab = lab;
		this.name = arr;
		this.idx = idx;
		this.value = value;
	}

	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer(this.indent(indent));
		if(name.lab == Label.Secure) {
			sb.append(name.name);
			sb.append(".Write(");
			sb.append(idx.toString());
			sb.append(',');
			sb.append(value.toString());
			sb.append(");\n");
		} else {
			sb.append(name.name+"["+idx.toString()+"]="+value.toString()+";\n");
		}
		return sb.toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new ArrayAssign(lab, name, idx, value);
		ret.withTypeDef = withTypeDef;
		return ret;
	}

}
