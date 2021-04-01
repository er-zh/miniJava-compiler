package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.ContextualAnalyzer.IdChecker;
import miniJava.ContextualAnalyzer.TypeChecker;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
//import miniJava.SyntacticAnalyzer.Token;
//import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	
	public static void main(String[] args) {
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + args[0] + " not found");
			System.exit(1);
		}
		
		ErrorReporter e = new ErrorReporter();
		Scanner s = new Scanner(inputStream);
		Parser p = new Parser(s, e);
		
		AST parseTree = p.parse();
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(4);
		}
		
		//new ASTDisplay().showTree(parseTree);
		
		///*
		IdChecker ic = new IdChecker(e);
		ic.check(parseTree);
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(4);
		}
		
		TypeChecker tc = new TypeChecker(e);
		tc.check(parseTree);
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(4);
		}
		//*/
		
		System.exit(0);
		
	}

}
