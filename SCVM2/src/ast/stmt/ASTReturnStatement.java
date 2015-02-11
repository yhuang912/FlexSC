package ast.stmt;

import ast.expr.ASTExpression;

/**
 * Defines the return of the program. Defined of type ASTExpression.
 */
public class ASTReturnStatement extends ASTStatement {
	public ASTExpression exp;
	
	public ASTReturnStatement(ASTExpression exp) {
		this.exp = exp;
	}
	
	@Override
	public String toString(int indent) {
		return super.indent(indent)+"return "+exp.toString()+";\n";
	}

}
