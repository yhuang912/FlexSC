package scvm;

import type.manage.Label;

public class If extends SCVMCode {

	public Label lab;
	public Variable cond;
	public SCVMCode trueBranch;
	public SCVMCode falseBranch;
	
	public If(Label lab, Variable cond, SCVMCode trueBranch, SCVMCode falseBranch) {
		if(lab != cond.lab || !cond.getBits().isConstant(1))
			throw new RuntimeException("Wrong construction of If");
		this.lab = lab;
		this.cond = cond;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}
	
	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.indent(indent));
		sb.append("if("+cond.name+") {\n");
		sb.append(trueBranch.toString(indent+1));
		sb.append(this.indent(indent));
		sb.append("} else {\n");
		sb.append(falseBranch.toString(indent+1));
		sb.append(this.indent(indent));
		sb.append("}\n");
		return sb.toString();
	}

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new If(lab, cond, trueBranch.clone(withTypeDef), falseBranch.clone(withTypeDef));
		ret.withTypeDef = withTypeDef;
		return ret;
	}
}
