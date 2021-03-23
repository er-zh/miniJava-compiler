package miniJava.AbstractSyntaxTrees;

import miniJava.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;

public class NullLiteral extends Terminal {
	
	public NullLiteral(Token t, SourcePosition posn) {
		super(t, posn);
	}
	
	@Override
	public <A, R> R visit(Visitor<A, R> v, A o) {
	      return v.visitNullLiteral(this, o);
	}

}
