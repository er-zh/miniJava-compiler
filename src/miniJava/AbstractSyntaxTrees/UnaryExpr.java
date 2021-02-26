/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class UnaryExpr extends Expression {
	public Operator operator;
	public Expression expr;

	public UnaryExpr(Operator o, Expression e) {
		super();
		operator = o;
		expr = e;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitUnaryExpr(this, o);
	}

}