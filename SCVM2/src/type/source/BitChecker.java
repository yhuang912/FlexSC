package type.source;

import java.util.HashSet;
import java.util.Set;

import ast.ASTFunction;
import ast.ASTFunctionDef;
import ast.ASTProgram;
import ast.DefaultVisitor;
import ast.expr.ASTAndPredicate;
import ast.expr.ASTArrayExpression;
import ast.expr.ASTBinaryExpression;
import ast.expr.ASTBinaryPredicate;
import ast.expr.ASTConstantExpression;
import ast.expr.ASTExpression;
import ast.expr.ASTFloatConstantExpression;
import ast.expr.ASTFuncExpression;
import ast.expr.ASTLogExpression;
import ast.expr.ASTNewObjectExpression;
import ast.expr.ASTOrPredicate;
import ast.expr.ASTRangeExpression;
import ast.expr.ASTRecExpression;
import ast.expr.ASTRecTupleExpression;
import ast.expr.ASTTupleExpression;
import ast.expr.ASTVariableExpression;
import ast.stmt.ASTAssignStatement;
import ast.stmt.ASTFuncStatement;
import ast.stmt.ASTIfStatement;
import ast.stmt.ASTReturnStatement;
import ast.stmt.ASTWhileStatement;
import ast.type.ASTArrayType;
import ast.type.ASTFloatType;
import ast.type.ASTFunctionType;
import ast.type.ASTIntType;
import ast.type.ASTNativeType;
import ast.type.ASTRecType;
import ast.type.ASTRndType;
import ast.type.ASTType;
import ast.type.ASTVariableType;
import ast.type.ASTVoidType;

public class BitChecker extends DefaultVisitor<Boolean, Boolean, Boolean> {

	Set<String> bitVariables;

	BitVariableExtractor bve = new BitVariableExtractor();
	
	public boolean check(ASTProgram program) {

		bitVariables = new HashSet<String>();

		for(int i = 0; i<program.typeDef.size(); ++i) {
			String name = program.typeDef.get(i).left;
			bitVariables.clear();
			if(program.typeBitVarDef.containsKey(name))
				for(ASTExpression e : program.typeBitVarDef.get(name)) {
					ASTVariableExpression ve = (ASTVariableExpression)e;
					bitVariables.add(ve.var);
				}
			if(!visit(program.typeDef.get(i).right))
				return false;
		}
		
		for(ASTFunction func : program.functionDef)
			if(func instanceof ASTFunctionDef) {
				ASTFunctionDef function = ((ASTFunctionDef)func);
				bitVariables.clear();
				if(function.baseType != null) {
					Set<String> t = bve.visit(function.baseType);
					if(t != null)
						bitVariables.addAll(t);
				}
				bitVariables.addAll(function.bitParameter);
				for(int i=0; i<function.inputVariables.size(); ++i)
					if(!visit(function.inputVariables.get(i).left))
						return false;
				for(int i=0; i<function.localVariables.size(); ++i)
					if(!visit(function.localVariables.get(i).left))
						return false;
			}
		return true;
	}
	
	@Override
	public Boolean visit(ASTArrayType type) {
		return visit(type.size) && visit(type.type);
	}

	@Override
	public Boolean visit(ASTIntType type) {
		return type.getBits() == null || visit(type.getBits());
	}

	@Override
	public Boolean visit(ASTFloatType type) {
		return type.getBits() == null || visit(type.getBits());
	}

	@Override
	public Boolean visit(ASTRndType type) {
		return type.getBits() == null || visit(type.getBits());
	}

	@Override
	public Boolean visit(ASTNativeType type) {
		return true;
	}

	@Override
	public Boolean visit(ASTRecType type) {
		for(ASTExpression e : type.bitVariables)
			if(!visit(e))
				return false;
		for(ASTType ty : type.fieldsType.values())
			if(!visit(ty))
				return false;
		return true;
	}

	@Override
	public Boolean visit(ASTVariableType type) {
		for(ASTExpression e : type.bitVars)
			if(!visit(e))
				return false;
		if(type.typeVars != null)
			for(ASTType ty : type.typeVars)
				if(!visit(ty))
					return false;
		return true;
	}

	@Override
	public Boolean visit(ASTVoidType type) {
		return true;
	}

	@Override
	public Boolean visit(ASTFunctionType type) {
		if(!visit(type.returnType))
			return false;
		for(int i=0; i<type.inputTypes.size(); ++i)
			if(!visit(type.inputTypes.get(i)))
				return false;
		return true;
	}

	@Override
	public Boolean visit(ASTAssignStatement assignStatement) {
		return false;
	}

	@Override
	public Boolean visit(ASTFuncStatement funcStatement) {
		return null;
	}

	@Override
	public Boolean visit(ASTIfStatement ifStatement) {
		return null;
	}

	@Override
	public Boolean visit(ASTReturnStatement returnStatement) {
		return null;
	}

	@Override
	public Boolean visit(ASTWhileStatement whileStatement) {
		return null;
	}

	@Override
	public Boolean visit(ASTAndPredicate andPredicate) {
		return visit(andPredicate.left) && visit(andPredicate.right);
	}

	@Override
	public Boolean visit(ASTArrayExpression arrayExpression) {
		return false;
	}

	@Override
	public Boolean visit(ASTBinaryExpression binaryExpression) {
		return visit(binaryExpression.left) && visit(binaryExpression.right);
	}

	@Override
	public Boolean visit(ASTBinaryPredicate binaryPredicate) {
		return visit(binaryPredicate.left) && visit(binaryPredicate.right);
	}

	@Override
	public Boolean visit(ASTConstantExpression constantExpression) {
		return true;
	}

	@Override
	public Boolean visit(ASTFuncExpression funcExpression) {
		return false;
	}

	@Override
	public Boolean visit(ASTNewObjectExpression exp) {
		return false;
	}

	@Override
	public Boolean visit(ASTOrPredicate orPredicate) {
		return visit(orPredicate.left) && visit(orPredicate.right);
	}

	@Override
	public Boolean visit(ASTRecExpression rec) {
		return false;
	}

	@Override
	public Boolean visit(ASTRecTupleExpression tuple) {
		return false;
	}

	@Override
	public Boolean visit(ASTTupleExpression tuple) {
		return false;
	}

	@Override
	public Boolean visit(ASTVariableExpression variableExpression) {
		return this.bitVariables.contains(variableExpression.var);
	}

	@Override
	public Boolean visit(ASTFloatConstantExpression constantExpression) {
		return true;
	}

	@Override
	public Boolean visit(ASTLogExpression tuple) {
		return true;
	}

	@Override
	public Boolean visit(ASTRangeExpression tuple) {
		return false;
	}

}
