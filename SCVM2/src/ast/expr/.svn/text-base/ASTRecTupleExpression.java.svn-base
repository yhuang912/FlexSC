package ast.expr;


public class ASTRecTupleExpression extends ASTTupleExpression {
	public ASTExpression base;
	
	public ASTRecTupleExpression(ASTExpression base, ASTTupleExpression tuple) {
		this.base = base;
		this.exps = tuple.exps;
	}
	
	@Override
	public int level() {
		return 100;
	}
	
	public String toString() {
		return base.toString() + ".(" + super.toString() + ")";
	}

}
