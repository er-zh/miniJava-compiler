package miniJava.SyntacticAnalyzer;

public class SyntaxError extends Error{
	private static final long serialVersionUID = 1L;
	
	private TokenType expected;
	private TokenType recieved;
	private String errorMsg;
	
	public SyntaxError(TokenType expect, TokenType found) {
		expected = expect;
		recieved = found;
		
		errorMsg = "expecting '" + expected + "' but found '" + recieved + "'";
	}
	
	public SyntaxError(String error) {
		errorMsg = error;
	}
	
	public SyntaxError(TokenType expect, TokenType found, String error) {
		expected = expect;
		recieved = found;
		
		errorMsg = error;
	}
	
	public void printErrorMsg() {
		System.out.println(errorMsg);
	}
}
