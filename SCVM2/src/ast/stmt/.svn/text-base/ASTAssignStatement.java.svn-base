package ast.stmt;

import ast.expr.ASTExpression;

/**
 * Defines an assignment statement of the form "var = expr;" 
 * where var is of type ASTVariable and expr is of type ASTExpression. 
 * @see ASTVariable
 * @see ASTExpression
 */
public class ASTAssignStatement extends ASTStatement {
	public ASTExpression var;
	public ASTExpression expr;
	
	public ASTAssignStatement(ASTExpression v, ASTExpression e) {
		this.var = v;
		this.expr = e;
	}

	@Override
	public String toString(int indent) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.indent(indent));
		sb.append(toString());
		sb.append("\n");
		return sb.toString();
	}
	
	public String toString() {
		return var+"="+expr.toString(0)+";";
	}
}
