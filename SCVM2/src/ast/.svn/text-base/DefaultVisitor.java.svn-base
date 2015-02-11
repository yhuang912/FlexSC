package ast;

import ast.type.ASTArrayType;
import ast.type.ASTFloatType;
import ast.type.ASTFunctionType;
import ast.type.ASTIntType;
import ast.type.ASTNativeType;
import ast.type.ASTRecType;
import ast.type.ASTRndType;
import ast.type.ASTType;
import ast.type.ASTTypeVisitor;
import ast.type.ASTVariableType;
import ast.type.ASTVoidType;

public abstract class DefaultVisitor<T1, T2, T3> extends DefaultStatementExpressionVisitor<T1, T2> implements ASTTypeVisitor<T3> {

	public T3 visit(ASTType type) {
		if(type instanceof ASTArrayType) {
			return visit((ASTArrayType)type);
		} else if(type instanceof ASTIntType) {
			return visit((ASTIntType)type);
		} else if(type instanceof ASTFloatType) {
			return visit((ASTFloatType)type);
		} else if(type instanceof ASTRndType) {
			return visit((ASTRndType)type);
		} else if(type instanceof ASTFunctionType) {
			return visit((ASTFunctionType)type);
		} else if(type instanceof ASTNativeType) {
			return visit((ASTNativeType)type);
		} else if(type instanceof ASTRecType) {
			return visit((ASTRecType)type);
		} else if(type instanceof ASTVariableType) {
			return visit((ASTVariableType)type);
		} else if(type instanceof ASTVoidType) {
			return visit((ASTVoidType)type);
		} else
			throw new RuntimeException("Unknown type!");
	}
}
