package miniJava.CodeGenerator;

public class UnknownAddress extends RuntimeEntityDescriptor {
	public int offset;
	
	public UnknownAddress(int size, int offset) {
		super(size);
		this.offset = offset;
	}

}
