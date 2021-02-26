package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class ParserTests {

	@Test
	void testClassDeclParsing() {
		String input = "class test { }";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		
		assertTrue(p.parse() != null);
		
		input = "class test { } class test2 {\n}class t3{} class t4{}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
		
		input = "class fails {";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class {}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class fails {}class here } {";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
	}
	
	@Test
	void testFieldDecs() {
		String input = "class test { private static type typed;\n"
				+ "public type pubfield;"
				+ "private int status;"
				+ "static int[] array;"
				+ "type[] tarr;"
				+ "public boolean TF;}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		
		assertTrue(p.parse() != null);
		
		// grammar enforces visibility before acces
		input = "class fails {static private sometype var;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		// grammar doesn't allow bool arrays
		input = "class fails {boolean[] bb;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		// no semicolon
		input = "class fails {private var}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		// cannot declare a void typed value
		input = "class fails {public static void no_no;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		// declaring a variable without a name
		input = "class fails {int[] ;}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
	}

	@Test
	@Disabled
	void testMethodDecs() {
		String input = "class classic {\n"
				+ "public static void main(String[] args){}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
		
		input = "class methods {"
				+ "int m1(){}\n"
				+ "typed m2(){}"
				+ "static typed[] m3(){}"
				+ "private void m4(Vector[] vs, Mat m, int size){}\n}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
		
		input = "class fails {method(){}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class fails {void method{}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class fails {void method(static variable){}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class fails {void method();}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testRefs() {
		String input = "class classic {\n"
				+ "public static void main(String[] args){\n"
				+ "System.out.println(message);\n"
				+ "this.that.which.there.then(v1, v2);}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
		
		input = "class classic {\n"
				+ "public static void badrefs(){\n"
				+ "this.this.name = 12;}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class classic {\n"
				+ "public static void badrefs(){\n"
				+ "name.this.name = 12;}}";
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
	}
	
	@Test
	void testDegenCase() {
		String input = "";
		
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testNestedIfs() {
		String input = "class ifNest {"
				+ "    public static void main(String[] args) {"
				+ "        System.out.println(test());"
				+ "    }\n"
				+ "    public boolean test() {"
				+ "        int a = 10;\r"
				+ "        if(a > 0) if(a > 5) if(a > 10) return true;"
				+ "               else return false; else return true;"
				+ "}}";
		
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testDegenOps() {
		String input = "class grossOps {\n"
				+ "public static void main(String[] args){"
				+ "int neg = ---3;\n"
				+ "int minus = 4 + -3;\n"
				+ "int confuse = 4 - - 7;\n"
				+ "}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	//-------------------------------------------------------------------------------//
	// following tests parsers ability to correctly parse expressions and statements
	
	@Test
	@Disabled
	void basicProgram() {
		String input = "class basicStatements {\n"
				+ "void main(){\n"
				+ "ComplexNum i = new ComplexNum();\n"
				+ "obj.status = this.rng(v1, v2, this.const);"
				+ "}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void moreControlFlowStatements() {
		String input = "class basicStatements {\n"
				+ "public static void main(String[] args){\n"
				+ "while(loop == true){"
				+ "c = a + b - d * f;"
				+ "if(!done || finished) {"
				+ "loop = !noloop && done; }"
				+ "else loop = this.tf(val);"
				+ "} return;\n"
				+ "return a - b;}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testTypeAssignStatements() {
		String input = "class statements {\n"
				+ "public static void main(String[] args){\n"
				+ "boolean set = false;"
				+ "int[] arr = new int[5];\n"
				+ "Vector v = w.transpose();\n"
				+ "Vector[] mat = w.mm(v + v);"
				+ "}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testRefdStatements() {
		String input = "class statements {\n"
				+ "public static void main(String[] args){\n"
				+ "this = that;\n"
				+ "arr = obj.normal.dist;"
				+ "method();"
				+ "method(overload);"
				+ "arr[0] = 10;"
				+ "}}";
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testMultiClassProgram() {
		String input = "class NumberDemo {\r\n"
				+ "	public static void main(String [] a) {\r\n"
				+ "		System.out.println(Math.choose(10, 4));\r\n"
				+ "		System.out.println(Math.gcd(84, 132));\r\n"
				+ "	}\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "// The following code contains these legitimage syntax usage:\r\n"
				+ "// - Variable declarations\r\n"
				+ "// - Unary and complicated binary arithmetic operations\r\n"
				+ "// - While loop\r\n"
				+ "class Math {\r\n"
				+ "	public int choose(int n, int k) {\r\n"
				+ "		int res = 0;\r\n"
				+ "     int i = 0;\r\n"
				+ "		if (!(n<k)) {\r\n"
				+ "			// n! / (k! (n-k)!)\r\n"
				+ "			i = 1;\r\n"
				+ "			res = -1; // just to test unary +\r\n"
				+ "			while (i <= k) {\r\n"
				+ "				res = res * (n-k+i) / i;\r\n"
				+ "			}\r\n"
				+ "		} else {\r\n"
				+ "			res = 0;\r\n"
				+ "		}\r\n"
				+ "		return res;\r\n"
				+ "	}\r\n"
				+ "	public int gcd(int a, int b) {\r\n"
				+ "		int tmp = 0;\r\n"
				+ "     int res = 0;\r\n"
				+ "		if (a < b) {\r\n"
				+ "			tmp = a;\r\n"
				+ "			a = b;\r\n"
				+ "			b = tmp;\r\n"
				+ "		} else {\r\n"
				+ "		}\r\n"
				+ "		if (b == 0)\r\n"
				+ "			res = a;\r\n"
				+ "		else if ((a / b) * b != a) {\r\n"
				+ "			res = this.gcd(b, a-b*(a/b));\r\n"
				+ "		} else {\r\n"
				+ "			res = b;\r\n"
				+ "		}\r\n"
				+ "		return res;\r\n"
				+ "	}\r\n"
				+ "}";
		
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertTrue(p.parse() != null);
	}
	
	@Test
	@Disabled
	void testFailurePrograms() {
		String input = "class Failure {\r\n"
				+ "	public static void main(String [] a) {\n"
				+ "			//declaring variables inside functions is not allowed.\r\n"
				+ "			Numbers n;\n"
				+ "			n = new Numbers();\n"
				+ "			n.choose(10, 4);\n"
				+ "			System.out.println(\"10 choose 4 is \");\n"
				+ "			System.out.println(new Numbers().choose(10, 4));\n"
				+ "			System.out.println(\"\\nGCD of 84, 132 is \")\n;"
				+ "	}}";
		
		Parser p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
		
		input = "class Failure {\r\n"
				+ "public static void main(String [] a) {"
				+ "System.out.println(int);}}";
		
		p = new Parser(new Scanner(str2Stream(input)));
		assertFalse(p.parse() != null);
	}
	
	@Test
	void testFileInputStream() {
		String[] args = new String[2];
		args[0] = "../tests/pa1_selfmade/valid_nums.java";
		args[1] = "../tests/pa1_selfmade/valid_qs.java";
		
		for(String fname : args) {
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
	
	private InputStream str2Stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
