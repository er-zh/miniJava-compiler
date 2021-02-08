package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	private static int rc = 0;
	
	public static void main(String[] args) {
		/*InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + args[0] + " not found");
			rc = 1;
			System.exit(rc);
		}
		
		Scanner s = new Scanner(inputStream);*/
		
		Scanner s = new Scanner(System.in);
		Parser p = new Parser(s);
		
		p.parse();
		
		/*Token t = s.getNextToken();
		System.out.println(t.getType());
		System.out.println(t.getLexeme());
		
		while(t.getType() != TokenType.EOT) {
			t = s.getNextToken();
			System.out.println(t.getType());
			System.out.println(t.getLexeme());
		}*/
		
		
		System.out.println(rc);
		System.exit(rc);
	}

}
