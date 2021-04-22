/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class MethodDeclList implements Iterable<MethodDecl> {
	private List<MethodDecl> methodDeclList;

	public MethodDeclList() {
		methodDeclList = new ArrayList<MethodDecl>();
	}

	public void add(MethodDecl cd) {
		methodDeclList.add(cd);
	}

	public MethodDecl get(int i) {
		return methodDeclList.get(i);
	}

	public int size() {
		return methodDeclList.size();
	}

	public Iterator<MethodDecl> iterator() {
		return methodDeclList.iterator();
	}

}
