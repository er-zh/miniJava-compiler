package miniJava.SyntacticAnalyzer;

import miniJava.CompilerError;
import miniJava.SourcePosition;

public class SyntaxError extends CompilerError{
	private static final long serialVersionUID = 1L;
	
	public SyntaxError(TokenType expect, TokenType found, SourcePosition sp) {
		super(sp, "Syntax Error --> expecting '" + expect + "' but found '" + found + "'");
	}
	
	public SyntaxError(String error, SourcePosition sp) {
		super(sp, "Syntax Error --> " + error);
	}
	
}
