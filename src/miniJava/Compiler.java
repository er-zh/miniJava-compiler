package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import mJAM.ObjectFile;
import miniJava.AbstractSyntaxTrees.AST;
import miniJava.CodeGenerator.Encoder;
import miniJava.ContextualAnalyzer.IdChecker;
import miniJava.ContextualAnalyzer.TypeChecker;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
//import miniJava.SyntacticAnalyzer.Token;
//import miniJava.SyntacticAnalyzer.TokenType;

public class Compiler {
	private static final int compilefail_code = 4;
	private static final int filenotfound_code = 1;
	private static final int success_code = 0;
	
	public static void main(String[] args) {
		InputStream inputStream = null;
		
		try {
			inputStream = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + args[0] + " not found");
			System.exit(filenotfound_code);
		}
		
		ErrorReporter e = new ErrorReporter();
		Scanner s = new Scanner(inputStream);
		Parser p = new Parser(s, e);
		
		AST parseTree = p.parse();
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(compilefail_code);
		}
		
		//new ASTDisplay().showTree(parseTree);
		
		///*
		IdChecker ic = new IdChecker(e);
		ic.check(parseTree);
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(compilefail_code);
		}
		else if(!ic.hasUniqueMain()) {
			System.out.println("*** line 0: Error --> Program is missing a main method or has multiple main methods");
			System.exit(compilefail_code);
		}
		
		TypeChecker tc = new TypeChecker(e);
		tc.check(parseTree);
		
		if(e.hasErrors()) {
			System.out.println(e.getErrorReport());
			System.exit(compilefail_code);
		}
		//*/
		
		Encoder ecd = new Encoder(ic.mainClass());
		ecd.encode(parseTree);
		
		String mJamFileName = args[0].replace(".mJAM",".asm");
		
		ObjectFile objF = new ObjectFile(mJamFileName);
		
		if (objF.write()) {
			System.exit(success_code);
		}
		else {
			System.exit(compilefail_code);
		}
		
		
		
	}

}
