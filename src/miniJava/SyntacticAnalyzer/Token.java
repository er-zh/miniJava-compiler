package miniJava.SyntacticAnalyzer;

public class Token {
	private TokenType type;
	private String lexeme;
	
	public Token(TokenType t, String text) {
		type = t;
		lexeme = text;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	// method specifically for - operator
	public void convUnop2Binop() {
		if(type == TokenType.UNOP) {
			type = TokenType.BINOP;
		}
	}
}
