package compiler;

import java.util.Map;

import util.Pair;
import ast.ASTFunction;
import ast.ASTFunctionDef;
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
import ast.type.ASTNativeType;
import ast.type.ASTRecType;
import ast.type.ASTRndType;
import ast.type.ASTType;

public class BitInferenceEngine  extends DefaultStatementExpressionVisitor<Void, ASTExpression>{

	ASTProgram program;

	ASTFunctionDef func;

	ASTType current;

	public void process(ASTProgram program) {
		this.program = program;
		for(int i=0; i<program.functionDef.size(); ++i) {
			if(program.functionDef.get(i) instanceof ASTFunctionDef) {
				this.func = (ASTFunctionDef)program.functionDef.get(i);
				for(ASTStatement stmt : this.func.body) {
					this.current = func.baseType;
					visit(stmt);
				}
			}
		}
	}

	public void pushDown(ASTExpression exp, ASTExpression bits) {
		if(bits == null)
			return;
		if(exp instanceof ASTConstantExpression) {
			((ASTConstantExpression) exp).bitSize =bits;
		} else if(exp instanceof ASTFloatConstantExpression) {
			((ASTFloatConstantExpression) exp).bitSize =bits;
		} else if (exp instanceof ASTBinaryPredicate) {
			pushDown(((ASTBinaryPredicate) exp).left, bits);
			pushDown(((ASTBinaryPredicate) exp).right, bits);
		} else if (exp instanceof ASTBinaryExpression) {
			pushDown(((ASTBinaryExpression) exp).left, bits);
			pushDown(((ASTBinaryExpression) exp).right, bits);
		} else {
			exp.targetBits = bits;
			return;
		}
	}

	public ASTExpression getCurrentBits() {
		if(current instanceof ASTIntType) {
			return ((ASTIntType)current).getBits();
		} else if(current instanceof ASTFloatType) {
			return ((ASTFloatType)current).getBits();
		} else if(current instanceof ASTRndType) {
			return ((ASTRndType)current).getBits();
		}
		return null;
	}

	@Override
	public Void visit(ASTAssignStatement assignStatement) {
		ASTType old = current;
		if(assignStatement.var instanceof ASTTupleExpression) {
			if(assignStatement.var instanceof ASTRecTupleExpression) {
				ASTRecTupleExpression target = (ASTRecTupleExpression)assignStatement.var;
				if(assignStatement.var instanceof ASTRecTupleExpression) {
					ASTRecTupleExpression source = (ASTRecTupleExpression)assignStatement.var;
					for(int i=0; i<target.exps.size(); ++i) {
						visit(target.base);
						ASTExpression tbits = visit(target.exps.get(i));
						if(tbits == null)
							tbits = new ASTConstantExpression(32);
						this.current = null;
						visit(source.base);
						//						if(visit(source.exps.get(i)) == null)
						ASTExpression rbits = visit(source.exps.get(i));
						if(rbits != null)
							pushDown(source.exps.get(i), rbits);
						else
							pushDown(source.exps.get(i), tbits);
					}
				} else {
					ASTTupleExpression source = (ASTTupleExpression)assignStatement.expr;
					for(int i=0; i<target.exps.size(); ++i) {
						visit(target.base);
						ASTExpression tbits = visit(target.exps.get(i));
						if(tbits == null)
							tbits = new ASTConstantExpression(32);
						this.current = null;
						//						if(visit(source.exps.get(i)) == null)
						ASTExpression rbits = visit(source.exps.get(i));
						if(rbits != null)
							pushDown(source.exps.get(i), rbits);
						else
							pushDown(source.exps.get(i), tbits);
					}
				}
			} else {
				ASTTupleExpression target = (ASTTupleExpression)assignStatement.var;
				if(assignStatement.var instanceof ASTRecTupleExpression) {
					ASTRecTupleExpression source = (ASTRecTupleExpression)assignStatement.var;
					for(int i=0; i<target.exps.size(); ++i) {
						ASTExpression tbits = visit(target.exps.get(i));
						if(tbits == null)
							tbits = new ASTConstantExpression(32);
						this.current = null;
						visit(source.base);
						//						if(visit(source.exps.get(i)) == null)
						ASTExpression rbits = visit(source.exps.get(i));
						if(rbits != null)
							pushDown(source.exps.get(i), rbits);
						else
							pushDown(source.exps.get(i), tbits);
					}
				} else {
					ASTTupleExpression source = (ASTTupleExpression)assignStatement.expr;
					for(int i=0; i<target.exps.size(); ++i) {
						ASTExpression tbits = visit(target.exps.get(i));
						if(tbits == null)
							tbits = new ASTConstantExpression(32);
						this.current = null;
						//						if(visit(source.exps.get(i)) == null)
						ASTExpression rbits = visit(source.exps.get(i));
						if(rbits != null)
							pushDown(source.exps.get(i), rbits);
						else
							pushDown(source.exps.get(i), tbits);
					}
				}
			}
		} else {
			ASTExpression rbits = visit(assignStatement.expr);
			ASTExpression bits = visit(assignStatement.var);
			//			if(rbits != null && bits != null && !rbits.equals(bits))
			//				throw new RuntimeException("Bits does not match!");
			if(bits != null)
				pushDown(assignStatement.expr, bits);
			else if(rbits != null)
				pushDown(assignStatement.expr, rbits);
			else
				pushDown(assignStatement.expr, new ASTConstantExpression(32)); // TODO optimizing it
		}
		current = old;
		return null;
	}

	@Override
	public Void visit(ASTFuncStatement funcStatement) {
		visit(funcStatement.func);
		return null;
	}

	@Override
	public Void visit(ASTIfStatement ifStatement) {
		visit(ifStatement.cond);
		for(ASTStatement stmt : ifStatement.trueBranch)
			visit(stmt);
		for(ASTStatement stmt : ifStatement.falseBranch)
			visit(stmt);
		return null;
	}

	@Override
	public Void visit(ASTReturnStatement returnStatement) {
		if(visit(returnStatement.exp) == null) {
			if(func.returnType instanceof ASTIntType) {
				pushDown(returnStatement.exp, ((ASTIntType)func.returnType).getBits());
			} else if(func.returnType instanceof ASTFloatType) {
				pushDown(returnStatement.exp, ((ASTFloatType)func.returnType).getBits());
			} else if(func.returnType instanceof ASTRndType) {
				pushDown(returnStatement.exp, ((ASTRndType)func.returnType).getBits());
			} 
		}
		return null;
	}

	@Override
	public Void visit(ASTWhileStatement whileStatement) {
		visit(whileStatement.cond);
		for(ASTStatement stmt : whileStatement.body)
			visit(stmt);
		return null;
	}

	@Override
	public ASTExpression visit(ASTAndPredicate andPredicate) {
		ASTType oldCurrent = current;
		ASTExpression left = visit(andPredicate.left);
		current = oldCurrent;
		ASTExpression right = visit(andPredicate.right);
		current = oldCurrent;
		if(left == null && right == null) {
			// set to 1 by default
			pushDown(andPredicate.left, new ASTConstantExpression(1));
			pushDown(andPredicate.right, new ASTConstantExpression(1));
		} else {
			if(left == null)
				pushDown(andPredicate.left, right);
			else if(right == null)
				pushDown(andPredicate.right, left);
			else if (!left.equals(right))
				throw new RuntimeException("Bits doesn't match! "+left+"\t"+right);
		}
		current = ASTIntType.get(1, ASTLabel.Secure);
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTArrayExpression arrayExpression) {
		if(visit(arrayExpression.indexExpr) == null) {
			pushDown(arrayExpression.indexExpr, new ASTConstantExpression(32)); // TODO optimizing it
		}
		visit(arrayExpression.var);
		this.current = ((ASTArrayType)current).type;
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTBinaryExpression binaryExpression) {
		ASTType oldCurrent = current;
		ASTExpression left = visit(binaryExpression.left);
		current = oldCurrent;
		ASTExpression right = visit(binaryExpression.right);
		current = oldCurrent;
		if(left == null && right == null) {
			// set to 32 by default
			//			pushDown(binaryExpression.left, new ASTConstantExpression(32));
			//			pushDown(binaryExpression.right, new ASTConstantExpression(32));
		} else {
			if(left == null) {
				pushDown(binaryExpression.left, right);
				left = right;
			} else if(right == null)
				pushDown(binaryExpression.right, left);
//			else if (!left.equals(right))
//				throw new RuntimeException("Bits doesn't match! "+left+"\t"+right);
		}
		//		this.current = IntType;
		return left;
	}

	@Override
	public ASTExpression visit(ASTBinaryPredicate binaryPredicate) {
		ASTType oldCurrent = current;
		ASTExpression left = visit(binaryPredicate.left);
		current = oldCurrent;
		ASTExpression right = visit(binaryPredicate.right);
		current = oldCurrent;
		if(left == null && right == null) {
			// set to 32 by default
			pushDown(binaryPredicate.left, new ASTConstantExpression(32));
			pushDown(binaryPredicate.right, new ASTConstantExpression(32));
		} else {
			if (left != null && right != null && !left.equals(right))
				throw new RuntimeException("Bits doesn't match! "+left+"\t"+right);
			if(left != null) {
				pushDown(binaryPredicate.right, left);
			}
			if(right != null) {
				pushDown(binaryPredicate.left, right);
				left = right;
			}
		}
		this.current = ASTIntType.get(1, ASTLabel.Secure);
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTConstantExpression constantExpression) {
		this.current = ASTIntType.get(constantExpression.bitSize, ASTLabel.Pub);
		return constantExpression.bitSize;
	}

	@Override
	public ASTExpression visit(ASTFuncExpression funcExpression) {
		visit(funcExpression.obj);
		// TODO currently, the input parameter's bit size is not adjusted correctly.
		ASTFunctionType type = ((ASTFunctionType)current);
		if(type != null) {
			for(int i = 0; i<funcExpression.inputs.size(); ++i) {
				Pair<String, ASTExpression> inputs = funcExpression.inputs.get(i);
				ASTExpression bits = visit(inputs.right);
				if(bits != null)
					pushDown(inputs.right, bits);
				else {
					this.current = type.inputTypes.get(i);
					pushDown(inputs.right, this.getCurrentBits());

				}
			}
			this.current = type.returnType;
		}
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTNewObjectExpression exp) {
		for(Map.Entry<String, ASTExpression> ent : exp.valueMapping.entrySet()) {
			ASTExpression bits = visit(ent.getValue());
			if(bits == null) {
				ASTType ty = exp.type.fieldsType.get(ent.getKey());
				if(ty instanceof ASTIntType) {
					pushDown(ent.getValue(), ((ASTIntType)ty).getBits());
				} else if(ty instanceof ASTFloatType) {
					pushDown(ent.getValue(), ((ASTFloatType)ty).getBits());
				} else if(ty instanceof ASTRndType) {
					pushDown(ent.getValue(), ((ASTRndType)ty).getBits());
				}
			}
		}
		this.current = exp.type;
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTOrPredicate orPredicate) {
		ASTType oldCurrent = current;
		ASTExpression left = visit(orPredicate.left);
		current = oldCurrent;
		ASTExpression right = visit(orPredicate.right);
		current = oldCurrent;
		if(left == null && right == null) {
			// set to 32 by default
			pushDown(orPredicate.left, new ASTConstantExpression(32));
			pushDown(orPredicate.right, new ASTConstantExpression(32));
		} else {
			if(left == null) {
				pushDown(orPredicate.left, right);
				left = right;
			} else if(right == null)
				pushDown(orPredicate.right, left);
			else if (!left.equals(right))
				throw new RuntimeException("Bits doesn't match! "+left+"\t"+right);
		}
		current = ASTIntType.get(1, ASTLabel.Secure);
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTRecExpression rec) {
		visit(rec.base);
		if(current instanceof ASTRecType) {
			ASTRecType rt = (ASTRecType)current;
			if(rt.fieldsType.containsKey(rec.field)) {
				current = ((ASTRecType)current).fieldsType.get(rec.field);
			} else {
				for(ASTFunction func : this.program.functionDef) 
					if(func.name.equals(rec.field)){
						String name = null;
						if(func.baseType != null && func.baseType instanceof ASTRecType)
							name = ((ASTRecType)func.baseType).name;
						else if(func.baseType != null && func.baseType instanceof ASTNativeType)
							name = ((ASTNativeType)func.baseType).name;
						else
							continue;
						if(name.equals(rt.name)) {
							current = func.getType();
							return null;
						}
					}
			}
		} else
			current = null;
		return getCurrentBits();
	}

	@Override
	public ASTExpression visit(ASTRecTupleExpression tuple) {
		throw new RuntimeException("Shouldn't reach here.");
	}

	@Override
	public ASTExpression visit(ASTTupleExpression tuple) {
		throw new RuntimeException("Shouldn't reach here.");
	}

	@Override
	public ASTExpression visit(ASTVariableExpression variableExpression) {
		if(variableExpression.var.equals("this")) {
			this.current = this.func.baseType;
			return getCurrentBits();
		}
		if(current instanceof ASTRecType) {
			ASTRecType ty = (ASTRecType)current;
			if(ty.fieldsType.containsKey(variableExpression.var)) {
				this.current = ty.fieldsType.get(variableExpression.var);
				return getCurrentBits();
			}
			ASTType ty1 = this.program.getBaseType(variableExpression.var);
			if(ty1 instanceof ASTRecType && (ty.name.equals(((ASTRecType)ty1).name))) {
				this.current = this.program.getFunctionType(variableExpression.var);
				return null;
			}
		}
		for(Pair<ASTType, String> inputs : func.inputVariables) {
			if(inputs.right.equals(variableExpression.var)) {
				this.current = inputs.left;
				return getCurrentBits();
			}
		}
		for(Pair<ASTType, String> inputs : func.localVariables) {
			if(inputs.right.equals(variableExpression.var)) {
				this.current = inputs.left;
				return getCurrentBits();
			}
		}
		for(ASTFunction func : program.functionDef) {
			if(func.name.equals(variableExpression.var) && func.baseType == null) {
				this.current = func.getType();
				return null;
			}
		}
		return null;
	}

	@Override
	public ASTExpression visit(ASTFloatConstantExpression constantExpression) {
		this.current = ASTFloatType.get(constantExpression.bitSize, ASTLabel.Pub);
		return constantExpression.bitSize;
	}

	@Override
	public ASTExpression visit(ASTLogExpression tuple) {
		// Always public int32
		return new ASTConstantExpression(32);
	}

	@Override
	public ASTExpression visit(ASTRangeExpression tuple) {
		if(tuple.ranger == null)
			return new ASTConstantExpression(1);
		else
			return new ASTBinaryExpression(tuple.ranger, BOP.SUB, tuple.rangel);
	}

}
