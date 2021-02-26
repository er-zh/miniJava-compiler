/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class LiteralExpr extends Expression {
	public Terminal lit;

	public LiteralExpr(Terminal t) {
		super();
		lit = t;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitLiteralExpr(this, o);
	}
}