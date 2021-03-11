package miniJava.SyntacticAnalyzer;

public class SourcePosition {
	private int start, end;
	
	public SourcePosition(int s) {
		start = s;
		end = s;
	}

	public SourcePosition(int s, int f) {
		start = s;
		end = f;
	}

	public String toString() {
		return "(" + start + ", " + end + ")";
	}
	
	public int getStartLine() {
		return start;
	}
	
	public int getEndLine() {
		return end;
	}
}
