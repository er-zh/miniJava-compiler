package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import miniJava.ContextualAnalyzer.IdChecker;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class IdentificationTests {
	private Scanner s;
	private Parser p;
	private IdChecker ic;
	
	void setupTest(String fileloc) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileloc);
		} catch (FileNotFoundException e) {
			fail();
		}
	}
	
	
	@Test
	void testBaseFieldDecs() {
		// only tests base types: boolean, int, int[]
		String file = "../tests/pa2_selfmade/simple.java";
		
	}

}
