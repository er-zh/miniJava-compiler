/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ClassDeclList implements Iterable<ClassDecl> {
	private List<ClassDecl> classDeclList;

	public ClassDeclList() {
		classDeclList = new ArrayList<ClassDecl>();
	}

	public void add(ClassDecl cd) {
		classDeclList.add(cd);
	}

	public ClassDecl get(int i) {
		return classDeclList.get(i);
	}

	public int size() {
		return classDeclList.size();
	}

	public Iterator<ClassDecl> iterator() {
		return classDeclList.iterator();
	}
	
}
