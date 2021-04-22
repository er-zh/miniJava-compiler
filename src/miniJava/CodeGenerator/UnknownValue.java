package miniJava.CodeGenerator;

public class UnknownValue extends RuntimeEntityDescriptor {
	public int offset;
	
	public UnknownValue(int size, int disp) {
		super(size);
		offset = disp;
	}

}
