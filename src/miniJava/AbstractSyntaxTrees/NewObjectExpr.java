/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class NewObjectExpr extends NewExpr {
	public ClassType classtype;

	public NewObjectExpr(ClassType ct) {
		super();
		classtype = ct;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitNewObjectExpr(this, o);
	}

}
