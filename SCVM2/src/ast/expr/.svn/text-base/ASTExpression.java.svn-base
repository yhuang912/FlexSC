package ast.expr;

import ast.AST;

/**
 * Top level class for expression.  
 * Type hierarchy of ASTExpression.java:<p>
 * <BLOCKQUOTE>
 * - ASTArrayExpression.java <br>
 * - ASTBinaryExpression.java <br>
 * - ASTConstantExpression.java <br>
 * - ASTConstantStringExpression.java <br>
 * - ASTVariableExpression <br>
 * </BLOCKQUOTE>
 * @see ASTArrayExpression
 * @see ASTBinaryExpression
 * @see ASTConstantExpression
 * @see ASTConstantStringExpression
 * @see ASTVariableExpression
 */
public abstract class ASTExpression extends AST {
	public ASTExpression targetBits = null;
	
	public String toString(int indent) {
		return toString();
	}
	
	public abstract int level();
}
