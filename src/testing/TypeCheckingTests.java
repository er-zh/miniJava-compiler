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
import miniJava.ContextualAnalyzer.TypeChecker;

class TypeCheckingTests {
	private ErrorReporter err;
	private Scanner s;
	private Parser p;
	private AST ast;
	private IdChecker ic;
	private TypeChecker tc;
	
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
		tc = new TypeChecker(err);
		
		assertTrue(ast != null);
		
		ic.check(ast);
		tc.check(ast);
	}
	
	void pass() {
		assertTrue(err.hasErrors() == false);
	}
	
	void error(int ln) {
		assertTrue(err.hasErrors() == true);
		
		assertEquals("*** line "+ln+":", err.getErrorReport().substring(0, 11));
	}
	
	@Test
	void testFailStringUnsupported1() {
		// ref to string must fail
		setupTest("../tests/pa3_selfmade/pass_predefs1.java");
		error(3);
	}
	
	@Test
	void testFailStringUnsupported2() {
		setupTest("../tests/pa3_selfmade/types/fail_unsupported1.java");	
		
		error(3);
	}
	
	@Test
	void testFailStringUnsupported3() {
		setupTest("../tests/pa3_selfmade/types/fail_unsupported2.java");
		
		error(3);
	}
	
	@Test
	void testFailStringUnsupported4() {
		setupTest("../tests/pa3_selfmade/types/fail_unsupported3.java");
		error(4);
	}
	
	@Test
	void testPassVarDecls() {
		setupTest("../tests/pa3_selfmade/types/pass1.java");
		pass();
	}
	
	@Test
	@Disabled
	void testPassNewArrayExprs() {
		setupTest("../tests/pa3_selfmade/types/pass1.java");
		
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
		System.out.println(err);
		pass();
	}

}
