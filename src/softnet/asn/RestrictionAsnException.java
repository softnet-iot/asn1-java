package softnet.asn;

public class RestrictionAsnException extends AsnException
{
	private static final long serialVersionUID = 2395269591363432969L;

	public RestrictionAsnException(String message)
	{		
		super(message);
	}
	
    public RestrictionAsnException()
    {
    	super("The input data doesn't match the validation restrictions.");
    }	
}
