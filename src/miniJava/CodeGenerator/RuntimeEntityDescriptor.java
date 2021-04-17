package miniJava.CodeGenerator;

public abstract class RuntimeEntityDescriptor {
	public int size;
	
	// needs extending with known and unknown values and addresses
	public RuntimeEntityDescriptor(int size) {
		this.size = size;
	}
}
