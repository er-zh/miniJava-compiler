/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;

public abstract class Reference extends AST {
	private Declaration controllingDecl;

	public Reference(SourcePosition posn) {
		super(posn);
		controllingDecl = null;
	}
	
	public void linkDecl(Declaration dec) {
		controllingDecl = dec;
	}
	
	public Declaration getDecl() {
		return controllingDecl;
	}
}
