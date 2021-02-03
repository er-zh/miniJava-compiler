package miniJava;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	private static int rc = 0;
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		Token t = s.getNextToken();
		System.out.println(t.getType());
		System.out.println(t.getLexeme());
		
		while(t.getType() != TokenType.EOT) {
			t = s.getNextToken();
			System.out.println(t.getType());
			System.out.println(t.getLexeme());
		}
		
		
		System.out.println(rc);
		System.exit(rc);
	}

}
