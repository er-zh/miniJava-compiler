package miniJava;

import java.util.ArrayList;

public class ErrorReporter {
	private ArrayList<CompilerError> errors;

	public ErrorReporter() {
		errors = new ArrayList<CompilerError>();
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public void reportError(CompilerError e) {
		errors.add(e);
	}
	
	public String getErrorReport() {
		// TODO add line number locations to error report
		return errors.get(0).toString();
	}
}
