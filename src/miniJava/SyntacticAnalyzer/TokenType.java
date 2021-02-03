package miniJava.SyntacticAnalyzer;

public enum TokenType {
	EOT,
	//punctuation
		// = sign
		ASSIGNMENT,
		// delimiters
		LPAREN,
		RPAREN,
		LBRACE,
		RBRACE,
		LSQUARE,
		RSQUARE,
		SEMICOLON,
		COMMA,
	//keywords
		CLASS,
		// control structures
		IF,
		ELSE,
		WHILE,
		RETURN,
		// visibility
		PUBLIC,
		PRIVATE,
		// scope related
		STATIC,
		THIS,
		// primitive types
		INT,
		BOOLEAN, T, F,
		VOID,
	// sets of terminals, arbitrarily identified
	ID, // name
	NUM_LITERAL,
	UNOP, // punctuation
	BINOP,
	COMMENT,
	ERROR // lexing error encountered
}
