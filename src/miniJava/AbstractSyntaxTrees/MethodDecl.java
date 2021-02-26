/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class MethodDecl extends MemberDecl {
	public ParameterDeclList parameterDeclList;
	public StatementList statementList;

	public MethodDecl(MemberDecl md, ParameterDeclList pl, StatementList sl) {
		super(md);
		parameterDeclList = pl;
		statementList = sl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitMethodDecl(this, o);
	}

}
