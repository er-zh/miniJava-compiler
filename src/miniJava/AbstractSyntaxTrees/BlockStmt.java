/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;

public class BlockStmt extends Statement {
	public StatementList sl;
	
	public BlockStmt(StatementList sl, SourcePosition posn) {
		super(posn);
		this.sl = sl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitBlockStmt(this, o);
	}

}