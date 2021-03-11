package miniJava;

public class CompilerError extends Error{
	private static final long serialVersionUID = 1L;
	
	protected String errorMessage;
	
	public CompilerError() {
		errorMessage = "";
	}
	
	public CompilerError(String error) {
		errorMessage = error;
	}
	
	public void printErrorMsg() {
		System.out.println(errorMessage);
	}
	
	public String toString() {
		return "Compilation Error:: " + errorMessage;
	}
}
