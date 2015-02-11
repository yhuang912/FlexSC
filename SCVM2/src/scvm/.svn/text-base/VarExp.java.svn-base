package scvm;

import type.manage.Label;

public class VarExp extends Expression {
	public Variable var;
	
	public VarExp(Variable var) {
		this.var = var;
	}
	
	public String toString() {
		return var.toString();
	}

	@Override
	public Label getLabels() {
		return var.lab;
	}
}
