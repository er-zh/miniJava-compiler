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
		return errors.get(0).toString();
	}
	
	public String toString() {
		String report = "";
		
		for(CompilerError err : errors) {
			report = report + err.toString() + "\n";
		}
		
		return report;
	}
}
