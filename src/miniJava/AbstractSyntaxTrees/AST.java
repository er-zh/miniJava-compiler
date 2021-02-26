/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public abstract class AST {

	public AST() {}

	public String toString() {
		String fullClassName = this.getClass().getName();
		String cn = fullClassName.substring(1 + fullClassName.lastIndexOf('.'));

		return cn;
	}

	public abstract <A, R> R visit(Visitor<A, R> v, A o);

}
