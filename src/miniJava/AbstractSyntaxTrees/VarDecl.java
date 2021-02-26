/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class VarDecl extends LocalDecl {
	
	public VarDecl(TypeDenoter t, String name) {
		super(name, t);
	}
	
	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitVarDecl(this, o);
	}
}
