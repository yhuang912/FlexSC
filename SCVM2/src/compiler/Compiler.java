package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import backend.flexsc.Config;

import scvm.ArrayAssign;
import scvm.ArrayExp;
import scvm.Assign;
import scvm.BopExp;
import scvm.BopExp.Op;
import scvm.ConstExp;
import scvm.EnforceBitExp;
import scvm.Expression;
import scvm.FuncCallExp;
import scvm.If;
import scvm.LocalFuncCallExp;
import scvm.LogExp;
import scvm.MuxExp;
import scvm.NewObjExp;
import scvm.RangeAssign;
import scvm.RangeExp;
import scvm.RecExp;
import scvm.RecordAssign;
import scvm.Ret;
import scvm.ReverseRecordAssign;
import scvm.RopExp;
import scvm.SCVMCode;
import scvm.Seq;
import scvm.Skip;
import scvm.UnaryOpExp;
import scvm.VarExp;
import scvm.Variable;
import scvm.While;
import type.manage.ArrayType;
import type.manage.BOPVariableConstant;
import type.manage.BitVariable;
import type.manage.Constant;
import type.manage.FloatType;
import type.manage.FunctionType;
import type.manage.IntType;
import type.manage.Label;
import type.manage.LogVariable;
import type.manage.Method;
import type.manage.NativeType;
import type.manage.RecordType;
import type.manage.RndType;
import type.manage.Type;
import type.manage.TypeManager;
import type.manage.VariableConstant;
import type.manage.VariableType;
import type.manage.VoidType;
import type.source.TypeChecker;
import util.Pair;
import ast.ASTFunction;
import ast.ASTFunctionDef;
import ast.ASTFunctionNative;
import ast.ASTProgram;
import ast.DefaultVisitor;
import ast.expr.ASTAndPredicate;
import ast.expr.ASTArrayExpression;
import ast.expr.ASTBinaryExpression;
import ast.expr.ASTBinaryExpression.BOP;
import ast.expr.ASTBinaryPredicate;
import ast.expr.ASTBinaryPredicate.REL_OP;
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
import ast.type.ASTVariableType;
import ast.type.ASTVoidType;

public class Compiler extends DefaultVisitor<SCVMCode, Pair<List<Variable>, SCVMCode>, Type> {

	private TypeChecker tc = new TypeChecker();
	private BitInferenceEngine bie = new BitInferenceEngine();

	private ASTProgram program;
	private ASTFunctionDef function;

	public Map<String, Variable> variableValues;

	public Compiler() {
	}

	private int varNum = 0;

	private String newTempVar() {
		return "__tmp"+(varNum++);
	}

	private Variable currentCond = null;

	private boolean phantom = true;
	
	public TypeManager translate(ASTProgram program) {
		TypeManager tm = new TypeManager();
		this.program = program;
		this.bie.process(program);

		for(Pair<String, ASTType> i : program.typeDef) {
			if((i.right instanceof ASTRecType) 
					&& ((ASTRecType)i.right).name.equals(i.left)) {
				tm.put(i.left, visit(i.right));
			}
		}

		Map<String, Variable> func = new HashMap<String, Variable>();
		for(ASTFunction function : program.functionDef) {
			if(function.baseType == null) {
				Type ty = visit(function.getType());
				func.put(function.name, new Variable(ty, Label.Pub, function.name));
			}
		}

		for(ASTFunction function : program.functionDef) {
			if(function instanceof ASTFunctionDef) {
				this.function = (ASTFunctionDef)function;
				Type baseType;
				if(this.function.baseType == null)
					baseType = null;
				else
					baseType = visit(this.function.baseType);
				List<Pair<Type, String>> inputs = new ArrayList<Pair<Type, String>>();
				for(Pair<ASTType, String> arg : this.function.inputVariables) {
					inputs.add(new Pair<Type, String>(visit(arg.left), arg.right));				
				}
				List<Pair<Type, String>> local = new ArrayList<Pair<Type, String>>();
				for(Pair<ASTType, String> arg : this.function.localVariables) {
					local.add(new Pair<Type, String>(visit(arg.left), arg.right));				
				}
				Method method = new Method(baseType, 
						visit(this.function.returnType),
						this.function.name,
						function.bitParameter,
						inputs, local);
				if(function.typeVariables != null) {
					for(int i=0; i<function.typeVariables.size(); ++i) {
						method.typeParameters.add(new VariableType(function.typeVariables.get(i)));
					}
				}
				method.code = new Skip();
				this.variableValues = new HashMap<String, Variable>(func);
				if(baseType != null) {
					this.variableValues.put("this", new Variable(baseType, baseType.getLabel(), "this"));
					if(baseType instanceof RecordType) {
						RecordType rt = (RecordType)baseType;
						for(VariableConstant vc : rt.bits) {
							String s = ((BitVariable)vc).var;
							this.variableValues.put(s, new Variable(new IntType(32, Label.Pub), Label.Pub, s));
						}
					}
				}
				for(String v : this.function.bitParameter) {
					this.variableValues.put(v, new Variable(new IntType(32, Label.Pub), Label.Pub, v));
				}
				for(Pair<ASTType, String> arg : this.function.inputVariables) {
					Type type = visit(arg.left);
					this.variableValues.put(arg.right, new Variable(type, type.getLabel(), arg.right));
				}
				for(Pair<ASTType, String> arg : this.function.localVariables) {
					Type type = visit(arg.left);
					this.variableValues.put(arg.right, new Variable(type, type.getLabel(), arg.right));
				}
				this.phantom = function.isDummy;
				method.isPhantom = phantom;
				for(ASTStatement stmt : this.function.body)
					method.code = Seq.seq(method.code, visit(stmt));
				tm.addMethod(baseType, method);
			}
		}

		for(Pair<ASTFunctionType, ASTType> ent : program.functionTypeMapping) {
			tm.add((FunctionType)visit(ent.left), ent.right);
		}

		return tm;
	}

	public VariableConstant constructConstant(ASTExpression exp) {
		if(exp instanceof ASTLogExpression) {
			return new LogVariable(constructConstant(((ASTLogExpression)exp).exp));
		} if(exp instanceof ASTConstantExpression) {
			return new Constant(((ASTConstantExpression)exp).value);
		} else if(exp instanceof ASTVariableExpression) {
			return new BitVariable(((ASTVariableExpression)exp).var);
		} else if(exp instanceof ASTBinaryExpression) {
			ASTBinaryExpression be = (ASTBinaryExpression)exp;
			VariableConstant left = constructConstant(be.left);
			VariableConstant right = constructConstant(be.right);
			Op op = null;
			if(left == null || right == null)
				return null;
			if(be.op == ASTBinaryExpression.BOP.ADD)
				op = Op.Add;
			else if(be.op == ASTBinaryExpression.BOP.SUB)
				op = Op.Sub;
			else if(be.op == ASTBinaryExpression.BOP.MUL)
				op = Op.Mul;
			else if(be.op == ASTBinaryExpression.BOP.DIV)
				op = Op.Div;
			else if(be.op == ASTBinaryExpression.BOP.MOD)
				op = Op.Mod;
			else if(be.op == ASTBinaryExpression.BOP.AND)
				op = Op.And;
			else if(be.op == ASTBinaryExpression.BOP.OR)
				op = Op.Or;
			else if(be.op == ASTBinaryExpression.BOP.XOR)
				op = Op.Xor;
			else if(be.op == ASTBinaryExpression.BOP.SHL)
				op = Op.Shl;
			else if(be.op == ASTBinaryExpression.BOP.SHR)
				op = Op.Shr;
			if(op == null)
				return null;
			return new BOPVariableConstant(left, op, right);
		}
		return null;
	}

	@Override
	public Type visit(ASTArrayType type) {
		return new ArrayType(constructConstant(type.size), Label.get(type.lab), visit(type.type));
	}

	@Override
	public Type visit(ASTIntType type) {
		return new IntType(constructConstant(type.getBits()), Label.get(type.getLabel()));
	}

	@Override
	public Type visit(ASTNativeType type) {
		// TODO raw name
		return new NativeType("", type.name);
	}

	@Override
	public Type visit(ASTRecType type) {
		List<VariableConstant> vc = new ArrayList<VariableConstant>();
		for(int i = 0; i < type.bitVariables.size(); ++i)
			vc.add(this.constructConstant(type.bitVariables.get(i)));
		RecordType ret = new RecordType(type, vc);
		if(type.typeVariables != null) {
			for(ASTType ty : type.typeVariables) {
				ret.typeParameter.add(visit(ty));
			}
		}
		for(String s : type.fields) {
			ret.fields.put(s, visit(type.fieldsType.get(s)));
		}
		return ret;
	}

	@Override
	public Type visit(ASTVariableType type) {
		return new VariableType(type.var);
	}

	@Override
	public Type visit(ASTVoidType type) {
		return VoidType.get();
	}

	private static Variable phantomVariable = null;
	
	private static Variable getPhantomVariable() {
		if(phantomVariable == null) {
			phantomVariable =
					new Variable(new IntType(1, Label.Secure), Label.Secure, Config.phantomVariable);
		}
		return phantomVariable;
	}
	
	public SCVMCode translateAssign(ASTArrayExpression exp, Variable value) {
		Pair<List<Variable>, SCVMCode> tmp = visit(exp.var);
		SCVMCode code = tmp.right;
		Variable v = tmp.left.get(0);
		tmp = visit(exp.indexExpr);
		code = Seq.seq(code, tmp.right);
		Variable i = tmp.left.get(0);
		if(this.currentCond == null && !this.phantom) {
			code = Seq.seq(code,
					new ArrayAssign(v.lab, v, i, value));
		} else {
			Type type = ((ArrayType)v.type).type;
			Variable dumb = new Variable(type, type.getLabel(), newTempVar());
			code = Seq.seq(code, new Assign(dumb.lab, dumb, new ArrayExp(v, i)));
			Variable dumb1 = new Variable(type, Label.Secure, newTempVar());
			code = Seq.seq(code, new Assign(Label.Secure, dumb1, 
					new MuxExp(this.currentCond == null? getPhantomVariable() : this.currentCond, value, dumb)));
			code = Seq.seq(code,
					new ArrayAssign(v.lab, v, i, dumb1));
		}
		return code;
	}

	public SCVMCode translateAssign(ASTRangeExpression exp, Variable value) {
		Pair<List<Variable>, SCVMCode> tmp = visit(exp.source);
		SCVMCode code = tmp.right;
		Variable v = tmp.left.get(0);
		VariableConstant ll = this.constructConstant(exp.rangel);
		VariableConstant rr = exp.ranger == null ? null : this.constructConstant(exp.ranger);
		if(this.currentCond == null && !this.phantom) {
			code = Seq.seq(code,
					new RangeAssign(v.lab, v, ll, rr, value));
		} else {
			Type type = new IntType(
					rr == null ? new Constant(1) : new BOPVariableConstant(rr, Op.Sub, ll), 
							v.lab);
			Variable dumb = new Variable(type, type.getLabel(), newTempVar());
			code = Seq.seq(code, new Assign(dumb.lab, dumb, new RangeExp(dumb.lab, v, ll, rr)));
			Variable dumb1 = new Variable(type, Label.Secure, newTempVar());
			code = Seq.seq(code, new Assign(Label.Secure, dumb1, new MuxExp(
					this.currentCond == null ? getPhantomVariable() : currentCond, value, dumb)));
			code = Seq.seq(code,
					new RangeAssign(v.lab, v, ll, rr, dumb1));
		}
		return code;
	}

	private SCVMCode translateAssign(Variable base, String field, Variable value) {
		if(this.currentCond == null && !this.phantom) {
			return new RecordAssign(base.type.getLabel(), base, field, value);
		} else {
			Type type = ((RecordType)base.type).fields.get(field);
			Variable dumb = new Variable(type, type.getLabel(), newTempVar());
			SCVMCode code = new Assign(dumb.lab, dumb, new RecExp(type.getLabel(), base, field));
			Variable dumb1 = new Variable(type, Label.Secure, newTempVar());
			code = Seq.seq(code, new Assign(Label.Secure, dumb1, 
					new MuxExp(this.currentCond == null ? getPhantomVariable() : currentCond, value, dumb)));
			code = Seq.seq(code,
					new RecordAssign(base.type.getLabel(), base, field, dumb1));
			return code;
		}
	}

	public SCVMCode translateAssign(ASTRecExpression exp, Variable value) {
		Pair<List<Variable>, SCVMCode> tmp = visit(exp.base);
		SCVMCode code = tmp.right;
		return Seq.seq(code, translateAssign(tmp.left.get(0), exp.field, value));
	}

	public SCVMCode translateAssign(ASTTupleExpression exp, List<Variable> values) {
		SCVMCode code = new Skip();
		for(int i=0; i<exp.exps.size(); ++i) {
			Variable value = values.get(i);
			ASTExpression var = exp.exps.get(i);
			if(var instanceof ASTArrayExpression) {
				code = Seq.seq(code, translateAssign((ASTArrayExpression)var, value));
			} else if(var instanceof ASTRecExpression) {
				code = Seq.seq(code, translateAssign((ASTRecExpression)var, value));
			} else if(var instanceof ASTVariableExpression) {
				code = Seq.seq(code, translateAssign((ASTVariableExpression)var, value));
			} else
				throw new RuntimeException("Shouldn't reach here!");
		}
		return code;
	}

	public SCVMCode translateAssign(ASTRecTupleExpression exp, List<Variable> values) {
		Pair<List<Variable>, SCVMCode> tmp = visit(exp);
		SCVMCode code = tmp.right;
		Variable v = tmp.left.get(0);
		for(int i=0; i<exp.exps.size(); ++i) {
			ASTVariableExpression var = (ASTVariableExpression)exp.exps.get(i);
			code = Seq.seq(code, translateAssign(v, var.var, values.get(i)));
		}
		return code;
	}

	public SCVMCode translateAssign(ASTVariableExpression exp, Variable value) {
		Pair<List<Variable>, SCVMCode> tmp = visit(exp);
		SCVMCode code = tmp.right;
		Variable v = tmp.left.get(0);
		if(this.currentCond == null && (!this.phantom || v.lab == Label.Pub)) {
			code = Seq.seq(code,
					new Assign(v.type.getLabel(), v, new VarExp(value)));
		} else {
			Type type = v.type;
			Variable dumb = new Variable(type, Label.Secure, newTempVar());
			code = Seq.seq(code, new Assign(Label.Secure, dumb, 
					new MuxExp(this.currentCond == null ? getPhantomVariable() : currentCond, value, v)));
			code = Seq.seq(code,
					new Assign(v.type.getLabel(), v, new VarExp(dumb)));
		}
		return code;
	}

	@Override
	public SCVMCode visit(ASTAssignStatement stmt) {
		Pair<List<Variable>, SCVMCode> res = visit(stmt.expr);
		SCVMCode code = res.right;
		if(res.left.size() == 1) {
			Variable value = res.left.get(0);
			if(stmt.var instanceof ASTArrayExpression) {
				return Seq.seq(code, translateAssign((ASTArrayExpression)stmt.var, value));
			} else if(stmt.var instanceof ASTRecExpression) {
				return Seq.seq(code, translateAssign((ASTRecExpression)stmt.var, value));
			} else if(stmt.var instanceof ASTVariableExpression) {
				return Seq.seq(code, translateAssign((ASTVariableExpression)stmt.var, value));
			} else if(stmt.var instanceof ASTRangeExpression) {
				return Seq.seq(code, translateAssign((ASTRangeExpression)stmt.var, value));
			} else
				throw new RuntimeException("Shouldn't reach here!");
		} else {
			if(stmt.var instanceof ASTRecTupleExpression) {
				return Seq.seq(code, translateAssign((ASTRecTupleExpression)stmt.var, res.left));
			} else if(stmt.var instanceof ASTTupleExpression) {
				return Seq.seq(code, translateAssign((ASTTupleExpression)stmt.var, res.left));
			} else
				throw new RuntimeException("Shouldn't reach here!");
		}
	}

	@Override
	public SCVMCode visit(ASTFuncStatement funcStatement) {
		Pair<List<Variable>, SCVMCode> value = visit(funcStatement.func);
		return value.right;
	}

	@Override
	public SCVMCode visit(ASTIfStatement ifStatement) {
		Pair<List<Variable>, SCVMCode> value = visit(ifStatement.cond);
		if(value.left.get(0).lab == Label.Secure || currentCond != null) {
			SCVMCode ret = value.right;
			Variable old = this.currentCond;
			if(currentCond == null && this.phantom)
				this.currentCond = getPhantomVariable();
			if(currentCond == null) {
				this.currentCond = value.left.get(0);
			} else {
				Variable var = new Variable(new IntType(1, Label.Secure), Label.Secure, newTempVar());
				ret = Seq.seq(ret, new Assign(Label.Secure, var, new BopExp(this.currentCond, BopExp.Op.And, value.left.get(0))));
				this.currentCond = var;
			}
			SCVMCode tb = new Skip();
			for(int i=0; i<ifStatement.trueBranch.size(); ++i)
				tb = Seq.seq(tb, visit(ifStatement.trueBranch.get(i)));
			ret = Seq.seq(ret, tb);

			this.currentCond = old;

			if(ifStatement.falseBranch.size() > 0) {
				if(currentCond == null && this.phantom)
					this.currentCond = getPhantomVariable();
				Variable neg = new Variable(new IntType(1, Label.Secure), Label.Secure, newTempVar());
				ret = Seq.seq(ret, new Assign(Label.Secure, neg, new UnaryOpExp(UnaryOpExp.Op.Neg, value.left.get(0))));
				if(currentCond == null) {
					this.currentCond = neg;
				} else {
					Variable var = new Variable(new IntType(1, Label.Secure), Label.Secure, newTempVar());
					ret = Seq.seq(ret, new Assign(Label.Secure, var, new BopExp(this.currentCond, BopExp.Op.And, neg)));
					this.currentCond = var;
				}
				SCVMCode fb = new Skip();
				for(int i=0; i<ifStatement.falseBranch.size(); ++i)
					fb = Seq.seq(fb, visit(ifStatement.falseBranch.get(i)));
				ret = Seq.seq(ret, fb);
				this.currentCond = old;
			}
			return ret;
		} else {
			SCVMCode tb = new Skip();
			for(int i=0; i<ifStatement.trueBranch.size(); ++i)
				tb = Seq.seq(tb, visit(ifStatement.trueBranch.get(i)));
			SCVMCode fb = new Skip();
			for(int i=0; i<ifStatement.falseBranch.size(); ++i)
				fb = Seq.seq(fb, visit(ifStatement.falseBranch.get(i)));
			return Seq.seq(value.right, 
					new If(value.left.get(0).lab, value.left.get(0), tb, fb));
		}
	}

	@Override
	public SCVMCode visit(ASTReturnStatement returnStatement) {
		Pair<List<Variable>, SCVMCode> t = visit(returnStatement.exp);
		return Seq.seq(t.right, new Ret(t.left.get(0)));
	}

	@Override
	public SCVMCode visit(ASTWhileStatement whileStatement) {
		Pair<List<Variable>, SCVMCode> value = visit(whileStatement.cond);
		if(value.left.get(0).lab == Label.Secure || currentCond != null) {
			throw new RuntimeException("while loop cannot appear within a secure context.");
		} else {
			SCVMCode tb = new Skip();
			for(int i=0; i<whileStatement.body.size(); ++i)
				tb = Seq.seq(tb, visit(whileStatement.body.get(i)));
			return Seq.seq(value.right, 
					new While(value.left.get(0).lab, value.left.get(0), 
							Seq.seq(tb, value.right.clone(false))));
		}
	}

	public List<Variable> one(Variable var) {
		List<Variable> ret = new ArrayList<Variable>();
		ret.add(var);
		return ret;
	}

	public Pair<List<Variable>, SCVMCode> visit(ASTExpression exp) {
		Pair<List<Variable>, SCVMCode> pair = super.visit(exp);
		if(exp.targetBits == null 
				|| this.constructConstant(exp.targetBits).equals(pair.left.get(0).getBits())
				|| pair.left.size() > 1
				|| !(pair.left.get(0).type instanceof IntType)) {
			return pair;
		} else {
			Variable v = pair.left.get(0);
			Variable var = new Variable(
					new IntType(this.constructConstant(exp.targetBits), v.lab), 
					v.lab, 
					newTempVar());
			return new Pair<List<Variable>, SCVMCode>(one(var),
					Seq.seq(pair.right,
							new Assign(var.lab, var, new EnforceBitExp(v, var.getBits()))
							));
		}
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTAndPredicate andPredicate) {
		Pair<List<Variable>, SCVMCode> left = visit(andPredicate.left);
		Pair<List<Variable>, SCVMCode> right = visit(andPredicate.right);
		Variable lv = left.left.get(0);
		Variable rv = right.left.get(0);
		Variable var = new Variable(lv.type, lv.lab.meet(rv.lab), newTempVar());

		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(Seq.seq(left.right, right.right),
						new Assign(var.lab, var, new BopExp(lv, BopExp.Op.And, rv))
						));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTArrayExpression exp) {
		Pair<List<Variable>, SCVMCode> array = visit(exp.var);
		Pair<List<Variable>, SCVMCode> idx = visit(exp.indexExpr);
		Variable av = array.left.get(0);
		Variable iv = idx.left.get(0);
		Variable var = new Variable(((ArrayType)av.type).type, ((ArrayType)av.type).indexLab.lab.meet(((ArrayType)av.type).getLabel()), newTempVar());
		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(Seq.seq(array.right, idx.right),
						new Assign(var.lab, var, new ArrayExp(av, iv))
						));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTBinaryExpression exp) {
		Pair<List<Variable>, SCVMCode> left = visit(exp.left);
		Variable lv = left.left.get(0);
		if(exp.op == BOP.SHL) {
			Pair<List<Variable>, SCVMCode> right = visit(exp.right);
			Variable var = new Variable(lv.type, lv.lab, newTempVar());
			return new Pair<List<Variable>, SCVMCode>(one(var), 
					Seq.seq(Seq.seq(left.right, right.right),
							new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Shl, right.left.get(0)))
							));
		} else if(exp.op == BOP.SHR) {
			Pair<List<Variable>, SCVMCode> right = visit(exp.right);
			Variable var = new Variable(lv.type, lv.lab, newTempVar());
			return new Pair<List<Variable>, SCVMCode>(one(var), 
					Seq.seq(Seq.seq(left.right, right.right),
							new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Shr, right.left.get(0)))
							));
		}

		Pair<List<Variable>, SCVMCode> right = visit(exp.right);
		Variable rv = right.left.get(0);
		if(!(lv.type instanceof IntType) && !(lv.type instanceof FloatType)) {
			throw new RuntimeException("Binary expression can be operated between only int values or float values.");
		}
		Variable var = new Variable(lv.type, lv.lab.meet(rv.lab), newTempVar());

		SCVMCode assign;
		if(exp.op == BOP.ADD) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Add, rv));
		} else if(exp.op == BOP.SUB) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Sub, rv));
		} else if(exp.op == BOP.MUL) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Mul, rv));
		} else if(exp.op == BOP.DIV) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Div, rv));
		} else if(exp.op == BOP.MOD) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Mod, rv));
		} else if(exp.op == BOP.AND) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.And, rv));
		} else if(exp.op == BOP.OR) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Or, rv));
		} else if(exp.op == BOP.XOR) {
			assign = new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Xor, rv));
		} else
			throw new RuntimeException("Unsupported binary operation! "+exp.toString());

		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(Seq.seq(left.right, right.right), assign));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTBinaryPredicate exp) {
		Pair<List<Variable>, SCVMCode> left = visit(exp.left);
		Variable lv = left.left.get(0);

		Pair<List<Variable>, SCVMCode> right = visit(exp.right);
		Variable rv = right.left.get(0);
		Label lab = lv.lab.meet(rv.lab);

		IntType lty = (IntType)lv.type;
		IntType rty = (IntType)rv.type;

		if(lty.bit == null) lty.bit = rty.bit;
		if(rty.bit == null) rty.bit = lty.bit;

		Variable var = new Variable(new IntType(1, lab), lab, newTempVar());

		SCVMCode assign;
		if(exp.op == REL_OP.EQ) {
			if(lv.getBits().isConstant(1) && exp.right instanceof ASTConstantExpression) {
				right.right = new Skip();
				ASTConstantExpression ce = (ASTConstantExpression)exp.right;
				int cn = ce.value;
				if(cn != 0) {
					assign = new Assign(var.lab, var, new VarExp(lv));
				} else {
					assign = new Assign(var.lab, var, new UnaryOpExp(UnaryOpExp.Op.Neg, lv));
				}
			} else
				assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Eq, rv));
		} else if(exp.op == REL_OP.NEQ) {
			if(lv.getBits().isConstant(1) && exp.right instanceof ASTConstantExpression) {
				right.right = new Skip();
				ASTConstantExpression ce = (ASTConstantExpression)exp.right;
				int cn = ce.value;
				if(cn == 0) {
					assign = new Assign(var.lab, var, new VarExp(lv));
				} else {
					assign = new Assign(var.lab, var, new UnaryOpExp(UnaryOpExp.Op.Neg, lv));
				}
			} else
				assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Ne, rv));
		} else if(exp.op == REL_OP.GT) {
			assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Gt, rv));
		} else if(exp.op == REL_OP.GET) {
			assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Ge, rv));
		} else if(exp.op == REL_OP.LT) {
			assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Lt, rv));
		} else if(exp.op == REL_OP.LET) {
			assign = new Assign(var.lab, var, new RopExp(lv, RopExp.Op.Le, rv));
		} else
			throw new RuntimeException("Unsupported binary comparison! "+exp.toString());

		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(Seq.seq(left.right, right.right), assign));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTConstantExpression exp) {
		VariableConstant bits = this.constructConstant(exp.bitSize);
		Variable var = new Variable(new IntType(bits, Label.Pub), Label.Pub, newTempVar());
		return new Pair<List<Variable>, SCVMCode>(one(var),
				new Assign(Label.Pub, var, new ConstExp(exp.value, bits)));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTFuncExpression funcExpression) {
		SCVMCode code = new Skip();
		List<Variable> input = new ArrayList<Variable>();
		for(int i=0; i<funcExpression.inputs.size(); ++i) {
			Pair<List<Variable>, SCVMCode> tmp = visit(funcExpression.inputs.get(i).right);
			input.addAll(tmp.left);
			code = Seq.seq(code, tmp.right);
		}

		ASTExpression obj = funcExpression.obj;
		if(obj instanceof ASTVariableExpression) {
			String name = ((ASTVariableExpression)obj).var;
			for(Pair<ASTType, String> inputs : this.function.inputVariables) {
				if((inputs.left instanceof ASTFunctionType) && inputs.right.equals(name)) {
					ASTFunctionType fty = (ASTFunctionType)inputs.left;

					// TODO handle dummy
					if(this.currentCond != null)
						throw new RuntimeException("Function Pointers can be called only in low context.");

					Type ty = visit(fty.returnType);
					Expression exp =  new LocalFuncCallExp(ty, name, input);
					Variable var = new Variable(ty, ty.getLabel(), newTempVar());
					return new Pair<List<Variable>, SCVMCode>(one(var),
							Seq.seq(code, new Assign(var.lab, var, exp)));
				}
			}
			for(ASTFunction func : program.functionDef) {
				if(func.name.equals(name) && func.baseType == null) {
					Type ty = visit(func.returnType);
					if(func.isDummy) {
						if(this.currentCond == null) {
							Variable var = new Variable(new IntType(1, Label.Secure), Label.Secure, newTempVar());
							code = Seq.seq(code, new Assign(var.lab, var, new ConstExp(1, new Constant(1))));
							input.add(var);
						} else {
							input.add(currentCond);
						}
					}
					Expression exp;
					List<VariableConstant> bits = new ArrayList<VariableConstant>();
					for(int i=0; i<funcExpression.bitParameters.size(); ++i)
						bits.add(this.constructConstant(funcExpression.bitParameters.get(i)));
					if(func instanceof ASTFunctionNative)
						exp = new FuncCallExp((FunctionType)visit(program.getFunctionType(name)), ty.getLabel(), ty, 
								((ASTFunctionNative)func).nativeName, bits, input, true);
					else
						exp = new FuncCallExp((FunctionType)visit(program.getFunctionType(name)), ty.getLabel(), ty, 
								name, bits, input, false);
					Variable var = new Variable(ty, ty.getLabel(), newTempVar());
					return new Pair<List<Variable>, SCVMCode>(one(var),
							Seq.seq(code, new Assign(var.lab, var, exp)));
				}
			}
			throw new RuntimeException("Shouldn't reach here!");
		} else if(obj instanceof ASTRecExpression) {
			ASTRecExpression recObj = (ASTRecExpression)obj;
			Pair<List<Variable>, SCVMCode> base = visit(recObj.base);
			String name = recObj.field;
			tc.setContext(program, this.function);
			ASTType baseType = tc.visit(recObj.base).get(0);
			if(baseType == null)
				return null;
			for(ASTFunction func : program.functionDef) {
				if(func.name.equals(name) && func.baseType.instance(baseType)) {
					tc.resolver.resolvingSet = new HashSet<String>();
					tc.resolver.typeVars = new HashMap<String, ASTType>();
					if(this.function.baseType != null) {
						ASTRecType rt = (ASTRecType)this.function.baseType;
						if(rt.typeVariables != null) {
							for(int i = 0; i<rt.typeVariables.size(); ++i) {
								ASTVariableType vt = (ASTVariableType)rt.typeVariables.get(i);
								tc.resolver.typeVars.put(vt.var, vt);
							}
						}
					}
					if(baseType instanceof ASTRecType) {
						ASTRecType rt = (ASTRecType)func.baseType;
						if(rt.typeVariables != null) {
							for(int i = 0; i<rt.typeVariables.size(); ++i) {
								ASTVariableType vt = (ASTVariableType)rt.typeVariables.get(i);
								tc.resolver.typeVars.put(vt.var, ((ASTRecType)baseType).typeVariables.get(i));
							}
						}
					}
					Type ty = visit(tc.resolver.visit(func.returnType));
					if(func.isDummy) {
						if(this.currentCond == null) {
							Variable var = new Variable(new IntType(1, Label.Secure), Label.Secure, newTempVar());
							code = Seq.seq(code, new Assign(var.lab, var, new ConstExp(1, new Constant(1))));
							input.add(var);
						} else {
							input.add(currentCond);
						}
					}

					List<VariableConstant> bits = new ArrayList<VariableConstant>();
					for(int i=0; i<funcExpression.bitParameters.size(); ++i)
						bits.add(this.constructConstant(funcExpression.bitParameters.get(i)));

					FuncCallExp exp;
					if(func instanceof ASTFunctionNative)
						exp = new FuncCallExp((FunctionType)visit(program.getFunctionType(name)), 
								ty.getLabel(), ty, base.left.get(0), ((ASTFunctionNative)func).nativeName, bits, input, true);
					else
						exp = new FuncCallExp((FunctionType)visit(program.getFunctionType(name)), 
								ty.getLabel(), ty, base.left.get(0), name, bits, input, false);
					Variable var = new Variable(ty, ty.getLabel(), newTempVar());
					return new Pair<List<Variable>, SCVMCode>(one(var),
							Seq.seq(Seq.seq(code, base.right),
									new Assign(var.lab, var, exp)));
				}
			}
			throw new RuntimeException("Shouldn't reach here!");
		} else {
			throw new RuntimeException("Shouldn't reach here!");
		}
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTNewObjectExpression exp) {
		RecordType type = (RecordType)visit(exp.type);
		Map<String, Variable> init = new HashMap<String, Variable>();
		SCVMCode code = new Skip();
		for(Map.Entry<String, ASTExpression> ent : exp.valueMapping.entrySet()) {
			Pair<List<Variable>, SCVMCode> tmp = visit(ent.getValue());
			init.put(ent.getKey(), tmp.left.get(0));
			code = Seq.seq(code, tmp.right);
		}
		NewObjExp nexp = new NewObjExp(type, type.lab, init);
		Variable var = new Variable(type, type.getLabel(), newTempVar());
		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(code, new Assign(var.lab, var, nexp)));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTOrPredicate orPredicate) {
		Pair<List<Variable>, SCVMCode> left = visit(orPredicate.left);
		Pair<List<Variable>, SCVMCode> right = visit(orPredicate.right);
		Variable lv = left.left.get(0);
		Variable rv = right.left.get(0);
		Variable var = new Variable(lv.type, lv.lab.meet(rv.lab), newTempVar());

		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(Seq.seq(left.right, right.right),
						new Assign(var.lab, var, new BopExp(lv, BopExp.Op.Or, rv))
						));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTRecExpression rec) {
		Pair<List<Variable>, SCVMCode> code = visit(rec.base);
		RecordType ty = (RecordType)code.left.get(0).type;
		RecExp exp = new RecExp(ty.lab, code.left.get(0), rec.field);
		Variable var = new Variable(ty.fields.get(rec.field), 
				ty.fields.get(rec.field).getLabel(), 
				newTempVar());

		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(code.right, new Assign(var.lab, var, exp)));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTRecTupleExpression tuple) {
		Pair<List<Variable>, SCVMCode> base = visit(tuple.base);
		Variable bv = base.left.get(0);
		Variable[] var = new Variable[((RecordType)bv.type).fields.size()];
		String[] fields = new String[((RecordType)bv.type).fields.size()];
		int j = 0;
		for(Map.Entry<String, Type> i : ((RecordType)bv.type).fields.entrySet()) {
			var[j] = new Variable(i.getValue(), i.getValue().getLabel(), newTempVar());
			fields[j] = i.getKey();
			j++;
		}
		SCVMCode code = Seq.seq(base.right, new ReverseRecordAssign(bv, var, fields));
		List<Variable> ret = new ArrayList<Variable>();
		Map<String, Variable> old = new HashMap<String, Variable>(this.variableValues);
		for(int i=0; i<fields.length; ++i)
			this.variableValues.put(fields[i], var[i]);
		for(int i=0; i<tuple.exps.size(); ++i) {
			Pair<List<Variable>, SCVMCode> tmp = visit(tuple.exps.get(i));
			ret.add(tmp.left.get(0));
			code = Seq.seq(code, tmp.right);
		}
		this.variableValues = old;
		return new Pair<List<Variable>, SCVMCode>(ret, code);
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTTupleExpression tuple) {
		SCVMCode code = new Skip();
		List<Variable> res = new ArrayList<Variable>();
		for(int i=0; i<tuple.exps.size(); ++i) {
			Pair<List<Variable>, SCVMCode> tmp = visit(tuple.exps.get(i));
			res.add(tmp.left.get(0));
			code = Seq.seq(code, tmp.right);
		}
		return new Pair<List<Variable>, SCVMCode>(res, code);
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTVariableExpression variableExpression) {
		return new Pair<List<Variable>, SCVMCode>(
				one(this.variableValues.get(variableExpression.var)), new Skip());
	}

	@Override
	public Type visit(ASTFloatType type) {
		return new FloatType(constructConstant(type.getBits()), Label.get(type.getLabel()));
	}

	@Override
	public Type visit(ASTRndType type) {
		return new RndType(constructConstant(type.getBits()), Label.get(type.getLabel()));
	}

	@Override
	public Type visit(ASTFunctionType type) {
		Type ret = visit(type.returnType);
		List<Type> inputs = new ArrayList<Type>();
		for(int i=0; i<type.inputTypes.size(); ++i)
			inputs.add(visit(type.inputTypes.get(i)));
		List<Type> tp = new ArrayList<Type>();
		if(type.typeParameter != null) {
			for(int i=0; i<type.typeParameter.size(); ++i)
				tp.add(visit(type.typeParameter.get(i)));
		}

		return new FunctionType(ret, type.name, inputs, type.bitParameter, tp, type.global);
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(
			ASTFloatConstantExpression exp) {
		VariableConstant bits = this.constructConstant(exp.bitSize);
		Variable var = new Variable(new FloatType(bits, Label.Pub), Label.Pub, newTempVar());
		return new Pair<List<Variable>, SCVMCode>(one(var),
				new Assign(Label.Pub, var, new ConstExp(exp.value, bits)));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTLogExpression tuple) {
		Pair<List<Variable>, SCVMCode> expCode = visit(tuple.exp);
		Variable var = new Variable(new IntType(32, Label.Pub), Label.Pub, newTempVar());
		return new Pair<List<Variable>, SCVMCode>(one(var),
				Seq.seq(expCode.right,
						new Assign(Label.Pub, var, new LogExp(Label.Pub, expCode.left.get(0)))));
	}

	@Override
	public Pair<List<Variable>, SCVMCode> visit(ASTRangeExpression tuple) {
		Pair<List<Variable>, SCVMCode> expCode = visit(tuple.source);
		VariableConstant ll = this.constructConstant(tuple.rangel);
		SCVMCode code = expCode.right;
		Label lab = expCode.left.get(0).lab;
		if(tuple.ranger == null) {
			Variable var = new Variable(
					new IntType(1, lab), 
					lab, 
					newTempVar());
			return new Pair<List<Variable>, SCVMCode>(one(var),
					Seq.seq(code,
							new Assign(lab, var, 
									new RangeExp(lab, expCode.left.get(0), ll, null)))); 
		} else {
			VariableConstant rr = this.constructConstant(tuple.ranger);
			Variable var = new Variable(
					new IntType(new BOPVariableConstant(rr, Op.Sub, ll), lab), 
					lab, 
					newTempVar());
			return new Pair<List<Variable>, SCVMCode>(one(var),
					Seq.seq(code,
							new Assign(lab, var, 
									new RangeExp(lab, expCode.left.get(0), ll, rr)))); 
		}
	}

}
