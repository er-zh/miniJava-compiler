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
		String file = "../tests/pa2_selfmade/simple.java";
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
		
		InputStream inputStream = str2filestream(file);
		Parser p = new Parser(new Scanner(inputStream));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testEmptyFile() {
		String file = "../tests/pa2_selfmade/pass_empty.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testClassDec1() {
		String file = "../tests/pa2_selfmade/pass_class_dec1.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [2]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"Object\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [0]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"Computer\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testFieldDec1() {
		String file = "../tests/pa2_selfmade/pass_field_decs.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"fields\" classname\n"
				+ "  .   FieldDeclList [6]\n"
				+ "  .   . (public static) FieldDecl\n"
				+ "  .   .   INT BaseType\n"
				+ "  .   .   \"f1\" fieldname\n"
				+ "  .   . (public) FieldDecl\n"
				+ "  .   .   ArrayType\n"
				+ "  .   .     INT BaseType\n"
				+ "  .   .   \"f2\" fieldname\n"
				+ "  .   . (private static) FieldDecl\n"
				+ "  .   .   BOOLEAN BaseType\n"
				+ "  .   .   \"f3\" fieldname\n"
				+ "  .   . (private) FieldDecl\n"
				+ "  .   .   ClassType\n"
				+ "  .   .     \"Object\" Identifier\n"
				+ "  .   .   \"f4\" fieldname\n"
				+ "  .   . (public static) FieldDecl\n"
				+ "  .   .   ArrayType\n"
				+ "  .   .     ClassType\n"
				+ "  .   .       \"Pepperoni\" Identifier\n"
				+ "  .   .   \"f5\" fieldname\n"
				+ "  .   . (public) FieldDecl\n"
				+ "  .   .   ArrayType\n"
				+ "  .   .     ClassType\n"
				+ "  .   .       \"Interface\" Identifier\n"
				+ "  .   .   \"f6\" fieldname\n"
				+ "  .   MethodDeclList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testMethodDec1() {
		String file = "../tests/pa2_selfmade/pass_method_decs1.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"methods\" classname\n"
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
				+ "  .   .   StmtList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testMethodDec2() {
		String file = "../tests/pa2_selfmade/pass_method_decs2.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"methods\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [4]\n"
				+ "  .   . (private) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"hidden\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"visible\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"notPrivate\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (public static) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"notMember\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testMethodDec3() {
		String file = "../tests/pa2_selfmade/pass_method_decs3.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"methods\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [4]\n"
				+ "  .   . (private static) MethodDecl\n"
				+ "  .   .   INT BaseType\n"
				+ "  .   .   \"hidden\" methodname\n"
				+ "  .   .   ParameterDeclList [2]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   INT BaseType\n"
				+ "  .   .   .   \"a\"parametername \n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   INT BaseType\n"
				+ "  .   .   .   \"b\"parametername \n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   ClassType\n"
				+ "  .   .     \"String\" Identifier\n"
				+ "  .   .   \"strcat\" methodname\n"
				+ "  .   .   ParameterDeclList [2]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ClassType\n"
				+ "  .   .   .     \"String\" Identifier\n"
				+ "  .   .   .   \"one\"parametername \n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ClassType\n"
				+ "  .   .   .     \"String\" Identifier\n"
				+ "  .   .   .   \"two\"parametername \n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   BOOLEAN BaseType\n"
				+ "  .   .   \"not\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   BOOLEAN BaseType\n"
				+ "  .   .   .   \"b\"parametername \n"
				+ "  .   .   StmtList [0]\n"
				+ "  .   . (private) MethodDecl\n"
				+ "  .   .   ClassType\n"
				+ "  .   .     \"Object\" Identifier\n"
				+ "  .   .   \"something\" methodname\n"
				+ "  .   .   ParameterDeclList [1]\n"
				+ "  .   .   . ParameterDecl\n"
				+ "  .   .   .   ArrayType\n"
				+ "  .   .   .     ClassType\n"
				+ "  .   .   .       \"Object\" Identifier\n"
				+ "  .   .   .   \"array\"parametername \n"
				+ "  .   .   StmtList [0]\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	//test parsing different kinds of statements
	@Test
	void testWhile() {
		String file = "../tests/pa2_selfmade/pass_while.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testReturn1() {
		String file = "../tests/pa2_selfmade/pass_return.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testReturn2() {
		String file = "../tests/pa2_selfmade/pass_return_nothing.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf1() {
		String file = "../tests/pa2_selfmade/pass_if1.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf2() {
		String file = "../tests/pa2_selfmade/pass_if2.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testIf3() {
		String file = "../tests/pa2_selfmade/pass_if_noelse.java";
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
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testCalls() {
		String file = "../tests/pa2_selfmade/pass_callStmt.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [3]\n"
				// getNumber();
				+ "  .   .   . CallStmt\n"
				+ "  .   .   .   QualRef\n"
				+ "  .   .   .     \"getNumber\" Identifier\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"math\" Identifier\n"
				+ "  .   .   .   ExprList [0]\n"
				// randomInt(0, 100);
				+ "  .   .   . CallStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"randomInt\" Identifier\n"
				+ "  .   .   .   ExprList [2]\n"
				+ "  .   .   .   . LiteralExpr\n"
				+ "  .   .   .   .   \"0\" IntLiteral\n"
				+ "  .   .   .   . LiteralExpr\n"
				+ "  .   .   .   .   \"100\" IntLiteral\n"
				// randomInt(gaussian, 0, variance[2]);
				+ "  .   .   . CallStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"randomInt\" Identifier\n"
				+ "  .   .   .   ExprList [3]\n"
				+ "  .   .   .   . RefExpr\n"
				+ "  .   .   .   .   IdRef\n"
				+ "  .   .   .   .     \"gaussian\" Identifier\n"
				+ "  .   .   .   . LiteralExpr\n"
				+ "  .   .   .   .   \"0\" IntLiteral\n"
				+ "  .   .   .   . IxExpr\n"
				+ "  .   .   .   .   IdRef\n"
				+ "  .   .   .   .     \"variance\" Identifier\n"
				+ "  .   .   .   .   LiteralExpr\n"
				+ "  .   .   .   .     \"2\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	// test parsing expressions
	@Test
	void testExprOr() {
		String file = "../tests/pa2_selfmade/pass_or.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"||\" Operator\n"
				+ "  .   .   .       RefExpr\n"
				+ "  .   .   .         IdRef\n"
				+ "  .   .   .           \"a\" Identifier\n"
				+ "  .   .   .       RefExpr\n"
				+ "  .   .   .         IdRef\n"
				+ "  .   .   .           \"b\" Identifier\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testExprAnd() {
		String file = "../tests/pa2_selfmade/pass_and.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [1]\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"&&\" Operator\n"
				+ "  .   .   .       RefExpr\n"
				+ "  .   .   .         IdRef\n"
				+ "  .   .   .           \"a\" Identifier\n"
				+ "  .   .   .       RefExpr\n"
				+ "  .   .   .         IdRef\n"
				+ "  .   .   .           \"b\" Identifier\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testExprLAssoc() { // also tests plus and binary minus
		String file = "../tests/pa2_selfmade/pass_left_assoc_expr.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [2]\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"-\" Operator\n"
				+ "  .   .   .       BinaryExpr\n"
				+ "  .   .   .         \"+\" Operator\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"2\" IntLiteral\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"3\" IntLiteral\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"4\" IntLiteral\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"-\" Operator\n"
				+ "  .   .   .       BinaryExpr\n"
				+ "  .   .   .         \"-\" Operator\n"
				+ "  .   .   .           BinaryExpr\n"
				+ "  .   .   .             \"+\" Operator\n"
				+ "  .   .   .               RefExpr\n"
				+ "  .   .   .                 IdRef\n"
				+ "  .   .   .                   \"ans\" Identifier\n"
				+ "  .   .   .               LiteralExpr\n"
				+ "  .   .   .                 \"2\" IntLiteral\n"
				+ "  .   .   .           RefExpr\n"
				+ "  .   .   .             IdRef\n"
				+ "  .   .   .               \"ans\" Identifier\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"7\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testExprLAssoc2() { // also tests times and divides
		String file = "../tests/pa2_selfmade/pass_left_assoc_expr2.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [2]\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"/\" Operator\n"
				+ "  .   .   .       BinaryExpr\n"
				+ "  .   .   .         \"*\" Operator\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"2\" IntLiteral\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"3\" IntLiteral\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"4\" IntLiteral\n"
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"/\" Operator\n"
				+ "  .   .   .       BinaryExpr\n"
				+ "  .   .   .         \"*\" Operator\n"
				+ "  .   .   .           BinaryExpr\n"
				+ "  .   .   .             \"*\" Operator\n"
				+ "  .   .   .               RefExpr\n"
				+ "  .   .   .                 IdRef\n"
				+ "  .   .   .                   \"ans\" Identifier\n"
				+ "  .   .   .               LiteralExpr\n"
				+ "  .   .   .                 \"2\" IntLiteral\n"
				+ "  .   .   .           RefExpr\n"
				+ "  .   .   .             IdRef\n"
				+ "  .   .   .               \"e\" Identifier\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"7\" IntLiteral\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testExprUnary() {
		String file = "../tests/pa2_selfmade/pass_unary.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [4]\n"
				// ans[0] = -2;
				+ "  .   .   . IxAssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"0\" IntLiteral\n"
				+ "  .   .   .   UnaryExpr\n"
				+ "  .   .   .     \"-\" Operator\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"2\" IntLiteral\n"
				// ans[-2] = !true;
				+ "  .   .   . IxAssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   UnaryExpr\n"
				+ "  .   .   .     \"-\" Operator\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"2\" IntLiteral\n"
				+ "  .   .   .   UnaryExpr\n"
				+ "  .   .   .     \"!\" Operator\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"true\" BooleanLiteral\n"
				// ans = 4 - -2;
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"-\" Operator\n"
				+ "  .   .   .       LiteralExpr\n"
				+ "  .   .   .         \"4\" IntLiteral\n"
				+ "  .   .   .       UnaryExpr\n"
				+ "  .   .   .         \"-\" Operator\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"2\" IntLiteral\n"
				// ans = -4 + ---3 *- ref;
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   IdRef\n"
				+ "  .   .   .     \"ans\" Identifier\n"
				+ "  .   .   .   BinaryExpr\n"
				+ "  .   .   .     \"+\" Operator\n"
				+ "  .   .   .       UnaryExpr\n"
				+ "  .   .   .         \"-\" Operator\n"
				+ "  .   .   .           LiteralExpr\n"
				+ "  .   .   .             \"4\" IntLiteral\n"
				+ "  .   .   .       BinaryExpr\n"
				+ "  .   .   .         \"*\" Operator\n"
				+ "  .   .   .           UnaryExpr\n"
				+ "  .   .   .             \"-\" Operator\n"
				+ "  .   .   .               UnaryExpr\n"
				+ "  .   .   .                 \"-\" Operator\n"
				+ "  .   .   .                   UnaryExpr\n"
				+ "  .   .   .                     \"-\" Operator\n"
				+ "  .   .   .                       LiteralExpr\n"
				+ "  .   .   .                         \"3\" IntLiteral\n"
				+ "  .   .   .           UnaryExpr\n"
				+ "  .   .   .             \"-\" Operator\n"
				+ "  .   .   .               RefExpr\n"
				+ "  .   .   .                 IdRef\n"
				+ "  .   .   .                   \"ref\" Identifier\n"
				+ "=============================================\n";
		
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}

	@Test
	void testExprCall() {
		String file = "../tests/pa2_selfmade/pass_expr_calls.java";
		String expected = "======= AST Display =========================\n"
				+ "Package\n"
				+ "  ClassDeclList [1]\n"
				+ "  . ClassDecl\n"
				+ "  .   \"e\" classname\n"
				+ "  .   FieldDeclList [0]\n"
				+ "  .   MethodDeclList [1]\n"
				+ "  .   . (public) MethodDecl\n"
				+ "  .   .   VOID BaseType\n"
				+ "  .   .   \"main\" methodname\n"
				+ "  .   .   ParameterDeclList [0]\n"
				+ "  .   .   StmtList [3]\n"
				// this.that[4] = getNumber();
				+ "  .   .   . IxAssignStmt\n"
				+ "  .   .   .   QualRef\n"
				+ "  .   .   .     \"that\" Identifier\n"
				+ "  .   .   .     ThisRef\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"4\" IntLiteral\n"
				+ "  .   .   .   CallExpr\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"getNumber\" Identifier\n"
				+ "  .   .   .     ExprList + [0]\n"
				// a.b[0] = randomInt(0, 100);
				+ "  .   .   . IxAssignStmt\n"
				+ "  .   .   .   QualRef\n"
				+ "  .   .   .     \"b\" Identifier\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"a\" Identifier\n"
				+ "  .   .   .   LiteralExpr\n"
				+ "  .   .   .     \"0\" IntLiteral\n"
				+ "  .   .   .   CallExpr\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"randomInt\" Identifier\n"
				+ "  .   .   .     ExprList + [2]\n"
				+ "  .   .   .     . LiteralExpr\n"
				+ "  .   .   .     .   \"0\" IntLiteral\n"
				+ "  .   .   .     . LiteralExpr\n"
				+ "  .   .   .     .   \"100\" IntLiteral\n"
				// q.e.d = randomInt(gaussian, 0, 1);
				+ "  .   .   . AssignStmt\n"
				+ "  .   .   .   QualRef\n"
				+ "  .   .   .     \"d\" Identifier\n"
				+ "  .   .   .     QualRef\n"
				+ "  .   .   .       \"e\" Identifier\n"
				+ "  .   .   .       IdRef\n"
				+ "  .   .   .         \"q\" Identifier\n"
				+ "  .   .   .   CallExpr\n"
				+ "  .   .   .     IdRef\n"
				+ "  .   .   .       \"randomInt\" Identifier\n"
				+ "  .   .   .     ExprList + [3]\n"
				+ "  .   .   .     . RefExpr\n"
				+ "  .   .   .     .   IdRef\n"
				+ "  .   .   .     .     \"gaussian\" Identifier\n"
				+ "  .   .   .     . LiteralExpr\n"
				+ "  .   .   .     .   \"0\" IntLiteral\n"
				+ "  .   .   .     . LiteralExpr\n"
				+ "  .   .   .     .   \"1\" IntLiteral\n"
				+ "=============================================\n";
				
				
		Parser p = new Parser(new Scanner(str2filestream(file)));
		
		AST tree = p.parse();
		
		assertTrue(tree != null);
		
		assertTrue(new ASTtoString().stringifyTree(tree).equals(expected));
	}
	
	@Test
	void testCheckpoint() {
		String floc = "../tests/pa2_tests/pass292.java";
		
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(floc);
		}
		catch(FileNotFoundException e) {
			fail("File not found");
		}
		
		Parser p = new Parser(new Scanner(fs));
		
		AST ptree = p.parse();
		
		new ASTDisplay().showTree(ptree);
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
			AST parseTree = p.parse();
			
			assertTrue(parseTree != null);
			
			new ASTDisplay().showTree(parseTree);
			
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
