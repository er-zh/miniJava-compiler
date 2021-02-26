/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

abstract public class TypeDenoter extends AST {
	public TypeKind typeKind;

	public TypeDenoter(TypeKind type) {
		super();
		typeKind = type;
	}
	
}
