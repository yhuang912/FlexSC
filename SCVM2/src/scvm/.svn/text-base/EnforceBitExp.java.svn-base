package scvm;

import type.manage.Label;
import type.manage.VariableConstant;

public class EnforceBitExp extends Expression {
	public Variable v;
	public VariableConstant bits;
	
	public EnforceBitExp(Variable v, VariableConstant bits) {
		this.v = v;
		this.bits = bits;
	}
	
	@Override
	public String toString() {
		return v+" :> int@"+bits;
	}

	@Override
	public Label getLabels() {
		return v.lab;
	}

}
