package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.ContextualAnalyzer.IdChecker;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class IdentificationTests {
	private ErrorReporter err;
	private Scanner s;
	private Parser p;
	private AST ast;
	private IdChecker ic;
	
	void setupTest(String fileloc) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileloc);
		} catch (FileNotFoundException e) {
			fail();
		}
		
		err = new ErrorReporter();
		s = new Scanner(is);
		p = new Parser(s, err);
		
		ast = p.parse();
		
		ic = new IdChecker(err);
		
		assertTrue(ast != null);
		
		ic.check(ast);
	}
	
	void pass() {		
		assertTrue(!err.hasErrors());
	}
	
	void error(int ln) {
		
		assertTrue(err.hasErrors());
		
		assertEquals("*** line "+ln+":", err.getErrorReport().substring(0, 11));
	}
	
	@Test
	@Disabled
	void testErrReportParse() {
		String file = "../tests/pa2_tests/fail120.java";
		
		setupTest(file);
		assertTrue(ast == null);
		String rep = err.getErrorReport();
		assertEquals("*** line 4:", rep.substring(0, 11));
		
		file = "../tests/pa2_tests/fail217.java";
		
		setupTest(file);
		assertTrue(ast == null);
		rep = err.getErrorReport();
		assertEquals("*** line 4:", rep.substring(0, 11));
		
		file = "../tests/pa2_tests/fail171.java";
		
		setupTest(file);
		assertTrue(ast == null);
		rep = err.getErrorReport();
		assertEquals("*** line 5:", rep.substring(0, 11));
	}
	
	// TODO passing cases should also test that ids have been correctly
	// linked to their declarations
	
	@Test
	void testPredefs1() {
		// tests that the String class is present
		setupTest("../tests/pa3_selfmade/pass_predefs1.java");
		pass();
	}
	
	@Test
	void testPredefs2() {
		// tests that the System and _PrintStream classes are present
		setupTest("../tests/pa3_selfmade/pass_predefs2.java");
		pass();
	}
	
	@Test
	void testFieldDeclPass1() {
		// tests base types: boolean, int, int[]
		setupTest("../tests/pa3_selfmade/pass_field_decs.java");
		pass();
	}
	
	@Test
	void testFieldDeclPass2() {
		// tests basic class types
		setupTest("../tests/pa3_selfmade/pass_field_decs_classes1.java");
		pass();
	}
	
	@Test
	void testFieldDeclFail1() {
		// fails a field dec that has type of undeclared class
		setupTest("../tests/pa3_selfmade/fail_fieldw_no_dec.java");
		error(3);
	}
	
	@Test
	void testFieldDeclFail2() {
		// fails a field dec that duplicates an identifier
		setupTest("../tests/pa3_selfmade/fail_field_duplicate.java");
		error(5);
	}
	
	@Test
	void testFieldDeclFail3() {
		// fails a field dec that duplicates an identifier
		setupTest("../tests/pa3_selfmade/fail_field_duplicate2.java");
		error(4);
	}
	
	@Test 
	void testMethodDeclPass1(){
		// tests a simple method declarations using base return types
		// i.e.    int, int[], boolean, and void
		setupTest("../tests/pa3_selfmade/pass_method_decs1.java");
		pass();
	}
	
	@Test
	void testMethodDeclPass2() {
		// tests method decs using other classes as return types
		setupTest("../tests/pa3_selfmade/pass_method_decs2.java");
		pass();
	}
	
	@Test
	void testMethodDeclPass3() {
		// tests method decs with parameters that are base types
		// return type can be a base type or can be a class type
		setupTest("../tests/pa3_selfmade/pass_method_decs3.java");
		pass();
	}
	
	@Test
	void testMethodDeclPass4() {
		// tests method decs with parameters that are class types
		// return type can be a base type or can be a class type
		setupTest("../tests/pa3_selfmade/pass_method_decs4.java");
		pass();
	}
	
	@Test
	void testMethodDeclFail1() {
		// fails a method dec that duplicates an identifier
		setupTest("../tests/pa3_selfmade/fail_method_decs1.java");
		error(5);
	}
	
	@Test
	void testMethodDeclFail2() {
		// fails a method dec that has undeclared return type
		setupTest("../tests/pa3_selfmade/fail_method_decs2.java");
		error(1);
	}
	
	@Test
	void testMethodDeclFail3() {
		// fails a method dec that has undeclared return type
		setupTest("../tests/pa3_selfmade/fail_method_decs3.java");
		error(2);
	}
	
	@Test
	void testMethodDeclFail4() {
		// fails a method dec that has undeclared return type
		setupTest("../tests/pa3_selfmade/fail_method_decs4.java");
		error(2);
	}
	
	@Test
	void testMethodDeclFail5() {
		// fails a method dec that has undeclared return type
		setupTest("../tests/pa3_selfmade/fail_undeclared1.java");
		error(9);
	}
	
	@Test
	void testVarDeclPass1() {
		setupTest("../tests/pa3_selfmade/pass_vars1.java");
		pass();
	}
	
	@Test
	void testVarDeclFail1() {
		// fails a method dec that has duplicate local vars
		setupTest("../tests/pa3_selfmade/fail_vars1.java");
		error(5);
	}
	
	@Test
	void testVarDeclFail2() {
		// fails a method that has undeclared typing for a local variable
		setupTest("../tests/pa3_selfmade/fail_vars2.java");
		error(3);
	}
	
	@Test
	void testVarDeclFail3() {
		// fails a method that shadows a param with a local var
		setupTest("../tests/pa3_selfmade/fail_vars3.java");
		error(3);
		
		//System.out.println(err);
	}
	
	@Test
	void testVarDeclFail4() {
		// fails a method that uses a variable prior to declaration
		setupTest("../tests/pa3_selfmade/fail_vars4.java");
		error(3);
		
		//System.out.println(err);
	}
	
	@Test
	void testPassScopeIndep1() {
		// test independent scoping of fields in different class
		
		setupTest("../tests/pa3_selfmade/pass_indep_scope_fields1.java");
		pass();
	}
	
	@Test
	void testPassScopeIndep2() {
		// test independent scoping of fields in different class
		setupTest("../tests/pa3_selfmade/pass_indep_scope_fields2.java");
		pass();
	}
	
	@Test
	void testPassScopeIndep3() {
		// test independent scoping of fields in classes with one containing the other
		setupTest("../tests/pa3_selfmade/pass_indep_scope_fields3.java");
		pass();
	}
	
	@Test
	void testPassScopeIndep4() {
		// test independent scoping of fields in classes with one containing the other
		setupTest("../tests/pa3_selfmade/pass_indep_scope_fields4.java");
		pass();
	}
	
	@Test
	void testPassScopeIndep5() {
		// test independent scoping of methods
		setupTest("../tests/pa3_selfmade/pass_indep_scope_methods1.java");
		pass();
	}
	
	@Test
	void testPassScopeIndep6() {
		// test independent scoping of methods
		setupTest("../tests/pa3_selfmade/pass_indep_scope_methods2.java");
		pass();
	}
	
	@Test
	void testVarsPass1() {
		// simple test, check that declared variable can be used elsewhere
		setupTest("../tests/pa3_selfmade/pass_vars2.java");
		pass();
	}
	
	@Test
	void testRefsPass1() {
		//testing simple id refs
		setupTest("../tests/pa3_selfmade/pass_refs1.java");
		pass();
	}
	
	@Test
	void testRefsPass2() {
		//testing qualified references
		setupTest("../tests/pa3_selfmade/pass_refs2.java");
		pass();
	}
	
	@Test
	void testRefsPass3() {
		//testing qualified references
		setupTest("../tests/pa3_selfmade/pass_refs3.java");
		pass();
	}
	
	@Test
	void testRefsPass4() {
		//testing qualified references
		setupTest("../tests/pa3_selfmade/pass_refs4.java");
		pass();
	}
	
	@Test
	void testRefsFail1() {
		//testing simple id refs
		setupTest("../tests/pa3_selfmade/fail_refs1.java");
		error(3);
		//System.out.println(err);
	}
	
	@Test
	void testRefsFail2() {
		// attempt to access private member of another class should fail
		setupTest("../tests/pa3_selfmade/fail_refs2.java");
		error(3);
		//System.out.println(err);
	}
	
	@Test
	void testRefsFail3() {
		// using this within a static method should fail
		setupTest("../tests/pa3_selfmade/fail_refs3.java");
		error(3);
		//System.out.println(err);
	}
	
	@Test
	void testRefsFail4() {
		// using this within a static method should fail
		setupTest("../tests/pa3_selfmade/fail_refs4.java");
		error(5);
		//System.out.println(err);
	}
	
	@Test
	void testRefsFail5() {
		// using this within a static method should fail
		setupTest("../tests/pa3_selfmade/fail_refs5.java");
		error(9);
		//System.out.println(err);
	}
	
	@Test
	void testControlStructPass() {
		// test control structures like if and while
		// also test some more complicated expressions
		setupTest("../tests/pa3_selfmade/pass_control.java");
		
		pass();
	}
	
	
	@Test
	void testPassComplicatedProg1() {
		setupTest("../tests/pa1_selfmade/valid_qs.java");
		pass();
	}
	
	@Test
	void testPassCompilcatedProg2() {
		setupTest("../tests/pa1_selfmade/valid_nums.java");
		pass();
	}
	
	@Test
	void testFailComplicatedProg() {
		setupTest("../tests/pa3_selfmade/fail_idk.java");
		error(3);
	}
}

