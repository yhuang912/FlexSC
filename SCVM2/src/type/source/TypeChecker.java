package type.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Pair;
import ast.ASTFunction;
import ast.ASTFunctionDef;
import ast.ASTFunctionNative;
import ast.ASTProgram;
import ast.DefaultStatementExpressionVisitor;
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
import ast.expr.ASTBinaryExpression.BOP;
import ast.stmt.ASTAssignStatement;
import ast.stmt.ASTFuncStatement;
import ast.stmt.ASTIfStatement;
import ast.stmt.ASTReturnStatement;
import ast.stmt.ASTStatement;
import ast.stmt.ASTWhileStatement;
import ast.type.ASTArrayType;
import ast.type.ASTFloatType;
import ast.type.ASTFunctionType;
import ast.type.ASTIntType;
import ast.type.ASTLabel;
import ast.type.ASTRecType;
import ast.type.ASTType;
import ast.type.ASTVariableType;

public class TypeChecker extends DefaultStatementExpressionVisitor<Boolean, List<ASTType>> {

	public TypeResolver resolver = new TypeResolver();
	AssignableChecker ac = new AssignableChecker();
	BitChecker bc = new BitChecker();
	
	public ASTFunctionDef function = null;
	public ASTProgram program;

	Map<String, ASTType> variableMapping;

	public boolean check(ASTProgram program) {
		resolver.copy = false;
		program = resolver.resolve(program);
		resolver.copy = true;
		this.program = program;

		if(!bc.check(program))
			return false;
		
		variableMapping = new HashMap<String, ASTType>();

		for(ASTFunction func : program.functionDef) {
			if(func.baseType == null) {
				variableMapping.put(func.name, func.getType());
			}
		}
		
		for(ASTFunction func : program.functionDef)
			if(func instanceof ASTFunctionDef) {
				this.function = ((ASTFunctionDef)func);
				Map<String, ASTType> old = variableMapping;
				variableMapping = new HashMap<String, ASTType>(old);
				if(function.baseType instanceof ASTRecType) {
					ASTRecType rt = (ASTRecType)function.baseType;
					for(ASTExpression e : rt.bitVariables)
						variableMapping.put(((ASTVariableExpression)e).var, ASTIntType.get(32, ASTLabel.Pub));
				}
				for(String e : function.bitParameter)
					variableMapping.put(e, ASTIntType.get(32, ASTLabel.Pub));
				for(Pair<ASTType, String> v : function.inputVariables) {
					variableMapping.put(v.right, v.left);
				}
				for(Pair<ASTType, String> v : function.localVariables) {
					variableMapping.put(v.right, v.left);
				}
				for(ASTStatement stmt : function.body)
					if(!visit(stmt)) {
						visit(stmt);
						return false;
					}
				variableMapping = old;
			}
		return true;
	}

	ASTType assertOne(List<ASTType> ret) {
		if(ret == null || ret.size() != 1)
			return null;
		else
			return ret.get(0);
	}

	List<ASTType> buildOne(ASTType ret) {
		if(ret == null)
			return null;
		List<ASTType> r = new ArrayList<ASTType>();
		r.add(ret);
		return r;
	}

	@Override
	public Boolean visit(ASTAssignStatement assignStatement) {
		if(!ac.visit(assignStatement.var))
			return false;
		List<ASTType> expTypes = visit(assignStatement.expr);
		List<ASTType> varTypes = visit(assignStatement.var);
		if(expTypes == null || varTypes == null) {
			System.err.println("Statement:\n"+assignStatement+"\ncannot type check!");
			return false;
		}
		if(varTypes.size() != expTypes.size() && expTypes.size() != 1) {
			System.err.println("Number of expressions are not matched in: "+assignStatement);
			return false;
		}
		if(expTypes.size() == 1 && varTypes.size() != 1) {
			if(!(expTypes.get(0) instanceof ASTRecType))
				return false;
			ASTRecType ty = (ASTRecType)expTypes.get(0);
			if(varTypes.size() != ty.fields.size())
				return false;
			for(int i=0; i<varTypes.size(); ++i) {
				if(!varTypes.get(i).match(ty.fieldsType.get(ty.fields.get(i))))
					return false;
			}
			ASTExpression[] exps = new ASTExpression[ty.fields.size()];
			for(int i=0; i<exps.length; ++i)
				exps[i] = new ASTVariableExpression(ty.fields.get(i));
			ASTRecTupleExpression exp = new ASTRecTupleExpression(assignStatement.expr, new ASTTupleExpression(exps));
			assignStatement.expr = exp;
			return true;
		} else {
			for(int i=0; i<expTypes.size(); ++i) {
				if(!varTypes.get(i).match(expTypes.get(i)))
					return false;
			}
			return true;
		}
	}

	@Override
	public Boolean visit(ASTFuncStatement funcStatement) {
		return visit(funcStatement.func) != null;
	}

	@Override
	public Boolean visit(ASTIfStatement ifStatement) {
		ASTType ty = assertOne(visit(ifStatement.cond));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty)) {
			visit(ifStatement.cond);
			System.err.println("Condition: "+ifStatement.cond+" is not a boolean!");
			return false;
		}
		// TODO security check
		for(ASTStatement stmt : ifStatement.trueBranch)
			if(!visit(stmt)) {
				System.err.println(stmt+" cannot type check!");
				visit(stmt);
				return false;
			}
		for(ASTStatement stmt : ifStatement.falseBranch)
			if(!visit(stmt)) {
				System.err.println(stmt+" cannot type check!");
				visit(stmt);
				return false;
			}
		return true;
	}

	@Override
	public Boolean visit(ASTReturnStatement returnStatement) {
		ASTType ty = assertOne(visit(returnStatement.exp));

		return this.function.returnType.match(ty);
	}

	@Override
	public Boolean visit(ASTWhileStatement whileStatement) {
		// TODO security check
		ASTType ty = assertOne(visit(whileStatement.cond));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty))
			return false;
		for(ASTStatement stmt : whileStatement.body)
			if(!visit(stmt)) {
				System.err.println(stmt+" cannot type check!");
				visit(stmt);
				return false;
			}
		return true;
	}

	@Override
	public List<ASTType> visit(ASTArrayExpression arrayExpression) {
		ASTType ty = assertOne(visit(arrayExpression.var));
		if(!(ty instanceof ASTArrayType))
			return null;
		ASTArrayType type = (ASTArrayType)ty;
		ty = assertOne(visit(arrayExpression.indexExpr));
		if(!(ty instanceof ASTIntType))
			return null;
		// TODO Check security
		return buildOne(type.type);
	}

	@Override
	public List<ASTType> visit(ASTBinaryExpression binaryExpression) {
		ASTType ty = assertOne(visit(binaryExpression.left));
		if(ty instanceof ASTIntType) {
			ASTIntType ty1 = (ASTIntType)ty;
			ty = assertOne(visit(binaryExpression.right));
			if(!(ty instanceof ASTIntType))
				return null;
			ASTIntType ty2 = (ASTIntType)ty;
			if(!ty1.match(ty2))
				return null;
			return buildOne(ASTIntType.get(ty1.getBits(), ty1.getLabel().meet(ty2.getLabel())));
		} else if (ty instanceof ASTFloatType) {
			ASTFloatType ty1 = (ASTFloatType)ty;
			ty = assertOne(visit(binaryExpression.right));
			if(!(ty instanceof ASTFloatType))
				return null;
			ASTFloatType ty2 = (ASTFloatType)ty;
			if(!ty1.match(ty2))
				return null;
			return buildOne(ASTFloatType.get(ty1.getBits(), ty1.getLabel().meet(ty2.getLabel())));
		} else
			return null;
	}

	@Override
	public List<ASTType> visit(ASTConstantExpression constantExpression) {
		return buildOne(ASTIntType.get(constantExpression.bitSize, ASTLabel.Pub));
	}

	@Override
	public List<ASTType> visit(ASTFuncExpression funcExpression) {
		ASTFuncExpression exp = funcExpression;
		ASTExpression obj = exp.obj;
		if(obj instanceof ASTVariableExpression) {
			String name = ((ASTVariableExpression)obj).var;
			if(exp.inputs.size() > 0) {
				// TODO forgot what to do...
			}
			if(this.variableMapping.get(name) instanceof ASTFunctionType) {
				// TODO Function Type is not adjusted to have bit parameters yet
				ASTFunctionType type = (ASTFunctionType)this.variableMapping.get(name);
				if(type.inputTypes.size() != exp.inputs.size()) {
					System.err.println("input numbers mis-match in "+funcExpression);
					return null;
				}
				int tpn = 0;
				if(type.typeParameter != null) tpn = type.typeParameter.size();
				int tvn = 0;
				if(funcExpression.typeVars != null) tvn = funcExpression.typeVars.size();
				if(tpn != tvn) {
					System.err.println("numbers of type parameters mis-match in "+funcExpression);
					return null;
				}
				if(tpn > 0) {
					resolver.typeVars = new HashMap<String, ASTType>();
					for(int i=0; i<type.typeParameter.size(); ++i)
						resolver.typeVars.put(((ASTVariableType)type.typeParameter.get(i)).var, funcExpression.typeVars.get(i));
					type = (ASTFunctionType)resolver.visit(type);
				}
				for(int i=0; i<type.inputTypes.size(); ++i) {
					// System.out.println(funcExpression);
					ASTType t = assertOne(visit(exp.inputs.get(i).right));
					if(!type.inputTypes.get(i).match(t))
						return null;
				}
				return buildOne(type.returnType);
			}
			for(ASTFunction func : program.functionDef) {
				if(func.name.equals(name) && func.baseType == null) {
					if(func.bitParameter.size() != funcExpression.bitParameters.size())
						return null;
					if(func.typeVariables.size() != funcExpression.typeVars.size()) {
						return null;
					}
					if(func.inputVariables.size() != exp.inputs.size())
						return null;
					for(int i=0; i<func.inputVariables.size(); ++i) {
						ASTType t = assertOne(visit(exp.inputs.get(i).right));
						if(!func.inputVariables.get(i).left.match(t))
							return null;
					}
					return buildOne(func.returnType);
				}
			}
			return null;
		} else if(obj instanceof ASTRecExpression) {
			ASTRecExpression recObj = (ASTRecExpression)obj;
			String name = recObj.field;
			ASTType baseType = assertOne(visit(recObj.base));
			if(baseType == null)
				return null;
			for(ASTFunction func : program.functionDef) {
				if(func.name.equals(name) && func.baseType.instance(baseType)) {
					if(func.inputVariables.size() != exp.inputs.size() + ((func.isDummy && !(func instanceof ASTFunctionNative)) ? 1 : 0))
						return null;
					resolver.typeVars = new HashMap<String, ASTType>();
					if(this.function.baseType instanceof ASTRecType) {
						ASTRecType rt = (ASTRecType)this.function.baseType;
						if(rt.typeVariables != null) {
							for(int i = 0; i<rt.typeVariables.size(); ++i) {
								ASTVariableType vt = (ASTVariableType)rt.typeVariables.get(i);
								resolver.typeVars.put(vt.var, vt);
							}
						}
					}
					if(func.baseType instanceof ASTRecType) {
						ASTRecType rt = (ASTRecType)func.baseType;
						if(rt.typeVariables != null) {
							for(int i = 0; i<rt.typeVariables.size(); ++i) {
								ASTVariableType vt = (ASTVariableType)rt.typeVariables.get(i);
								resolver.typeVars.put(vt.var, ((ASTRecType)baseType).typeVariables.get(i));
							}
						}
					}
					for(int i=0; i<exp.inputs.size(); ++i) {
						ASTType t = assertOne(visit(exp.inputs.get(i).right));
						if(!resolver.visit(func.inputVariables.get(i).left).match(t))
							return null;
					}
					return buildOne(func.returnType);
				}
			}
			return null;
		} else {
			System.err.println(obj+" is not a function!");
			return null;
		}
	}

	@Override
	public List<ASTType> visit(ASTRecExpression rec) {
		ASTType ty = assertOne(visit(rec.base));
		if(!(ty instanceof ASTRecType))
			return null;
		ASTRecType type = (ASTRecType)ty;
		return buildOne(type.fieldsType.get(rec.field));
	}

	@Override
	public List<ASTType> visit(ASTRecTupleExpression tuple) {
		ASTType ty = assertOne(visit(tuple.base));
		if(!(ty instanceof ASTRecType))
			return null;
		ASTRecType type = (ASTRecType)ty;
		Map<String, ASTType> old = new HashMap<String, ASTType>(this.variableMapping);
		for(Map.Entry<String, ASTType> ent : type.fieldsType.entrySet()) {
			this.variableMapping.put(ent.getKey(), ent.getValue());
		}
		List<ASTType> ret = visit((ASTTupleExpression)tuple);
		this.variableMapping = old;
		return ret;
	}

	@Override
	public List<ASTType> visit(ASTTupleExpression tuple) {
		List<ASTType> ret = new ArrayList<ASTType>();
		for(int i=0; i<tuple.exps.size(); ++i) {
			ASTType ty = assertOne(visit(tuple.exps.get(i)));
			if(ty == null) {
				visit(tuple.exps.get(i));
				return null;
			}
			ret.add(ty);
		}
		return ret;
	}

	@Override
	public List<ASTType> visit(ASTVariableExpression variableExpression) {
		if(variableExpression.var.equals("this")) 
			return buildOne(this.function.baseType);
		if(this.variableMapping.containsKey(variableExpression.var))
			return buildOne(this.variableMapping.get(variableExpression.var));
		else {
			for(Pair<ASTType, String> p : this.function.inputVariables)
				if(p.right.equals(variableExpression.var))
					return buildOne(p.left);
			for(Pair<ASTType, String> p : this.function.localVariables)
				if(p.right.equals(variableExpression.var))
					return buildOne(p.left);
		}
		return null;
	}

	@Override
	public List<ASTType> visit(ASTOrPredicate orPredicate) {
		ASTType ty = assertOne(visit(orPredicate.left));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty))
			return null;
		ASTIntType ty1 = (ASTIntType)ty;
		ty = assertOne(visit(orPredicate.right));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty))
			return null;
		ASTIntType ty2 = (ASTIntType)ty;
		return buildOne(ASTIntType.get(new ASTConstantExpression(1), ty1.getLabel().meet(ty2.getLabel())));
	}

	@Override
	public List<ASTType> visit(ASTAndPredicate andPredicate) {
		ASTType ty = assertOne(visit(andPredicate.left));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty))
			return null;
		ASTIntType ty1 = (ASTIntType)ty;
		ty = assertOne(visit(andPredicate.right));
		if(!ASTIntType.get(new ASTConstantExpression(1), ASTLabel.Secure).match(ty))
			return null;
		ASTIntType ty2 = (ASTIntType)ty;
		return buildOne(ASTIntType.get(new ASTConstantExpression(1), ty1.getLabel().meet(ty2.getLabel())));
	}

	@Override
	public List<ASTType> visit(ASTBinaryPredicate binaryPredicate) {
		ASTType ty = assertOne(visit(binaryPredicate.left));
		if(ty instanceof ASTIntType) {
			ASTIntType ty1 = (ASTIntType)ty;
			ty = assertOne(visit(binaryPredicate.right));
			if(!(ty instanceof ASTIntType))
				return null;
			ASTIntType ty2 = (ASTIntType)ty;
			if(!ty1.match(ty2))
				return null;
			return buildOne(ASTIntType.get(new ASTConstantExpression(1), ty1.getLabel().meet(ty2.getLabel())));
		} else if(ty instanceof ASTFloatType) {
			ASTFloatType ty1 = (ASTFloatType)ty;
			ty = assertOne(visit(binaryPredicate.right));
			if(!(ty instanceof ASTFloatType))
				return null;
			ASTFloatType ty2 = (ASTFloatType)ty;
			if(!ty1.match(ty2))
				return null;
			return buildOne(ASTIntType.get(new ASTConstantExpression(1), ty1.getLabel().meet(ty2.getLabel())));
		} else
			return null;
	}

	@Override
	public List<ASTType> visit(ASTNewObjectExpression exp) {
		for(Map.Entry<String, ASTExpression> ent : exp.valueMapping.entrySet()) {
			ASTType ty = assertOne(visit(ent.getValue()));
			if(!exp.type.fieldsType.containsKey(ent.getKey())
					|| !exp.type.fieldsType.get(ent.getKey()).match(ty))
				return null;
		}
		return buildOne(exp.type);
	}

	public void setContext(ASTProgram program, ASTFunctionDef function) {
		this.program = program;
		this.function = function;
		
		this.function = ((ASTFunctionDef)function);
		Map<String, ASTType> old = variableMapping;
		if(old == null)
			variableMapping = new HashMap<String, ASTType>();
		else
			variableMapping = new HashMap<String, ASTType>(old);
		if(function.baseType instanceof ASTRecType) {
			ASTRecType rt = (ASTRecType)function.baseType;
			for(ASTExpression e : rt.bitVariables)
				variableMapping.put(((ASTVariableExpression)e).var, ASTIntType.get(32, ASTLabel.Pub));
		}
		for(Pair<ASTType, String> v : function.inputVariables) {
			variableMapping.put(v.right, v.left);
		}
		for(Pair<ASTType, String> v : function.localVariables) {
			variableMapping.put(v.right, v.left);
		}

	}

	@Override
	public List<ASTType> visit(ASTFloatConstantExpression constantExpression) {
		return buildOne(ASTFloatType.get(constantExpression.bitSize, ASTLabel.Pub));
	}

	@Override
	public List<ASTType> visit(ASTLogExpression tuple) {
		ASTType ty = assertOne(visit(tuple.exp));
		if(!(ty instanceof ASTIntType))
			return null;
		return buildOne(ASTIntType.get(32, ASTLabel.Pub));
	}

	@Override
	public List<ASTType> visit(ASTRangeExpression tuple) {
		ASTType sty = assertOne(visit(tuple.source));
		if(!(sty instanceof ASTIntType))
			return null;
		ASTType lty = assertOne(visit(tuple.rangel));
		if(!(lty instanceof ASTIntType) && 
				((ASTIntType)lty).getLabel() == ASTLabel.Pub)
			return null;
		if(tuple.ranger != null) {
			ASTType rty = assertOne(visit(tuple.ranger));
			if(!(rty instanceof ASTIntType) && 
					((ASTIntType)rty).getLabel() == ASTLabel.Pub)
				return null;
		}
		return buildOne(ASTIntType.get(
				tuple.ranger == null ? new ASTConstantExpression(1) :
					new ASTBinaryExpression(
						tuple.ranger, BOP.SUB, tuple.rangel),
				((ASTIntType)sty).getLabel()
				));
	}

}
