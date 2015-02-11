package type.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Pair;
import ast.ASTFunction;
import ast.ASTFunctionDef;
import ast.ASTFunctionNative;
import ast.ASTProgram;
import ast.DefaultVisitor;
import ast.IFunctionVisitor;
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
import ast.expr.ASTPredicate;
import ast.expr.ASTRangeExpression;
import ast.expr.ASTRecExpression;
import ast.expr.ASTRecTupleExpression;
import ast.expr.ASTTupleExpression;
import ast.expr.ASTVariableExpression;
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
import ast.type.ASTNativeType;
import ast.type.ASTRecType;
import ast.type.ASTRndType;
import ast.type.ASTType;
import ast.type.ASTTypeVisitor;
import ast.type.ASTVariableType;
import ast.type.ASTVoidType;

public class TypeResolver extends DefaultVisitor<ASTStatement, ASTExpression, ASTType> implements ASTTypeVisitor<ASTType>, IFunctionVisitor<ASTFunction> {

	private ASTProgram program;

	private boolean isStructDef = false;

	public boolean copy = false;
	
	public Map<String, ASTType> typeVars;
	public Set<String> resolvingSet;

	public ASTProgram resolve(ASTProgram program) {
		this.program = program;
		typeVars = new HashMap<String, ASTType>();
		resolvingSet = new HashSet<String>();

		for(Pair<String, ASTType> type : program.typeDef) {
			if(type.right instanceof ASTRecType)
				this.isStructDef = true;
			type.right = visit(type.right);
			this.isStructDef = false;
		}

		for(List<ASTType> i : program.typeVarDef.values()) {
			if(i != null)
				for(ASTType ty : i) {
					if(!(ty instanceof ASTVariableType))
						throw new RuntimeException("Type parameters in definition must be variables!");
				}
		}

		for(int i=0; i<program.functionDef.size(); ++i) {
			program.functionDef.set(i, visit(program.functionDef.get(i)));
		}

		for(Pair<ASTFunctionType, ASTType> ent : program.functionTypeMapping) {
			ent.left = (ASTFunctionType)visit(ent.left);
		}
		return this.program;
	}

	@Override
	public ASTType visit(ASTArrayType type) {
		if(copy) {
			type = new ASTArrayType(visit(type.type), type.size, type.lab);
		} else {
			type.type = visit(type.type);
		}
		return type;
	}

	@Override
	public ASTType visit(ASTIntType type) {
		if(copy)
			return ASTIntType.get(type.getBits(), type.getLabel());
		else
			return type;
	}

	@Override
	public ASTType visit(ASTFloatType type) {
		if(copy)
			return ASTFloatType.get(type.getBits(), type.getLabel());
		else
			return type;
	}

	@Override
	public ASTType visit(ASTRndType type) {
		if(copy)
			return ASTRndType.get(type.getBits(), type.getLabel());
		else
			return type;
	}

	@Override
	public ASTType visit(ASTNativeType type) {
		if(copy)
			return new ASTNativeType(type.name, type.bitVariables);
		else
			return type;
	}

	@Override
	public ASTType visit(ASTRecType type) {
		if(resolvingSet.contains(type.name))
			throw new RuntimeException("Recursive type is forbidden!");

		ASTRecType ret = new ASTRecType(type.name, type.lab);
		ret.bitVariables = type.bitVariables;

		Map<String, ASTType> oldTypeVars = null;
		if(this.isStructDef) {
			if(type.typeVariables != null) {
				oldTypeVars = new HashMap<String, ASTType>(typeVars);
				ret.typeVariables = new ArrayList<ASTType>();
				for(ASTType i : type.typeVariables) {
					if(!(i instanceof ASTVariableType))
						throw new RuntimeException("Type parameters in definition must be variables!\n"+type.toString());
					ASTVariableType var = (ASTVariableType)i;
					ret.typeVariables.add(var);
					typeVars.put(var.var, var);
				}
			}
			resolvingSet.add(type.name);
			for(Map.Entry<String, ASTType> ent : type.fieldsType.entrySet()) {
				ret.fieldsType.put(ent.getKey(), visit(ent.getValue()));
			}
			for(String i : type.fields)
				ret.fields.add(i);
			if(type.typeVariables != null) {
				typeVars = oldTypeVars;
			}

			resolvingSet.remove(type.name);
		} else {
			if(type.typeVariables != null) {
				ret.typeVariables = new ArrayList<ASTType>();
				for(ASTType i : type.typeVariables) {
					ret.typeVariables.add(visit(i));
				}
			}
			for(Map.Entry<String, ASTType> ent : type.fieldsType.entrySet()) {
				ret.fieldsType.put(ent.getKey(), visit(ent.getValue()));
			}
			for(String i : type.fields)
				ret.fields.add(i);
		}


		return ret;
	}

	@Override
	public ASTType visit(ASTVariableType type) {
		if(resolvingSet.contains(type.var))
			throw new RuntimeException("Recursive type is forbidden.");
		if(this.typeVars.containsKey(type.var)) {
			if(type.typeVars != null)
				throw new RuntimeException("Type variable "+type.var+" cannot have type parameters in "+type.toString());
			return this.typeVars.get(type.var);
		}

		for(Pair<String, ASTType> ent : program.typeDef) {
			if(ent.left.equals(type.var)) {
				if(ent.right instanceof ASTArrayType) {
					ASTArrayType t = (ASTArrayType)ent.right;
					return new ASTArrayType(visit(t.type), t.size, t.lab);
				} else if(ent.right instanceof ASTIntType) {
					if(type.bitVars.size() >= 2)
						throw new RuntimeException("Too many bit variables for an int type!");
					ASTIntType ret = (ASTIntType)ent.right;
					ASTExpression bits = ret.getBits();
					if(type.bitVars.size() > 0 && bits == null)
						bits = type.bitVars.get(0);
					return ASTIntType.get(bits, ret.getLabel());
				} else if(ent.right instanceof ASTFloatType) {
					if(type.bitVars.size() >= 2)
						throw new RuntimeException("Too many bit variables for a float type!");
					return ASTFloatType.get(type.bitVars.size() == 0 ? null : type.bitVars.get(0), ((ASTFloatType)ent.right).getLabel());
				} else if(ent.right instanceof ASTRndType) {
					if(type.bitVars.size() >= 2)
						throw new RuntimeException("Too many bit variables for a rnd type!");
					return ASTRndType.get(type.bitVars.size() == 0 ? null : type.bitVars.get(0), ((ASTRndType)ent.right).getLabel());
				} else if(ent.right instanceof ASTNativeType) {
					return ent.right;
				} else if(ent.right instanceof ASTRecType) {
					ASTRecType t = (ASTRecType)ent.right;
					ASTRecType ret = new ASTRecType(t.name, t.lab);
					ret.bitVariables.addAll(type.bitVars);

					Map<String, ASTType> oldTypeVars = new HashMap<String, ASTType>(typeVars);
					if(!((type.typeVars == null && t.typeVariables == null) 
							|| (type.typeVars != null 
							&& t.typeVariables != null 
							&& type.typeVars.size() == t.typeVariables.size())))
						throw new RuntimeException("The number of type variables does not match.");

					if(t.typeVariables != null) {
						ret.typeVariables = new ArrayList<ASTType>();
						for(int i=0; i<t.typeVariables.size(); ++i)  {
							ret.typeVariables.add(visit(type.typeVars.get(i)));
						}
						for(int i=0; i<t.typeVariables.size(); ++i)  {
							if(!(t.typeVariables.get(i) instanceof ASTVariableType))
								throw new RuntimeException("Type parameters in definition must be variables!\n"+type.toString());
							ASTVariableType var = (ASTVariableType)t.typeVariables.get(i);
							typeVars.put(var.var, ret.typeVariables.get(i));
						}
					}
					ret.fields = new ArrayList<String>(t.fields);
					for(Map.Entry<String, ASTType> x : t.fieldsType.entrySet()) {
						ret.fieldsType.put(x.getKey(), visit(x.getValue()));
					}

					typeVars = oldTypeVars;
					return ret;
				} else if(ent.right instanceof ASTVariableType) {
					return visit(ent.right);
				} else if(ent.right instanceof ASTVoidType) {
					return ent.right;
				} else
					throw new RuntimeException("Unknown Type.");
			} else if (type.var.startsWith(ent.left)) {
				if(type.bitVars.size() != 0 || (!(ent.right instanceof ASTIntType) && !(ent.right instanceof ASTFloatType) && !(ent.right instanceof ASTRndType))) 
					continue;
				if(ent.right instanceof ASTIntType) {
					if (((ASTIntType)ent.right).getBits() != null)
						continue;
					ASTIntType it = (ASTIntType)ent.right;
					if(it.getBits() != null)
						continue;
					String suf = type.var.substring(ent.left.length());
					try {
						int bit = Integer.parseInt(suf);
						return ASTIntType.get(new ASTConstantExpression(bit), it.getLabel());
					} catch (Exception e) {
						continue;
					}
				} else if(ent.right instanceof ASTFloatType) {
					if (((ASTFloatType)ent.right).getBits() != null)
						continue;
					ASTFloatType it = (ASTFloatType)ent.right;
					if(it.getBits() != null)
						continue;
					String suf = type.var.substring(ent.left.length());
					try {
						int bit = Integer.parseInt(suf);
						return ASTFloatType.get(new ASTConstantExpression(bit), it.getLabel());
					} catch (Exception e) {
						continue;
					}
				} else if(ent.right instanceof ASTRndType) {
					if (((ASTRndType)ent.right).getBits() != null)
						continue;
					ASTRndType it = (ASTRndType)ent.right;
					if(it.getBits() != null)
						continue;
					String suf = type.var.substring(ent.left.length());
					try {
						int bit = Integer.parseInt(suf);
						return ASTRndType.get(new ASTConstantExpression(bit), it.getLabel());
					} catch (Exception e) {
						continue;
					}
				}
			}
		}
		throw new RuntimeException("Unresolved type: " + type.toString());
	}

	@Override
	public ASTType visit(ASTVoidType type) {
		return type;
	}

	public ASTFunction visit(ASTFunction func) {
		Map<String, ASTType> old = this.typeVars;
		if(!(func.baseType instanceof ASTVariableType) && func.baseType != null)
			throw new RuntimeException("Parser should parse the base type of a function as a ASTVariableType!");
		if(func.typeVariables != null)
			for(String s : func.typeVariables) {
				this.typeVars.put(s, new ASTVariableType(s));
			}
		if(func.baseType != null) {
			ASTVariableType type = (ASTVariableType)func.baseType;
			if(type.typeVars != null) {
				this.typeVars = new HashMap<String, ASTType>(this.typeVars);
				for(ASTType v : type.typeVars) {
					if(!(v instanceof ASTVariableType))
						throw new RuntimeException("Type parameters must be a string symbol!");
					this.typeVars.put(((ASTVariableType)v).var, v);
				}
			}
			func.baseType = visit(func.baseType);
		}
		for(int i=0; i<func.inputVariables.size(); ++i) {
			func.inputVariables.get(i).left = visit(func.inputVariables.get(i).left);
		}
//		if(func instanceof ASTFunctionDef) {
//			ASTFunctionDef funcDef = (ASTFunctionDef)func;
//			for(int i=0; i<funcDef.localVariables.size(); ++i) {
//				funcDef.localVariables.get(i).left = visit(funcDef.localVariables.get(i).left);
//			}
//		}
		func.returnType = visit(func.returnType);
		ASTFunction ret;
		if(func instanceof ASTFunctionNative) {
			ret = visit((ASTFunctionNative)func);
		} else if (func instanceof ASTFunctionDef) {
			ret = visit((ASTFunctionDef)func);
		} else
			throw new RuntimeException("Unknown function type.");

		this.typeVars = old;
		return ret;
	}

	public ASTFunction visit(ASTFunctionNative func) {
		return func;
	}

	public ASTFunction visit(ASTFunctionDef func) {
		List<Pair<ASTType, String>> localVariables = new ArrayList<Pair<ASTType, String>>();
		for(int i=0; i<func.localVariables.size(); ++i) {
			boolean a = true;
			for(int j=0; j<i; ++j)
				if(func.localVariables.get(i).right.equals(func.localVariables.get(j).right))
					a = false;
			if(a) {
				Pair<ASTType, String> pp = func.localVariables.get(i);
				localVariables.add(new Pair<ASTType, String>(visit(pp.left), pp.right));
			}
		}
		func.localVariables = localVariables;
		for(int i=0; i<func.body.size(); ++i)
			func.body.set(i, visit(func.body.get(i)));
		return func;
	}

	@Override
	public ASTStatement visit(ASTAssignStatement assignStatement) {
		assignStatement.expr = visit(assignStatement.expr);
		return assignStatement;
	}

	@Override
	public ASTStatement visit(ASTFuncStatement funcStatement) {
		ASTExpression stmt = visit(funcStatement.func);
		if(!(stmt instanceof ASTFuncExpression))
			throw new RuntimeException(stmt+" is not a function call!");
		funcStatement.func = (ASTFuncExpression)stmt;
		return funcStatement;
	}

	@Override
	public ASTStatement visit(ASTIfStatement ifStatement) {
		for(int i=0; i<ifStatement.trueBranch.size(); ++i)
			ifStatement.trueBranch.set(i, visit(ifStatement.trueBranch.get(i)));
		for(int i=0; i<ifStatement.falseBranch.size(); ++i)
			ifStatement.falseBranch.set(i, visit(ifStatement.falseBranch.get(i)));
		return ifStatement;
	}

	@Override
	public ASTStatement visit(ASTReturnStatement returnStatement) {
		returnStatement.exp = visit(returnStatement.exp);
		return returnStatement;
	}

	@Override
	public ASTStatement visit(ASTWhileStatement whileStatement) {
		for(int i=0; i<whileStatement.body.size(); ++i)
			whileStatement.body.set(i, visit(whileStatement.body.get(i)));
		return whileStatement;
	}

	@Override
	public ASTExpression visit(ASTAndPredicate andPredicate) {
		andPredicate.left = (ASTPredicate)visit(andPredicate.left);
		andPredicate.right = (ASTPredicate)visit(andPredicate.right);
		return andPredicate;
	}

	@Override
	public ASTExpression visit(ASTArrayExpression arrayExpression) {
		arrayExpression.var = visit(arrayExpression.var);
		arrayExpression.indexExpr = visit(arrayExpression.indexExpr);
		return arrayExpression;
	}

	@Override
	public ASTExpression visit(ASTBinaryExpression binaryExpression) {
		binaryExpression.left = visit(binaryExpression.left);
		binaryExpression.right = visit(binaryExpression.right);
		return binaryExpression;
	}

	@Override
	public ASTExpression visit(ASTBinaryPredicate binaryPredicate) {
		binaryPredicate.left = visit(binaryPredicate.left);
		binaryPredicate.right = visit(binaryPredicate.right);
		return binaryPredicate;
	}

	@Override
	public ASTExpression visit(ASTConstantExpression constantExpression) {
		return constantExpression;
	}

	@Override
	public ASTExpression visit(ASTFuncExpression funcExpression) {
		if(funcExpression.typeVars != null) {
			for(int i=0; i<funcExpression.typeVars.size(); ++i)
				funcExpression.typeVars.set(i, visit(funcExpression.typeVars.get(i)));
		}
		for(Pair<String, ASTExpression> ent : funcExpression.inputs) {
			ent.right = visit(ent.right);
		}
		if(!(funcExpression.obj instanceof ASTVariableExpression))
			return funcExpression;
		ASTVariableExpression exp = (ASTVariableExpression)funcExpression.obj;
		if(exp.var.equals("log") && funcExpression.bitParameters.size() == 0 
				&& funcExpression.typeVars == null && funcExpression.inputs.size() == 1) {
			return new ASTLogExpression(funcExpression.inputs.get(0).right);
		}
		// TODO Security Label
		ASTVariableType tmp = new ASTVariableType(exp.var);
		tmp.bitVars = funcExpression.bitParameters;
		tmp.typeVars = funcExpression.typeVars;
		ASTType ty;
		try {
			ty = visit(tmp);
		} catch(Exception e) {
			return funcExpression;
		}
		if(ty instanceof ASTRecType) {
			ASTRecType type = (ASTRecType)ty;
			Map<String, ASTExpression> initialValue = new HashMap<String, ASTExpression>();
			if(funcExpression.inputs.size() == 0) {
				if(type.fields.size() != 0)
					throw new RuntimeException("Type "+tmp.var+" has "+type.fields.size()+" fields!");
				return new ASTNewObjectExpression(type, initialValue);
			}
			boolean withField = funcExpression.inputs.get(0).left != null;
			if(withField) {
				for(Pair<String, ASTExpression> ent : funcExpression.inputs) {
					if(ent.left == null) {
						throw new RuntimeException("Fields in the type constructor "+type.shortName()+" are missing for "+ent.right+"!");
					}
					initialValue.put(ent.left, visit(ent.right));
				}
				return new ASTNewObjectExpression(type, initialValue);
			} else {
				if(funcExpression.inputs.size() != type.fields.size())
					throw new RuntimeException("Type "+tmp.var+" has "+type.fields.size()+" fields!");
				for(int i=0; i<type.fields.size(); ++i)
					initialValue.put(type.fields.get(i), visit(funcExpression.inputs.get(i).right));
				return new ASTNewObjectExpression(type, initialValue);
			}
		}
		return funcExpression;
	}

	@Override
	public ASTExpression visit(ASTOrPredicate orPredicate) {
		orPredicate.left = (ASTPredicate)visit(orPredicate.left);
		orPredicate.right = (ASTPredicate)visit(orPredicate.right);
		return orPredicate;
	}

	@Override
	public ASTExpression visit(ASTRecExpression rec) {
		rec.base = visit(rec.base);
		return rec;
	}

	@Override
	public ASTExpression visit(ASTRecTupleExpression tuple) {
		tuple.base = visit(tuple.base);
		for(int i=0; i<tuple.exps.size(); ++i)
			tuple.exps.set(i, tuple.exps.get(i));
		return tuple;
	}

	@Override
	public ASTExpression visit(ASTTupleExpression tuple) {
		for(int i=0; i<tuple.exps.size(); ++i)
			tuple.exps.set(i, tuple.exps.get(i));
		return tuple;
	}

	@Override
	public ASTExpression visit(ASTVariableExpression variableExpression) {
		return variableExpression;
	}

	@Override
	public ASTExpression visit(ASTNewObjectExpression exp) {
		ASTVariableType vt = new ASTVariableType(exp.type.name);
		vt.bitVars = exp.type.bitVariables;
		vt.typeVars = exp.type.typeVariables;
		ASTType type = visit(vt);
		if(!(type instanceof ASTRecType)) {
			throw new RuntimeException(vt.var+" is not a record type!");
		}
		exp.type = (ASTRecType)type;
		for(Map.Entry<String, ASTExpression> ent : exp.valueMapping.entrySet()) {
			ent.setValue(visit(ent.getValue()));
		}
		return exp;
		//		throw new RuntimeException("Should have new object expression from the parser!");
	}

	@Override
	public ASTType visit(ASTFunctionType type) {
		type.returnType = visit(type.returnType);
		for(int i=0; i<type.inputTypes.size(); ++i)
			type.inputTypes.set(i, visit(type.inputTypes.get(i)));
		return type;
	}

	@Override
	public ASTExpression visit(ASTFloatConstantExpression constantExpression) {
		return constantExpression;
	}

	@Override
	public ASTExpression visit(ASTLogExpression exp) {
		exp.exp = visit(exp.exp);
		return exp;
	}

	@Override
	public ASTExpression visit(ASTRangeExpression exp) {
		exp.source = visit(exp.source);
		exp.rangel = visit(exp.rangel);
		if(exp.ranger != null)
			exp.ranger = visit(exp.ranger);
		return exp;
	}

}
