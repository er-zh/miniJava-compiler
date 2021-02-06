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
			e.printErrorMsg();
		}
	}
	
	void parseProgram() {
		while(currentToken.getType() != TokenType.EOT) {
			parseClassDecl();
		}
		
		accept(TokenType.EOT);
	}
	
	void parseClassDecl() {
		accept(TokenType.CLASS);
		accept(TokenType.ID);
		accept(TokenType.LBRACE);
		
		parseVis();
		parseAccess();
		
		if(currentToken.getType() == TokenType.VOID) {
			accept(TokenType.VOID);
		}
		else {
			parseType();
		}
	}
	
	void parseFieldDecl() {
		accept(TokenType.SEMICOLON);
	}
	
	void parseMethodDecl() {
		accept(TokenType.LPAREN);
		
		if(currentToken.getType() != TokenType.RPAREN) {
			parseParamList();
		}
		
		accept(TokenType.RPAREN);
		accept(TokenType.LBRACE);
		
		while(currentToken.getType() != TokenType.RBRACE) {
			parseStatement();
		}
		
		accept(TokenType.RBRACE);
	}
	
	// can be empty
	void parseVis() {
		if(currentToken.getType() == TokenType.PRIVATE) {
			advance();
		}
		else if(currentToken.getType() == TokenType.PUBLIC) {
			advance();
		}
	}
	
	// can be empty
	void parseAccess() throws SyntaxError {
		if(currentToken.getType() == TokenType.STATIC) {
			advance();
		}
	}
	
	void parseType() {
		if(currentToken.getType() == TokenType.BOOLEAN) {
			advance();
		}
		else {
			if(currentToken.getType() == TokenType.INT || currentToken.getType() == TokenType.ID) {
				advance();
				if(currentToken.getType() == TokenType.LSQUARE) {
					advance();
					accept(TokenType.RSQUARE);
				}
			}
			else {
				throw new SyntaxError("incorrect typing");
			}
		}
	}
	
	// can be empty
	void parseParamList() {
		parseType();
		accept(TokenType.ID);
		
		while(currentToken.getType() == TokenType.COMMA) {
			advance();
			parseType();
			accept(TokenType.ID);
		}
	}
	
	// can be empty
	void parseArgList() {
		parseExpr();
		
		while(currentToken.getType() == TokenType.COMMA) {
			advance();
			parseExpr();
		}
	}
	
	void parseRef() {
		if(currentToken.getType() == TokenType.ID || currentToken.getType() == TokenType.THIS) {
			advance();
			
			while(currentToken.getType() == TokenType.PERIOD) {
				advance();
				accept(TokenType.ID);
			}
		}
		
	}
	
	void parseStatement() {
		switch(currentToken.getType()) {
		case LBRACE:
			advance();
			while(currentToken.getType() != TokenType.RBRACE) {
				parseStatement();
			}
			accept(TokenType.RBRACE);
			break;
		case IF:
			advance();
			accept(TokenType.LPAREN);
			parseExpr();
			accept(TokenType.RPAREN);
			
			parseStatement();
			
			if(currentToken.getType() == TokenType.ELSE) {
				advance();
				parseStatement();
			}
			
		case WHILE:
			advance();
			accept(TokenType.LPAREN);
			parseExpr();
			accept(TokenType.RPAREN);
			
			parseStatement();
			break;
		case RETURN:
			advance();
			if(currentToken.getType() != TokenType.SEMICOLON) {
				parseExpr();
			}
			accept(TokenType.SEMICOLON);
			break;
		case ID:
			// need to decide between 
			// type(id) id = expr;
			// and 
			// ref (some kind of expr);
			// which requires left factoring of possible id
			advance();
			
			if(currentToken.getType() == TokenType.PERIOD) {
				// remaining part of parsing a ref
				while(currentToken.getType() == TokenType.PERIOD) {
					advance();
					accept(TokenType.ID);
				}
				
				parseStatementRef();
			}
			else {
				// remaining portion of parsing a type
				if(currentToken.getType() == TokenType.LSQUARE) {
					advance();
					accept(TokenType.RSQUARE);
				}
				parseStatementAssign();
			}
			break;
		case THIS:
			parseRef();
			
			parseStatementRef();
			break;
		case BOOLEAN:
		case INT:
			parseType();
			parseStatementAssign();
			break;
		default:
			throw new SyntaxError("failed to parse statement");
		}
	}
	
	private void parseStatementRef() {
		switch(currentToken.getType()) {
		case ASSIGNMENT:
			advance();
			parseExpr();
			break;
		case LSQUARE:
			advance();
			parseExpr();
			accept(TokenType.RSQUARE);
			accept(TokenType.ASSIGNMENT);
			parseExpr();
			break;
		case LPAREN:
			advance();
			
			if(currentToken.getType() != TokenType.RPAREN) {
				parseArgList();
			}
			
			accept(TokenType.RPAREN);
			break;
		default:
			throw new SyntaxError("failed to parse ref statement");
		}
		accept(TokenType.SEMICOLON);
	}
	
	private void parseStatementAssign() {
		accept(TokenType.ID);
		accept(TokenType.ASSIGNMENT);
		parseExpr();
		accept(TokenType.SEMICOLON);
	}
	
	void parseExpr() {
		
	}
	
	private void accept(TokenType expectedToken) throws SyntaxError {
		if (currentToken.getType() == expectedToken) {
			/*if (trace)
				pTrace();*/
			advance();
		}
		else {
			throw new SyntaxError(expectedToken, currentToken.getType());
		}
	}
	
	private void advance() {
		currentToken = scanner.getNextToken();
		
		if(currentToken.getType() == TokenType.ERROR) {
			throw new SyntaxError("Lexical error, " + currentToken.getLexeme());
		}
	}
		
}
