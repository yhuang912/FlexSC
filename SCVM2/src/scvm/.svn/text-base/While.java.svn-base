package scvm;

import type.manage.Label;

public class While extends SCVMCode {
	public Label lab;
	public Variable cond;
	public SCVMCode body;
	
	public While(Label lab, Variable cond, SCVMCode body) {
		if(lab != cond.lab || !cond.getBits().isConstant(1))
			throw new RuntimeException("Wrong construction of If");
		this.lab = lab;
		this.cond = cond;
		this.body = body;
	}
	
	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<indent; ++i)
			sb.append('\t');
		sb.append("while("+cond.name+") {\n");
		sb.append(body.toString(indent+1));
		for(int i=0; i<indent; ++i)
			sb.append('\t');
		sb.append("}\n");
		return sb.toString();
	}
	

	@Override
	public SCVMCode clone(boolean withTypeDef) {
		SCVMCode ret = new While(lab, cond, body.clone(withTypeDef));
		ret.withTypeDef = withTypeDef;
		return ret;
	}

}
