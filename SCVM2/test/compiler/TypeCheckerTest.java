package compiler;

import java.io.FileInputStream;

import parser.CParser;
import type.source.TypeChecker;
import ast.ASTProgram;

public class TypeCheckerTest {
	public static void main(String[] args) throws Exception {
		CParser parser = new CParser(new FileInputStream("input3/leadingZero.lcc"));
		ASTProgram program = parser.TranslationUnit();
		System.out.println(program.toString());
		TypeChecker tc = new TypeChecker();
		System.out.println(tc.check(program));
	}

}
