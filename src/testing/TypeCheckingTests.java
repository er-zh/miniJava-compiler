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
		
		if(err.hasErrors()) {
			fail("Identification step failed, no type checking done");
		}
		
		tc.check(ast);
	}
	
	void pass() {
		assertTrue(!err.hasErrors());
	}
	
	void error(int ln) {
		assertTrue(err.hasErrors());
		
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
	void testPassMethodReturns1() {
		// test to see that the typechecker can tell that a method returns a value as promised
		setupTest("../tests/pa3_selfmade/types/pass4.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns2() {
		// if with no else and a return at the end
		
		setupTest("../tests/pa3_selfmade/types/pass3.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns3() {
		// test whether or not the type checker can tell if there is a return statement
		// in each branch of the if statement
		setupTest("../tests/pa3_selfmade/types/pass2.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns4() {
		// test returns inside of while loops and in void functions
		setupTest("../tests/pa3_selfmade/types/pass5.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns5() {
		// void function with no body
		setupTest("../tests/pa3_selfmade/types/pass6.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns6() {
		// if statement that doesn't always return followed by a return
		setupTest("../tests/pa3_selfmade/types/pass7.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns7() {
		// returns immediately
		// also checks that array return types are handled correctly
		setupTest("../tests/pa3_selfmade/types/pass8.java");
		pass();
	}
	
	@Test
	void testPassMethodReturns8() {
		// checks nested if statments where
		// each if has a corresponding else
		setupTest("../tests/pa3_selfmade/types/pass9.java");
		pass();
	}
	
	@Test
	@Disabled
	void testPassMethodReturns9() {
		// checks nested if statements that are unbalanced
		// not always an else for each if
		
		// tricky, requires correct parsing of if-elses
		setupTest("../tests/pa3_selfmade/types/pass11.java");
		pass();
	}
	
	@Test
	void testFailMethodReturns1() {
		// test returns inside of while loops and in void functions
		setupTest("../tests/pa3_selfmade/types/fail1.java");
		error(2);
	}
	
	@Test
	void testFailMethodReturns2() {
		// has a return inside an if then branch
		setupTest("../tests/pa3_selfmade/types/fail2.java");
		error(2);
	}
	
	@Test
	void testFailMethodReturns3() {
		// has a return but only in an else branch
		setupTest("../tests/pa3_selfmade/types/fail3.java");
		error(2);
	}
	
	@Test
	void testFailMethodReturns4() {
		// no return statement
		setupTest("../tests/pa3_selfmade/types/fail4.java");
		error(2);
	}
	
	@Test
	void testFailMethodReturns5() {
		// unbalanced ifs that don't have a closing return
		setupTest("../tests/pa3_selfmade/types/fail5.java");
		error(2);
	}
	
	@Test
	void testPassNewArrayExprs() {
		setupTest("../tests/pa3_selfmade/types/pass12.java");
		pass();
	}
	
	@Test
	void testPassBinExprs() {
		setupTest("../tests/pa3_selfmade/types/pass13.java");
		pass();
	}
	
	@Test
	void testPassClasses() {
		setupTest("../tests/pa3_selfmade/types/pass14.java");
		pass();
	}
	
	@Test
	void testPassForLoops() {
		setupTest("../tests/pa5_test/pass002.java");
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
