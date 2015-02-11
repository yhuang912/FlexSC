package scvm;

import type.manage.IntType;
import type.manage.Label;

public class Assign extends SCVMCode {

	public Label lab;
	public Variable name;
	public Expression exp;
	
	public Assign(Label lab, Variable name, Expression exp) {
		this.lab = lab;
		this.name = name;
		this.exp = exp;
	}
	
	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.indent(indent));
		if(name.name.startsWith("__") && withTypeDef)
			sb.append(name.type.toString()+" "+name.name+" = "+exp.toString()+";\n");
		else
			sb.append(name.name+" = "+exp.toString()+";\n");
		return sb.toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new Assign(lab, name, exp);
		ret.withTypeDef = withTypeDef;
		return ret;
	}
}
