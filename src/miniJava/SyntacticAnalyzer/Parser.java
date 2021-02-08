package miniJava.SyntacticAnalyzer;

public class Parser {
	private boolean trace = true;
	
	private Scanner scanner;
	private Token currentToken;
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public boolean parse() {
		boolean succeed = false;
		currentToken = scanner.getNextToken();
		
		try {
			parseProgram();
			succeed = true;
		}
		catch(SyntaxError e) {
			e.printErrorMsg();
		}
		
		return succeed;
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
		
		while(currentToken.getType() != TokenType.RBRACE) {
			parseVis();
			parseAccess();
			
			if(currentToken.getType() == TokenType.VOID) {
				accept(TokenType.VOID);
				accept(TokenType.ID);
				
				parseMethodDecl();
			}
			else {
				parseType();
				
				accept(TokenType.ID);
				
				if(currentToken.getType() == TokenType.SEMICOLON) {
					parseFieldDecl();
				}
				else {
					parseMethodDecl();
				}
			}
		}
		
		accept(TokenType.RBRACE);
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
				throw new SyntaxError("invalid typing: expected non-void typing, "
						+ "but got " + currentToken.getType());
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
		if(currentToken.getType() == TokenType.ID 
				|| currentToken.getType() == TokenType.THIS) {
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
			break;
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
			// need to decide between statements of the form:
			// type(id) id = expr; and  ref (some kind of expr);
			// which requires left factoring of possible id
			advance();
			
			switch(currentToken.getType()) {
			case ID:
				// 2 id's indicates
				// declaring a new object id with type id
				advance();
				parseStatementAssign();
				break;
			case PERIOD:
				while(currentToken.getType() == TokenType.PERIOD) {
					advance();
					accept(TokenType.ID);
				}
				
				parseStatementRef();
				break;
			case ASSIGNMENT:
				// if the input is of the form
				// id =
				// then the id is a ref
				parseStatementAssign();
				break;
			case LPAREN:
				advance();
				
				if(currentToken.getType() != TokenType.RPAREN) {
					parseArgList();
				}
				
				accept(TokenType.RPAREN);
				break;
			case LSQUARE:
				advance();
				
				if(currentToken.getType() == TokenType.RSQUARE) {
					advance();
					accept(TokenType.ID);
				}
				else {
					parseExpr();
					accept(TokenType.RSQUARE);
				}
				parseStatementAssign();
				break;
			default:
				throw new SyntaxError("failed to parse statement");
			}
			break;
		case THIS:
			parseRef();
			
			parseStatementRef();
			break;
		case BOOLEAN:
		case INT:
			parseType();
			accept(TokenType.ID);
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
		accept(TokenType.ASSIGNMENT);
		parseExpr();
		accept(TokenType.SEMICOLON);
	}
	
	void parseExpr() {
		switch(currentToken.getType()) {
		case ID:
		case THIS:
			parseRef();
			
			if(currentToken.getType() == TokenType.LSQUARE) {
				advance();
				parseExpr();
				accept(TokenType.RSQUARE);
			}
			else if(currentToken.getType() == TokenType.LPAREN) {
				advance();
				if(currentToken.getType() != TokenType.RPAREN) {
					parseArgList();
				}
				accept(TokenType.RPAREN);
			}
			break;
		case LPAREN:
			advance();
			parseExpr();
			accept(TokenType.RPAREN);
			break;
		case NUM_LITERAL:
		case T:
		case F:
			advance();
			break;
		case NEW:
			advance();
			
			if(currentToken.getType() == TokenType.ID) {
				advance();
				if(currentToken.getType() == TokenType.LPAREN) {
					advance();
					accept(TokenType.RPAREN);
				}
				else {
					accept(TokenType.LSQUARE);
					parseExpr();
					accept(TokenType.RSQUARE);
				}
			}
			else if(currentToken.getType() == TokenType.INT) {
				advance();
				accept(TokenType.LSQUARE);
				parseExpr();
				accept(TokenType.RSQUARE);
			}
			else {
				throw new SyntaxError("invalid use of new in expression");
			}
			break;
		case UNOP:
			advance();
			parseExpr();
			break;
		default:
			throw new SyntaxError("invalid expression");
		}
		
		if(currentToken.getType() == TokenType.BINOP) {
			advance();
			parseExpr();
		}
		else if(currentToken.getLexeme().equals("-")) {
			currentToken.convUnop2Binop();
			advance();
			parseExpr();
		}
	}
	
	private void accept(TokenType expectedToken) throws SyntaxError {
		if (currentToken.getType() == expectedToken) {
			advance();
		}
		else {
			throw new SyntaxError(expectedToken, currentToken.getType());
		}
	}
	
	private void advance() {
		if (trace) pTrace();
		
		do {
			currentToken = scanner.getNextToken();
		} while(currentToken.getType() == TokenType.COMMENT);
		
		if(currentToken.getType() == TokenType.ERROR) {
			throw new SyntaxError("Lexical error, " + currentToken.getLexeme());
		}
	}
	
	private void pTrace() {
		StackTraceElement [] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i > 0 ; i--) {
			if(stl[i].toString().contains("parse"))
				System.out.println(stl[i]);
		}
		System.out.println("accepting: " + currentToken.getType() + " (\"" + currentToken.getLexeme() + "\")");
		System.out.println();
	}
		
}
