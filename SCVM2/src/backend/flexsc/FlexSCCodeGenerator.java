package backend.flexsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import parser.CParser;
import parser.ParseException;
import type.manage.FunctionType;
import type.manage.IntType;
import type.manage.Method;
import type.manage.RecordType;
import type.manage.Type;
import type.manage.TypeManager;
import type.manage.Unknown;
import type.manage.VariableType;
import type.source.TypeChecker;
import util.Pair;
import ast.ASTProgram;

/**
 * Compile file to target class using source file path.
 * @param source : Absolute file path of source code.
 * @param target : Name of output compiler class.
 * @throws FileNotFoundException
 * @throws ParseException
 */
public class FlexSCCodeGenerator {

	private TypeChecker tc = new TypeChecker();
	private compiler.Compiler trans = new compiler.Compiler();
	private CParser parser;
	public ASTProgram program;

	/**
	 * Compile file to target class using source file path.
	 * @param source : Absolute file path of source code.
	 * @param target : Name of output compiler class.
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public FlexSCCodeGenerator(String source) throws FileNotFoundException, 
	ParseException {
		parser = new CParser(new FileInputStream(source));
		program = parser.TranslationUnit();
	}

	/**
	 * Compile ASTFunction into the TargetAST.
	 * @param function : ASTFunction
	 * @return TargetAST
	 */
	public TypeManager compile(ASTProgram program) {
		boolean tcVisit = tc.check(program);
		if(!tcVisit)
			return null;
		return trans.translate(program);
	}

	CodeGenVisitor codeGen;
	
	/**
	 * 
	 * @param config : Usually you just want to pass "new Config()".
	 * @param count : Set to false for normal compilation to FlexSC using ORAM protocol. 
	 * Set to true to enable non-ORAM protocol for encryption counting purposes.
	 * @param trivial : Set to true to run the trivial ORAM implementation. 
	 * @throws FileNotFoundException
	 */
	public void FlexSCCodeGen(Config config, boolean count, boolean trivial) throws IOException {
		TypeManager tm = compile(program);
		codeGen = new CodeGenVisitor(count, trivial);
		for(Type ty : tm.getTypes()) {
			new TypeEmittable(config, codeGen, (RecordType)ty).emit();
		}
		
		for(String name : tm.functions.keySet()) {
			new FunctionPointerEmittable(config, codeGen, name, tm).emit();
		}
		
		for(Method meth : tm.noClassFunctions) {
			new FunctionPointerImplEmittable(config, codeGen, meth, tm).emit();
		}
	}
}
