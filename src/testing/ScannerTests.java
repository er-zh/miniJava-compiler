package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

class ScannerTests {
	static Scanner s;
	static HashMap<String, TokenType> typeIndex;
	
	@BeforeAll
	static void setup() {
		typeIndex = new HashMap<String, TokenType>(100);
		
		typeIndex.put("public", TokenType.PUBLIC);
		typeIndex.put("private", TokenType.PRIVATE);
		typeIndex.put("static", TokenType.STATIC);
		typeIndex.put("this", TokenType.THIS);
		typeIndex.put("boolean", TokenType.BOOLEAN);
		typeIndex.put("true", TokenType.T);
		typeIndex.put("false", TokenType.F);
		typeIndex.put("int", TokenType.INT);
		typeIndex.put("void", TokenType.VOID);
		typeIndex.put("if", TokenType.IF);
		typeIndex.put("else", TokenType.ELSE);
		typeIndex.put("while", TokenType.WHILE);
		typeIndex.put("return", TokenType.RETURN);
			
		typeIndex.put("+", TokenType.BINOP);
		typeIndex.put("*", TokenType.BINOP);
		typeIndex.put("/", TokenType.BINOP);
		typeIndex.put("-", TokenType.UNOP);
		typeIndex.put("!", TokenType.UNOP);
		typeIndex.put("&&", TokenType.BINOP);
		typeIndex.put("||", TokenType.BINOP);
		typeIndex.put("!=", TokenType.BINOP);
		typeIndex.put("==", TokenType.BINOP);
		typeIndex.put("<", TokenType.BINOP);
		typeIndex.put("<=", TokenType.BINOP);
		typeIndex.put(">", TokenType.BINOP);
		typeIndex.put(">=", TokenType.BINOP);
		
		typeIndex.put("{", TokenType.LBRACE);
		typeIndex.put("}", TokenType.RBRACE);
		typeIndex.put("[", TokenType.LSQUARE);
		typeIndex.put("]", TokenType.RSQUARE);
		typeIndex.put("(", TokenType.LPAREN);
		typeIndex.put(")", TokenType.RPAREN);
		typeIndex.put(";", TokenType.SEMICOLON);
		typeIndex.put(",", TokenType.COMMA);
		typeIndex.put("=", TokenType.ASSIGNMENT);
		
		typeIndex.put("//", TokenType.COMMENT);
		typeIndex.put("/*", TokenType.COMMENT);
	}
	
	@Test
	void testCharOnlyNameInputs() {
		String input = "this is a test";
		
		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.THIS);
		assertEquals(t.getLexeme(), "this");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "is");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "a");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "test");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void testMixedNameInputs() {
		String input = "th1s is a_t3st";
		
		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "th1s");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "is");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		assertEquals(t.getLexeme(), "a_t3st");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void testKeywords() {
		String input = "public private pirate static void"
				+ "boolean int true ture false if else"
				+ "while return class this thjis"
				+ "aaaaaaaa aa_aaaa"
				+ "num12342342344523432134234"
				+ "nums2132_underscore____";
		
		s = new Scanner(str2Stream(input));
		
		Token t;
		for(String word : input.split("\\s+")) {
			t = s.getNextToken();
			
			assertEquals(t.getLexeme(), word);
			
			TokenType type = typeIndex.get(t.getLexeme());
			type = (type != null) ? type : TokenType.ID;
			assertEquals(t.getType(), type);
		}
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void testOps() {
		String num1 = "234232498756987";
		String ops = "+ + / + * * + - == = <= /";
		String num2 = "85848483";
		
		s = new Scanner(str2Stream(num1 + ops + num2));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.NUM_LITERAL);
		assertEquals(t.getLexeme(), "234232498756987");
		
		for(String word : ops.split("\\s+")) {
			t = s.getNextToken();
			
			assertEquals(t.getLexeme(), word);
			
			TokenType type = typeIndex.get(t.getLexeme());
			type = (type != null) ? type : TokenType.ID;
			assertEquals(t.getType(), type);
		}
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.NUM_LITERAL);
		assertEquals(t.getLexeme(), "85848483");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	void testOpsHarder() {
		String input = "+ / + - */!+-&&/**/||=== !=<<=>=,>";
		
		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "+");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "/");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "+");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "-");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "*");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "/");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "!");

		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "+");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "-");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "&&");

		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.COMMENT);
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "||");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "==");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ASSIGNMENT);
		assertEquals(t.getLexeme(), "=");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "!=");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "<");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), "<=");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), ">=");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.COMMA);
		assertEquals(t.getLexeme(), ",");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.BINOP);
		assertEquals(t.getLexeme(), ">");
		
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void testPunc() {
		String input = "/*var1 & \nmultiline comment\nvar2*/";

		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.COMMENT);
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void test1LineComment() {
		String input = "//single line comment\ncode = data";
		
		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		assertEquals(t.getType(), TokenType.COMMENT);
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ASSIGNMENT);
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.ID);
		t = s.getNextToken();
		assertEquals(t.getType(), TokenType.EOT);
	}
	
	@Test
	void testLexicalError() {
		String input = "var1 & var2";
		
		s = new Scanner(str2Stream(input));
		
		Token t = s.getNextToken();
		
		t = s.getNextToken();
		
		assertEquals(t.getType(), TokenType.ERROR);
	}
	
	private InputStream str2Stream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}
}
