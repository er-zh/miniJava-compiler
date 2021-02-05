package miniJava.SyntacticAnalyzer;

public class Parser {
	private Scanner scanner;
	private Token currentToken;
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void parse() {
		currentToken = scanner.getNextToken();
		
		try {
			parseProgram();
		}
		catch(SyntaxError e) {
			
		}
	}
	
	void parseProgram() {
		while(currentToken.getType() != TokenType.EOT) {
			parseClassDecl();
		}
		
		accept(TokenType.EOT);
	}
	
	void parseClassDecl() {
		
	}
	
	void parseFieldDecl() {
		
	}
	
	void parseMethoDecl() {
		
	}
	
	void parseVis() {
		
	}
	
	void parseAccess() {
		
	}
	
	void parseType() {
		
	}
	
	void parseParamList() {
		
	}
	
	void parseArgList() {
		
	}
	
	void parseRef() {
		
	}
	
	void parseStatement() {
		
	}
	
	void parseExpr() {
		
	}
	
	private void accept(TokenType expectedToken) throws SyntaxError {
		if (currentToken.getType() == expectedToken) {
			/*if (trace)
				pTrace();*/
			currentToken = scanner.getNextToken();
		}
		else {
			throw new SyntaxError(expectedToken, currentToken.getType());
		}
	}
		
}
