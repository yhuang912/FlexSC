package scvm;

import type.manage.Label;

public class RecordAssign extends SCVMCode {
	
	public Label lab;
	
	public Variable base;

	public String field;

	public Variable value;
	
	public RecordAssign(Label lab, Variable base, String field, Variable value) {
		this.lab = lab;
		this.base = base;
		this.field = field;
		this.value = value;
	}

	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer(this.indent(indent));
		sb.append(base.name+"."+field+" = "+value.toString()+";\n");
		return sb.toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new RecordAssign(lab, base, field, value);
		ret.withTypeDef = withTypeDef;
		return ret;
	}

}
