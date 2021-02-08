package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class ParserTests {

	@Test
	@Disabled
	void testClassDeclParsing() {
		String input = "class test { }";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		
		assertTrue(p.parse());
		
		input = "class test { } class test2 {\n}class t3{} class t4{}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
		
		input = "class fails {";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class {}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class fails {}class here } {";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
	}
	
	@Test
	@Disabled
	void testFieldDecs() {
		String input = "class test { private static type typed;\n"
				+ "public type pubfield;"
				+ "private int status;"
				+ "static int[] array;"
				+ "type[] tarr;"
				+ "public boolean TF;}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		
		assertTrue(p.parse());
		
		// grammar enforces visibility before acces
		input = "class fails {static private sometype var;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		// grammar doesn't allow bool arrays
		input = "class fails {boolean[] bb;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		// no semicolon
		input = "class fails {private var}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		// cannot declare a void typed value
		input = "class fails {public static void no_no;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		// declaring a variable without a name
		input = "class fails {int[] ;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
	}

	@Test
	@Disabled
	void testMethodDecs() {
		String input = "class classic {\n"
				+ "public static void main(String[] args){}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
		
		input = "class methods {"
				+ "int m1(){}\n"
				+ "typed m2(){}"
				+ "static typed[] m3(){}"
				+ "private void m4(Vector[] vs, Mat m, int size){}\n}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
		
		input = "class fails {method(){}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class fails {void method{}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class fails {void method(static variable){}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class fails {void method();}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
	}
	
	@Test
	@Disabled
	void testRefs() {
		String input = "class classic {\n"
				+ "public static void main(String[] args){\n"
				+ "System.out.println(message);\n"
				+ "this.that.which.there.then(v1, v2);}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
		
		input = "class classic {\n"
				+ "public static void badrefs(){\n"
				+ "this.this.name = 12;}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
		
		input = "class classic {\n"
				+ "public static void badrefs(){\n"
				+ "name.this.name = 12;}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse());
	}
	
	//-------------------------------------------------------------------------------//
	// following tests parsers ability to correctly parse expressions and statements
	
	@Test
	void basicProgram() {
		String input = "class basicStatements {\n"
				+ "void main(){\n"
				//+ "//ComplexNum i = new ComplexNum();\n"
				+ "this.status = this.rng(v1, v2, this.const);\n";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
	}
	
	@Test
	@Disabled
	void moreControlFlowProgram() {
		String input = "class basicStatements {\n"
				+ "public static void main(String[] args){\n"
				+ "while(loop == true){"
				+ "c = a + b - d * f;"
				+ "if(!done || finished) {"
				+ "loop = !noloop && done; }"
				+ "else loop = this.tf(val);"
				+ "} return; }";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse());
	}
	
	private InputStream str2Stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
