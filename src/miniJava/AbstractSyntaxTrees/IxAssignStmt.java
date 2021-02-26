/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class IxAssignStmt extends Statement {
	public Reference ref;
	public Expression ix;
	public Expression exp;

	public IxAssignStmt(Reference r, Expression i, Expression e) {
		super();
		ref = r;
		ix = i;
		exp = e;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitIxAssignStmt(this, o);
	}

}
