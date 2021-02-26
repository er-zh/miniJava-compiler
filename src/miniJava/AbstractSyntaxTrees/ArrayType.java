/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */

package miniJava.AbstractSyntaxTrees;

public class ArrayType extends TypeDenoter {
	public TypeDenoter eltType;

	public ArrayType(TypeDenoter eltType) {
		super(TypeKind.ARRAY);
		this.eltType = eltType;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitArrayType(this, o);
	}

}