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
	
	public static void main(String[] args) {
		InputStream inputStream = null;
		int rc = 0;
		
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + args[0] + " not found");
			rc = 1;
			System.exit(rc);
		}
		
		ErrorReporter e = new ErrorReporter();
		Scanner s = new Scanner(inputStream);
		Parser p = new Parser(s, e);
		
		AST parseTree = p.parse();
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			rc=4;
		}
		else {
			ASTDisplay td = new ASTDisplay();
			td.showTree(parseTree);
		}
		
		System.exit(rc);
	}

}
