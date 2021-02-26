/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class FieldDecl extends MemberDecl {
	
	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name){
		super(isPrivate, isStatic, t, name);
	}
	
	public FieldDecl(MemberDecl md) {
		super(md);
	}
	
	public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitFieldDecl(this, o);
    }
}

