package ast;

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
import ast.expr.ExpressionVisitor;
import ast.stmt.ASTAssignStatement;
import ast.stmt.ASTFuncStatement;
import ast.stmt.ASTIfStatement;
import ast.stmt.ASTReturnStatement;
import ast.stmt.ASTStatement;
import ast.stmt.ASTWhileStatement;
import ast.stmt.StatementVisitor;

/**
 * Visitor pattern class used for traversing the abstract syntax tree created 
 * after parsing. This class defines our "visit" functions which are triggered
 * during the creation of the target abstract syntax tree.
 * @param <T1>
 * @param <T2>
 */
public abstract class DefaultStatementExpressionVisitor<T1, T2> implements StatementVisitor<T1>, ExpressionVisitor<T2> {
	
	public T2 visit(ASTPredicate predicate) {
		if(predicate instanceof ASTBinaryPredicate) {
			return visit((ASTBinaryPredicate) predicate);
		} else if(predicate instanceof ASTAndPredicate) {
			return visit((ASTAndPredicate) predicate);
		} else if(predicate instanceof ASTOrPredicate) {
			return visit((ASTOrPredicate) predicate);
		} else
			throw new RuntimeException("Unknown Predicate!");
	}

	public T2 visit(ASTExpression expression) {
		if(expression instanceof ASTBinaryExpression) {
			return visit((ASTBinaryExpression)expression);
		} else if(expression instanceof ASTConstantExpression) {
			return visit((ASTConstantExpression)expression);
		} else if(expression instanceof ASTFloatConstantExpression) {
			return visit((ASTFloatConstantExpression)expression);
		} else if(expression instanceof ASTArrayExpression) {
			return visit((ASTArrayExpression)expression);
		} else if(expression instanceof ASTRecExpression) {
			return visit((ASTRecExpression)expression);
		} else if(expression instanceof ASTVariableExpression) {
			return visit((ASTVariableExpression)expression);
		} else if(expression instanceof ASTPredicate) {
			return visit((ASTPredicate)expression);
		} else if(expression instanceof ASTFuncExpression) {
			return visit((ASTFuncExpression)expression);
		} else if(expression instanceof ASTNewObjectExpression) {
			return visit((ASTNewObjectExpression)expression);
		} else if(expression instanceof ASTRecTupleExpression) {
			return visit((ASTRecTupleExpression)expression);
		} else if(expression instanceof ASTTupleExpression) {
			return visit((ASTTupleExpression)expression);
		} else if(expression instanceof ASTLogExpression) {
			return visit((ASTLogExpression)expression);
		} else if(expression instanceof ASTRangeExpression) {
			return visit((ASTRangeExpression)expression);
		} else
			throw new RuntimeException("Unknown Expression!");
	}

	@Override
	public T1 visit(ASTStatement statement) {
		if(statement instanceof ASTIfStatement) {
			return visit((ASTIfStatement)statement);
		} else if(statement instanceof ASTAssignStatement) {
			return visit((ASTAssignStatement)statement);
		} else if(statement instanceof ASTWhileStatement) {
			return visit((ASTWhileStatement)statement);
		} else if(statement instanceof ASTReturnStatement) {
			return visit((ASTReturnStatement)statement);
		} else if(statement instanceof ASTFuncStatement) {
			return visit((ASTFuncStatement)statement);
		} else
			throw new RuntimeException("Unknown Statement!");
	}

}
