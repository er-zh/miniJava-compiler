/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class AssignStmt extends Statement {
	public Reference ref;
	public Expression val;

	public AssignStmt(Reference r, Expression e) {
		super();
		ref = r;
		val = e;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitAssignStmt(this, o);
	}

}