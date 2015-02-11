package type.source;

import java.util.HashSet;
import java.util.Set;

import ast.expr.ASTConstantExpression;
import ast.expr.ASTExpression;
import ast.expr.ASTVariableExpression;
import ast.type.ASTArrayType;
import ast.type.ASTDefaultTypeVisitor;
import ast.type.ASTFloatType;
import ast.type.ASTFunctionType;
import ast.type.ASTIntType;
import ast.type.ASTNativeType;
import ast.type.ASTRecType;
import ast.type.ASTRndType;
import ast.type.ASTVariableType;
import ast.type.ASTVoidType;

public class BitVariableExtractor extends ASTDefaultTypeVisitor<Set<String>> {

	@Override
	public Set<String> visit(ASTArrayType type) {
		return null;
	}

	@Override
	public Set<String> visit(ASTIntType type) {
		Set<String> ret = new HashSet<String>();
		if(type.getBits() instanceof ASTConstantExpression)
			return ret;
		else if(type.getBits() instanceof ASTVariableExpression)
			ret.add(((ASTVariableExpression)type.getBits()).var);
		else
			return null;
		return ret;
	}

	@Override
	public Set<String> visit(ASTFloatType type) {
		Set<String> ret = new HashSet<String>();
		if(type.getBits() instanceof ASTConstantExpression)
			return ret;
		else if(type.getBits() instanceof ASTVariableExpression)
			ret.add(((ASTVariableExpression)type.getBits()).var);
		else
			return null;
		return ret;
	}

	@Override
	public Set<String> visit(ASTRndType type) {
		Set<String> ret = new HashSet<String>();
		if(type.getBits() instanceof ASTConstantExpression)
			return ret;
		else if(type.getBits() instanceof ASTVariableExpression)
			ret.add(((ASTVariableExpression)type.getBits()).var);
		else
			return null;
		return ret;
	}

	@Override
	public Set<String> visit(ASTNativeType type) {
		return null;
	}

	@Override
	public Set<String> visit(ASTRecType type) {
		Set<String> ret = new HashSet<String>();
		for(ASTExpression s : type.bitVariables)
			ret.add(((ASTVariableExpression)s).var);
		return ret;
	}

	@Override
	public Set<String> visit(ASTVariableType type) {
		Set<String> ret = new HashSet<String>();
		for(ASTExpression s : type.bitVars)
			ret.add(((ASTVariableExpression)s).var);
		return ret;
	}

	@Override
	public Set<String> visit(ASTVoidType type) {
		return null;
	}

	@Override
	public Set<String> visit(ASTFunctionType type) {
		return null;
	}
}

