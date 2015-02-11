package scvm;

import type.manage.Label;

public class LogExp extends Expression {
	public Variable exp;
	public Label lab;
	
	public LogExp(Label lab, Variable exp) {
		this.exp = exp;
		this.lab = lab;
	}
	
	@Override
	public String toString() {
		return "log("+this.exp+")";
	}

	@Override
	public Label getLabels() {
		return lab;
	}

}
