package softnet.asn;

class BooleanEncoder implements ElementEncoder
{
	private boolean m_Value;
	
	public BooleanEncoder(boolean value)
	{
		m_Value = value;
	}
	
	public int estimateTLV()
	{
		return 3;		
	}
	
	public int encodeTLV(BinaryStack binStack)
	{		
        if (m_Value)
        {
            binStack.stack((byte)0xFF);
            binStack.stack((byte)1);
            binStack.stack(UniversalTag.Boolean);
        }
        else
        {
        	binStack.stack((byte)0);
            binStack.stack((byte)1);
            binStack.stack(UniversalTag.Boolean);            
        }
        return 3;
	}
}
