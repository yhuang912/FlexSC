package scvm;

import type.manage.Label;

public abstract class Expression {
	public abstract String toString();
	
	public abstract Label getLabels();
}
