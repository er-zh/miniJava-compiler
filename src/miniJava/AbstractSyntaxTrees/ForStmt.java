package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;

public class ForStmt extends Statement {
	// any of the initialization, termination, increment
	// may be null to represent an empty input to that 
	// portion of the for loop
	
	// always a vardecl or assignment stmt
	public Statement initialization;
	public Expression termination;
	// always an assignment stmt
	public Statement increment;
	public Statement body;

	public ForStmt(Statement init, Expression term, Statement inc, Statement bod, SourcePosition posn) {
		super(posn);
		initialization = init;
		termination = term;
		increment = inc;
		body = bod;
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
		return v.visitForStmt(this, o);
	}

}
