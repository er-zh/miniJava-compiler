package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
//import miniJava.SyntacticAnalyzer.Token;
//import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	private static int rc = 0;
	
	public static void main(String[] args) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + args[0] + " not found");
			rc = 1;
			System.exit(rc);
		}
		
		Scanner s = new Scanner(inputStream);
		Parser p = new Parser(s);
		
		AST parseTree = p.parse();
		
		if(parseTree == null) {
			rc=4;
			System.exit(rc);
		}
		else {
			ASTDisplay td = new ASTDisplay();
			td.showTree(parseTree);
			
			System.exit(rc);
		}
	}

}
