/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public abstract class LocalDecl extends Declaration {

	public LocalDecl(String name, TypeDenoter t) {
		super(name, t);
	}

}
