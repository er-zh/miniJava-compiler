package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
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
	}
	
	@Test
	void testErrReportParse() {
		String file = "../tests/pa2_tests/fail120.java";
		
		setupTest(file);
		assertTrue(ast == null);
		String rep = err.getErrorReport();
		assertEquals("*** line 4:", rep.substring(0, 11));
		
		file = "../tests/pa2_tests/fail171.java";
		
		setupTest(file);
		assertTrue(ast == null);
		rep = err.getErrorReport();
		assertEquals("*** line 5:", rep.substring(0, 11));
	}
	
	@Test
	void testBaseFieldDecs() {
		// only tests base types: boolean, int, int[]
		String file = "../tests/pa3_selfmade/pass_field_decs.java";
		setupTest(file);
		assertTrue(ast != null);
		
		ic.check(ast);
		
		assertTrue(err.hasErrors() == false);
	}
	
	@Test
	void testFieldDeclFail1() {
		// fails a field dec that has type of undeclared class
		String file = "../tests/pa3_selfmade/fail_fieldw_no_dec.java";
		setupTest(file);
		assertTrue(ast != null);
		
		ic.check(ast);
		
		assertTrue(err.hasErrors() == true);
		assertEquals("*** line 3:", err.getErrorReport().substring(0, 11));
	}
}
