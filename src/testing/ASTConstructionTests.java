package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class ASTConstructionTests {
	
	@Test
	void testPA2sample() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"PA2sample\" classname\n"
				+ "  .   FieldDeclList [1]\n"
				+ "  .   . (public) FieldDecl\n"
				+ "  .   .   BOOLEAN BaseType\n"
				+ "  .   .   \"c\" fieldname\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . IfStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"true\" BooleanLiteral\n"
				+ "  .   .   .   IxAssignStmt\n"
				+ "  .   .   .     QualRef\n"
				+ "  .   .   .       \"b\" Identifier\n"
				+ "  .   .   .       ThisRef\n"
				+ "  .   .   .     LiteralExpr\n"
				+ "  .   .   .       \"3\" IntLiteral\n"
				+ "  .   .   .     BinaryExpr\n"
				+ "  .   .   .       \"+\" Operator\n"
				+ "  .   .   .         LiteralExpr\n"
				+ "  .   .   .           \"1\" IntLiteral\n"
				+ "  .   .   .         BinaryExpr\n"
				+ "  .   .   .           \"*\" Operator\n"
				+ "  .   .   .             LiteralExpr\n"
				+ "  .   .   .               \"2\" IntLiteral\n"
				+ "  .   .   .             RefExpr\n"
				+ "  .   .   .               IdRef\n"
				+ "  .   .   .                 \"x\" Identifier\n"
				+ "=============================================\n";
		
		InputStream inputStream = str2filestream("../tests/pa2_selfmade/simple.java");
		Parser p = new Parser(new Scanner(inputStream));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testWhile() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"WhileStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . WhileStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"true\" BooleanLiteral\n"
				+ "  .   .   .   AssignStmt\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"x\" Identifier\n"
				+ "  .   .   .     BinaryExpr\n"
				+ "  .   .   .       \"+\" Operator\n"
				+ "  .   .   .         RefExpr\n"
				+ "  .   .   .           ThisRef\n"
				+ "  .   .   .         LiteralExpr\n"
				+ "  .   .   .           \"1\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_while.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testReturn1() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"retStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . ReturnStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"16\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_return.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testReturn2() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"retStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . ReturnStmt\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_return_nothing.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf1() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"ifStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . IfStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"1\" IntLiteral\n"
				+ "  .   .   .   AssignStmt\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"a\" Identifier\n"
				+ "  .   .   .     LiteralExpr\n"
				+ "  .   .   .       \"2\" IntLiteral\n"
				+ "  .   .   .   AssignStmt\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"b\" Identifier\n"
				+ "  .   .   .     LiteralExpr\n"
				+ "  .   .   .       \"3\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_if1.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf2() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"ifStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . IfStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"false\" BooleanLiteral\n"
				+ "  .   .   .   AssignStmt\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"a\" Identifier\n"
				+ "  .   .   .     LiteralExpr\n"
				+ "  .   .   .       \"1\" IntLiteral\n"
				+ "  .   .   .   IfStmt\n"
				+ "  .   .   .     LiteralExpr\n"
				+ "  .   .   .       \"true\" BooleanLiteral\n"
				+ "  .   .   .     AssignStmt\n"
				+ "  .   .   .       IdRef\n"
				+ "  .   .   .         \"a\" Identifier\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"2\" IntLiteral\n"
				+ "  .   .   .     AssignStmt\n"
				+ "  .   .   .       IdRef\n"
				+ "  .   .   .         \"a\" Identifier\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"5\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_if2.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf3() {
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"ifStmt\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"String\" Identifier\n"
				+ "  .   .   .   \"args\"parametername \n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . IfStmt\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"true\" BooleanLiteral\n"
				+ "  .   .   .   BlockStmt\n"
				+ "  .   .   .     StatementList [1]\n"
				+ "  .   .   .     . AssignStmt\n"
				+ "  .   .   .     .   IdRef\n"
				+ "  .   .   .     .     \"variable\" Identifier\n"
				+ "  .   .   .     .   LiteralExpr\n"
				+ "  .   .   .     .     \"7\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream("../tests/pa2_selfmade/pass_if_noelse.java")));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		new ASTDisplay().showTree(tree);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	@Disabled
	void testFileInputStream() {
		String[] args = new String[2];
		args[0] = "../tests/pa1_selfmade/valid_nums.java";
		args[1] = "../tests/pa1_selfmade/valid_qs.java";

		for (String fname : args) {
			int rc = 0;
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(fname);
			} catch (FileNotFoundException e) {
				System.out.println("Input file " + fname + " not found");
				rc = 1;
				System.exit(rc);
			}

			Scanner s = new Scanner(inputStream);
			Parser p = new Parser(s);

			assertTrue(p.parse() != null);
		}
	}
	
	@SuppressWarnings("unused")
	private InputStream str2Stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
	
	private InputStream str2filestream(String s) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(s);
		} catch (FileNotFoundException e) {
			fail();
		}
		
		return inputStream;
	}
	
	@Test
	@Disabled
	void testAST2String() {
		InputStream inputStream = str2filestream("../tests/pa2_selfmade/simple.java");
		
		Parser p = new Parser(new Scanner(inputStream));
		
		AST tree = p.parse();
		
		System.out.println(new ASTtoString().stringifyTree(tree));
	}

}
