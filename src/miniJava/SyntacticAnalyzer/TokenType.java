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
		PERIOD,
	//keywords
		CLASS,
		NEW,
		// control structures
		IF,
		ELSE,
		WHILE,
		FOR,
		RETURN,
		// visibility
		PUBLIC,
		PRIVATE,
		// scope related
		STATIC,
		THIS,
		// primitive types / literals
		INT,
		BOOLEAN, T, F, NULL,
		VOID,
	// sets of terminals, arbitrarily identified
	ID, // name
	NUM_LITERAL,
	UNOP, // punctuation
	BINOP,
	COMMENT,
	ERROR // lexing error encountered
}
