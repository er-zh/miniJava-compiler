package miniJava.CodeGenerator;

import java.util.Stack;

import mJAM.Machine;

public class UnknownRoutine extends RuntimeEntityDescriptor {
	public Stack<Integer> patchCalls;
	
	public UnknownRoutine(int size) {
		super(size);
		patchCalls = new Stack<Integer>();
	}
	
	public void patchUnknownCalls(int codeaddr) {
		while(!patchCalls.isEmpty()) {
			Machine.patch(patchCalls.pop(), codeaddr);
		}
	}
}
