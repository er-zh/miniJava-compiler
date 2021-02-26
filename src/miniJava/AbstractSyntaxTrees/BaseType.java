/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class BaseType extends TypeDenoter {
	public BaseType(TypeKind t) {
		super(t);
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitBaseType(this, o);
	}
}
