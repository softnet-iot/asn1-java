package softnet.asn;

public class EndOfSequenceAsnException extends AsnException
{
	private static final long serialVersionUID = 8957059346962906349L;
	public EndOfSequenceAsnException()
	{
		super("There is no more input data in the Asn1 Sequence.");
	}
}
