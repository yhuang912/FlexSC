package ast;

import ast.expr.ASTExpression;
import ast.expr.ASTPredicate;
import ast.stmt.ASTStatement;
import ast.type.ASTType;

/**
 * Top level class in the abstract syntax tree. 
 * Used by the parser to build the AST. 
 * Type hierarchy of AST.java:<p>
 * <BLOCKQUOTE>
 * - ASTExpression.java<br>
 * - ASTFunction.java<br>
 * - ASTPredicate.java<br>
 * - ASTStatement.java<br>
 * - ASTType.java<br>
 * - ASTVariable.java<br>
 * </BLOCKQUOTE>
 * @see ASTExpression
 * @see ASTFunctionDef
 * @see ASTPredicate
 * @see ASTStatement
 * @see ASTType
 * @see ASTVariable
 */
public abstract class AST {
	public abstract String toString(int indent);
	
	public String indent(int n) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; ++i)
			sb.append("  ");
		return sb.toString();
	}
}
