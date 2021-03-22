package softnet.asn;

public class TypeMismatchAsnException extends AsnException
{
	private static final long serialVersionUID = 154896164472176485L;
	public TypeMismatchAsnException()
	{
		super("The type of the input content do not match what is trying to be accessed.");
	}
}
