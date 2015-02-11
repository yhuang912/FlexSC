package scvm;

import type.manage.Label;
import type.manage.Type;
import type.manage.VariableConstant;

public class Variable {
	public Label lab;
	public String name;
	public Type type;
	
	public Variable(Type type, Label lab, String name) {
		this.type = type;
		this.lab = lab;
		this.name = name;
	}
	
	public VariableConstant getBits() {
		return type.getBits();
	}
	
	public String toString() {
		return name;
	}
}
