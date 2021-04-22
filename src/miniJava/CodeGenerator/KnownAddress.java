package miniJava.CodeGenerator;

public class KnownAddress extends RuntimeEntityDescriptor{
	public int heapdisp;
	
	public KnownAddress(int size, int disp) {
		super(size);
		heapdisp = disp;
	}

}
