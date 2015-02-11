/**
 * 
 */
package ast.expr;

import ast.expr.ASTExpression;

/**
 * @author Chang Liu
 *
 */
public class ASTRangeExpression extends ASTExpression {

	public ASTExpression source, rangel, ranger;
	
	public ASTRangeExpression(ASTExpression source, ASTExpression rangel, ASTExpression ranger) {
		this.source = source;
		this.rangel = rangel;
		this.ranger = ranger;
	}
	
	public String toString() {
		return source.toString()+"$"+rangel.toString()+(ranger == null ? "$" : ".."+ranger.toString()+"$");
	}
	
	@Override
	public int level() {
		return 100;
	}

}
