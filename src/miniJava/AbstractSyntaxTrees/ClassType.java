/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class ClassType extends TypeDenoter {
	public Identifier className;

	public ClassType(Identifier cn) {
		super(TypeKind.CLASS);
		className = cn;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitClassType(this, o);
	}

}
