package miniJava.CodeGenerator;

public class KnownRoutine extends RuntimeEntityDescriptor {
	public int codeaddr;
	
	public KnownRoutine(int size, int codeaddr) {
		super(size);
		this.codeaddr = codeaddr;
	}

}
