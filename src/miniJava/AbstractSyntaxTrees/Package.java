/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class Package extends AST {
	public ClassDeclList classDeclList;

	public Package(ClassDeclList cdl) {
		super();
		classDeclList = cdl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitPackage(this, o);
	}

}
