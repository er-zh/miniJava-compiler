package miniJava;

public class CompilerError extends Error{
	private static final long serialVersionUID = 1L;
	
	protected String errorMessage;
	private SourcePosition srcpos;
	
	public CompilerError(SourcePosition sp) {
		srcpos = sp;
		errorMessage = "";
	}
	
	public CompilerError(SourcePosition sp, String error) {
		srcpos = sp;
		errorMessage = error;
	}
	
	public void printErrorMsg() {
		System.out.println(errorMessage);
	}
	
	public String toString() {
		return "*** line " + srcpos.getStartLine() + ": " + errorMessage;
	}
}
