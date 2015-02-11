package ast.expr;

/**
 * Extends ASTExpression. It defines a variable expression of the form 
 * "n = m;" where "n" and "m" are both variables.
 * <p>
 * <b> Member variables </b>: <p>
 * - public ASTVariable var;
 */
public class ASTVariableExpression extends ASTExpression {
	public String var;
	
	public ASTVariableExpression(String var) {
		this.var = var;
	}
	
	public String toString() {
		return var;
	}
	
	public int level() {
		return 100;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof ASTVariableExpression))
			return false;
		
		return var.equals(((ASTVariableExpression)obj).var);
	}
}
