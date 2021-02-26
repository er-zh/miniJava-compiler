/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

public class IdRef extends BaseRef {
	public Identifier id;

	public IdRef(Identifier id) {
		super();
		this.id = id;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitIdRef(this, o);
	}

}
