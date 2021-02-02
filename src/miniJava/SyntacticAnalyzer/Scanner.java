package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Scanner {
	// TODO find the correct end of stream markers
	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';
	private final static Pattern letterRx = Pattern.compile("[a-zA-Z]");
	private final static Pattern digitRx = Pattern.compile("[0-9]");
	private final static Pattern keywordRx = Pattern.compile("public|private|"
			+ "static|this|"
			+ "boolean|true|false|int|void"
			+ "if|else|while|return");
	
	private InputStream scanInput;
	private char currentChar;
	private boolean eot;
	
	public Scanner(InputStream input) {
		scanInput = input;
		
		eot = false;
		
		// initialize scanner by loading in first char of input
		nextChar();
	}
	
	public Token getNextToken() {
		// get rid of whitespace and comments
		while(!eot && currentChar == ' ') {
			// skip character
			nextChar();
		}
		
		// TODO get rid of comments
		StringBuilder currentLexeme = new StringBuilder();
		
		TokenType type = scan(currentLexeme);
		
		String tokenText = currentLexeme.toString();
		
		return new Token(type, tokenText);
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
			
		}
	}
	
	private TokenType scanName(StringBuilder lexeme) {
		lexeme.append(currentChar);
		nextChar();
		
		while(isLetter(currentChar) || isDigit(currentChar) || currentChar=='_') {
			lexeme.append(currentChar);
			nextChar();
		}
		
		String lexStr = lexeme.toString();
		Matcher m = keywordRx.matcher(lexStr);
		
		if(m.matches()) {
			switch(lexStr.charAt(0)) {
			case 'i':
				return lexStr.equals("if") ? TokenType.IF : TokenType.INT;
			case 'p':
				return lexStr.equals("public") ? TokenType.PUBLIC : TokenType.PRIVATE;
			case 't':
				return lexStr.equals("true") ? TokenType.T : TokenType.THIS;
			case 'b':
				return TokenType.BOOLEAN;
			case 's':
				return TokenType.STATIC;
			case 'f':
				return TokenType.F;
			case 'e':
				return TokenType.ELSE;
			case 'w':
				return TokenType.WHILE;
			case 'r':
				return TokenType.RETURN;
			case 'v':
				return TokenType.VOID;
			default:
				//error, regex matched something that definitely wasn't a keyword
				System.out.println("ERROR: keyword regex matched non keyword");
				eot = true;
				return TokenType.EOT;
			}
		}
		else {
			return TokenType.ID;
		}
	}
	
	private TokenType scanNum(StringBuilder lexeme) {
		lexeme.append(currentChar);
		nextChar();
		
		while(isDigit(currentChar)) {
			lexeme.append(currentChar);
			nextChar();
		}
		
		return TokenType.NUM_LITERAL;
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
	
	private void consumeComment() {
		
	}
	

}
