/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;

public class Identifier extends Terminal {
	private Declaration definingDecl;
	
	public Identifier(Token t, SourcePosition posn) {
		super(t, posn);
		definingDecl = null;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitIdentifier(this, o);
	}
	
	public void linkDecl(Declaration dec) {
		definingDecl = dec;
	}
	
	public Declaration getDecl() {
		return definingDecl;
	}

}
