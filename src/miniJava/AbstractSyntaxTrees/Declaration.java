/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;
import miniJava.CodeGenerator.RuntimeEntityDescriptor;

public abstract class Declaration extends AST {
	public String name;
	public TypeDenoter type;
	private RuntimeEntityDescriptor red;

	public Declaration(String name, TypeDenoter type, SourcePosition posn) {
		super(posn);
		this.name = name;
		this.type = type;
		this.red = null;
	}
	
	public void setRED(RuntimeEntityDescriptor red) {
		this.red = red;
	}
	
	public RuntimeEntityDescriptor getRED() {
		return red;
	}
}
