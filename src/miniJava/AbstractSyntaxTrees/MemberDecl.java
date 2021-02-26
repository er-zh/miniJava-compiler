/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

abstract public class MemberDecl extends Declaration {
	public boolean isPrivate;
	public boolean isStatic;

	public MemberDecl(boolean isPrivate, boolean isStatic, TypeDenoter mt, String name) {
		super(name, mt);
		this.isPrivate = isPrivate;
		this.isStatic = isStatic;
	}

	public MemberDecl(MemberDecl md) {
		super(md.name, md.type);
		this.isPrivate = md.isPrivate;
		this.isStatic = md.isStatic;
	}

}
