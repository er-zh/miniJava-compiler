package miniJava.CodeGenerator;

public class Field extends RuntimeEntityDescriptor {
	public int offset;
	
	public Field(int size, int fieldoffset) {
		super(size);
		offset = fieldoffset;
	}

}
