package miniJava.SyntacticAnalyzer;

import miniJava.CompilerError;

public class SyntaxError extends CompilerError{
	private static final long serialVersionUID = 1L;
	
	public SyntaxError(TokenType expect, TokenType found) {
		super("expecting '" + expect + "' but found '" + found + "'");
	}
	
	public SyntaxError(String error) {
		super(error);
	}
	
	public String toString() {
		return "Syntax Error:: " + errorMessage;
	}
}
