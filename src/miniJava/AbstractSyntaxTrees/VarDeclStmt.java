/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class VarDeclStmt extends Statement {
	public VarDecl varDecl;
	public Expression initExp;

	public VarDeclStmt(VarDecl vd, Expression e) {
		super();
		varDecl = vd;
		initExp = e;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitVardeclStmt(this, o);
	}

}
