package ast.type;

import ast.AST;

/**
 * Top level class for types.  
 * Type hierarchy of ASTType.java:<p>
 * <BLOCKQUOTE>
 * - ASTArrayType.java<br>
 * - ASTIntType.java<br> 
 * - ASTStringType.java<br>
 * </BLOCKQUOTE>
 * @see ASTArrayType 
 * @see ASTIntType 
 * @see ASTStringType
 */
public abstract class ASTType extends AST {
	public abstract boolean match(ASTType type);
	
	public abstract String shortName();
	
	public boolean instance(ASTType type) {
		return false;
	}
}
