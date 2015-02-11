package ast.stmt;

import ast.expr.ASTFuncExpression;

public class ASTFuncStatement extends ASTStatement {
	public ASTFuncExpression func;
	
	public ASTFuncStatement(ASTFuncExpression func) {
		this.func = func;
	}
	
	@Override
	public String toString(int indent) {
		return this.indent(indent)+func.toString()+";\n";
	}

	public String toString() {
		return toString(0);
	}
}
