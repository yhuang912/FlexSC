package ast;

public interface IFunctionVisitor<T> {
	
	public T visit(ASTFunctionNative func);
	
	public T visit(ASTFunctionDef func);
	
	public T visit(ASTFunction func);
}
