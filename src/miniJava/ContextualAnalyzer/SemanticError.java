package miniJava.ContextualAnalyzer;

import miniJava.SourcePosition;
import miniJava.CompilerError;

public class SemanticError extends CompilerError {
	private static final long serialVersionUID = 1L;
	
	public SemanticError(String error, SourcePosition sp, boolean typeError) {
		super(sp, (typeError ? "Type Error" : "Identification Error") + " --> " + error);
	}
	
}
