package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Scanner {
	// TODO find the correct end of stream markers
	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';
	private final static Pattern letterRx = Pattern.compile("[a-zA-Z]");
	private final static Pattern digitRx = Pattern.compile("[0-9]");
	private final static Pattern whitespaceRx = Pattern.compile(" |\t|\n|\r");
	/*private final static Pattern keywordRx = Pattern.compile("public|private|"
			+ "static|this|"
			+ "boolean|true|false|int|void"
			+ "if|else|while|return");*/
	// TODO mess with load factor and see if anything changes
	private final HashMap<String, TokenType> keywordDict = new HashMap<String, TokenType>(60);
	private final HashMap<Character, TokenType> puncDict = new HashMap<Character, TokenType>(50);
	
	private InputStream scanInput;
	private char currentChar;
	private boolean eot;
	
	public Scanner(InputStream input) {
		scanInput = input;
		eot = false;
		
		// initialize the keyword hashmap with the values of keywords
		keywordDict.put("public", TokenType.PUBLIC);
		keywordDict.put("private", TokenType.PRIVATE);
		keywordDict.put("static", TokenType.STATIC);
		keywordDict.put("this", TokenType.THIS);
		keywordDict.put("boolean", TokenType.BOOLEAN);
		keywordDict.put("true", TokenType.T);
		keywordDict.put("false", TokenType.F);
		keywordDict.put("int", TokenType.INT);
		keywordDict.put("void", TokenType.VOID);
		keywordDict.put("if", TokenType.IF);
		keywordDict.put("else", TokenType.ELSE);
		keywordDict.put("while", TokenType.WHILE);
		keywordDict.put("new", TokenType.NEW);
		keywordDict.put("return", TokenType.RETURN);
		
		// init another hashmap for punctuation
		puncDict.put('+', TokenType.BINOP);
		puncDict.put('*', TokenType.BINOP);
		// TODO figure out how to parse the token type
		// for now parse as unop -> add negative if needed
		puncDict.put('-', TokenType.UNOP);
		puncDict.put('{', TokenType.LBRACE);
		puncDict.put('}', TokenType.RBRACE);
		puncDict.put('[', TokenType.LSQUARE);
		puncDict.put(']', TokenType.RSQUARE);
		puncDict.put('(', TokenType.LPAREN);
		puncDict.put(')', TokenType.RPAREN);
		puncDict.put(';', TokenType.SEMICOLON);
		puncDict.put(',', TokenType.COMMA);
		puncDict.put('.', TokenType.PERIOD);
		// '/', '=', '<', '>', '&', '|', '!' need special handling
		// since they may be part of multi character operators
		
		// initialize scanner by loading in first char of input
		nextChar();
	}
	
	public Token getNextToken() {
		// get rid of whitespace and comments
		while(!eot && isWhitespace(currentChar)) {
			// skip character
			nextChar();
		}
		
		StringBuilder currentLexeme = new StringBuilder();
		
		TokenType type = scan(currentLexeme);

		return new Token(type, currentLexeme.toString());
	}
	
	private TokenType scan(StringBuilder lexeme) {
		if(eot) {
			return TokenType.EOT;
		}
		else if(isLetter(currentChar)) { //keyword or id
			return scanName(lexeme);
		}
		else if(isDigit(currentChar)) { //scan a number
			return scanNum(lexeme);
		}
		else { //check for punctuation
			return scanPunc(lexeme);
		}
	}
	
	private TokenType scanName(StringBuilder lexeme) {
		advanceScanner(lexeme);
		
		while(isLetter(currentChar) || isDigit(currentChar) || currentChar=='_') {
			advanceScanner(lexeme);
		}
		
		String lexStr = lexeme.toString();
		
		return keywordDict.containsKey(lexStr) ? keywordDict.get(lexStr) : TokenType.ID;
	}
	
	private TokenType scanNum(StringBuilder lexeme) {
		advanceScanner(lexeme);
		
		while(isDigit(currentChar)) {
			advanceScanner(lexeme);
		}
		
		return TokenType.NUM_LITERAL;
	}
	
	private TokenType scanPunc(StringBuilder lexeme) {
		switch(currentChar) {
		case '/':
			advanceScanner(lexeme);
			
			if(currentChar == '/' || currentChar == '*') {
				consumeComment(currentChar == '*');
				return TokenType.COMMENT;
			}
			return TokenType.BINOP;
		case '<':
		case '>':
			advanceScanner(lexeme);
			
			if(currentChar == '=') {
				advanceScanner(lexeme);
			}
			return TokenType.BINOP;
		case '=':
			advanceScanner(lexeme);
			
			if(currentChar == '=') {
				advanceScanner(lexeme);
				return TokenType.BINOP;
			}
			return TokenType.ASSIGNMENT;
		case '!':
			advanceScanner(lexeme);
			
			if(currentChar == '=') {
				advanceScanner(lexeme);
				return TokenType.BINOP;
			}
			return TokenType.UNOP;
		case '&':
			advanceScanner(lexeme);
			
			if(currentChar == '&') {
				advanceScanner(lexeme);
				return TokenType.BINOP;
			}
			// TODO implement proper error reporting
			// expected an and (&&) operator but got something else
			return TokenType.ERROR;
		case '|':
			advanceScanner(lexeme);
			
			if(currentChar == '|') {
				advanceScanner(lexeme);
				return TokenType.BINOP;
			}
			// same as for and op
			return TokenType.ERROR;
		default:
			// special cases have been checked
			// input is either a one char op or a lexing error is reached
			
			char op = currentChar;
			advanceScanner(lexeme);
			
			return puncDict.containsKey(op) ? puncDict.get(op) : TokenType.ERROR;
		}
	}
	
	private void consumeComment(boolean multiline) {		
		if(multiline) {
			boolean loop = true;
			
			while(loop) {
				if(eot) break; // TODO need to throw error in the case of malformed
				// comment
				
				nextChar(); //called directly bc the lexeme associated with
				// a comment need not be saved
				
				if(currentChar == '*') {
					nextChar();
					
					if(currentChar == '/') {
						nextChar();
						
						loop = false;
					}
				}
			}
		}
		else {
			while(currentChar != eolWindows && currentChar != eolUnix) {
				nextChar();
			}
			nextChar();
		}
	}
	
	private void advanceScanner(StringBuilder lexeme) {
		lexeme.append(currentChar);
		nextChar();
	}
	
	// gets the next character in the input stream by storing it in the currentChar field
	private void nextChar() {
		if(eot) return;
		
		try {
			int c = scanInput.read();
			
			if(c == -1) {
				currentChar = ' ';
				eot = true;
			}
			else {
				currentChar = (char) c;
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			eot = true;
		}
		
	}
	
	private boolean isDigit(char c) {
		return digitRx.matcher(c+"").matches();
	}
	
	private boolean isLetter(char c) {
		return letterRx.matcher(c+"").matches();
	}
	
	private boolean isWhitespace(char c) {
		return whitespaceRx.matcher(c+"").matches();
	}

}
