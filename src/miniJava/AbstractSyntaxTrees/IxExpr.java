/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class IxExpr extends Expression {
	public Reference ref;
	public Expression ixExpr;
	
	public IxExpr(Reference r, Expression e) {
		super();
		ref = r;
		ixExpr = e;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitIxExpr(this, o);
	}


}
