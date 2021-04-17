package miniJava.CodeGenerator;

public class KnownValue extends RuntimeEntityDescriptor {
	public int value;

	public KnownValue(int size, int value) {
		super(size);
		this.value = value;
	}
}
