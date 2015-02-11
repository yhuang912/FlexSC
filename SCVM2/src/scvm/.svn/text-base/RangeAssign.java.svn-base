package scvm;

import type.manage.Label;
import type.manage.VariableConstant;

public class RangeAssign extends SCVMCode {
	
	public Label lab;
	
	public Variable name;

	public VariableConstant left, right;

	public Variable value;
	
	public RangeAssign(Label lab, Variable arr, VariableConstant left, VariableConstant right, Variable value) {
		this.lab = lab;
		this.name = arr;
		this.left = left;
		this.right = right;
		this.value = value;
	}

	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer(this.indent(indent));
		sb.append(name.name);
		sb.append("$");
		sb.append(left.toString());
		sb.append('~');
		sb.append(right.toString());
		sb.append("$=");
		sb.append(value.toString());
		sb.append(";\n");
		return sb.toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new RangeAssign(lab, name, left, right, value);
		ret.withTypeDef = withTypeDef;
		return ret;
	}

}
