/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

abstract public class Terminal extends AST {
	public TokenType kind;
	public String spelling;

	public Terminal(Token t, SourcePosition posn) {
		super(posn);
		spelling = t.getLexeme();
		kind = t.getType();
	}

}
