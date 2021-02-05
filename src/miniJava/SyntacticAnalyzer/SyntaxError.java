package miniJava.SyntacticAnalyzer;

public class SyntaxError extends Error{
	private static final long serialVersionUID = 1L;
	
	private TokenType expected;
	private TokenType recieved;
	
	public SyntaxError(TokenType expect, TokenType found) {
		expected = expect;
		recieved = found;
	}
	
	public void printErrorMsg() {
		System.out.println("expecting '" + expected +
				"' but found '" + recieved + "'");
	}
}
