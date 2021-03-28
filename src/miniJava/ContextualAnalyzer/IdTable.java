package miniJava.ContextualAnalyzer;

import java.util.HashMap;
import java.util.Stack;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.Declaration;

public class IdTable {
	private class SDecl{ //scoped declaration
		public int scopelevel;
		public Declaration decl;
	}
	
	private Stack<HashMap<String, SDecl>> table;
	private int level;
	private ErrorReporter err;
	
	public IdTable(ErrorReporter reporter) {
		table = new Stack<HashMap<String, SDecl>>();
		level = 0;
		err = reporter;
	}
	
	public void enter(Declaration dec) throws SemanticError{
		HashMap<String, SDecl> currentScope = table.peek();
		String name = dec.name;
		SDecl sd = new SDecl();
		sd.scopelevel = level;
		sd.decl = dec;
		
		// check for identification errors
		if(currentScope.containsKey(name)) {
			SDecl shadowed = currentScope.get(name);
			
			if(shadowed.scopelevel == level) {
				err.reportError(new SemanticError(name + " duplicates a declaration in the same scope",
						dec.posn, false));
			}
			else if(level > 3 && shadowed.scopelevel >= 3) {
				err.reportError(new SemanticError(name + " is a local variable (level 4+) which "
						+ "may not hide other local names or parameter names (level 3+)", 
						dec.posn, false));
			}
		}
		
		currentScope.put(name, sd);
	}

	public Declaration retrieve(String name) {
		SDecl sd = table.peek().get(name);
		if(sd == null) return null;
		return sd.decl;
	}
	
	public void openScope() {
		if(table.isEmpty()) {
			table.push(new HashMap<String, SDecl>());
		}
		else {
			table.push(new HashMap<String, SDecl>(table.peek()));
		}
		
		level++;
	}
	
	public void closeScope() {
		table.pop();
		
		level--;
	}
}

