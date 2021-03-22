package softnet.asn;

class OctetStringEncoder implements ElementEncoder
{
	private byte[] m_Buffer;
	private int m_Offset;
	private int m_Length;
	
	public OctetStringEncoder(byte[] buffer, int offset, int length)
	{
		m_Buffer = buffer;
		m_Offset = offset;
		m_Length = length;
	}
	
	public int estimateTLV()
	{
		return 1 + LengthEncoder.estimate(m_Length) + m_Length;	
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		binStack.stack(m_Buffer, m_Offset, m_Length);
        int L_length = LengthEncoder.encode(m_Length, binStack);
	    binStack.stack(UniversalTag.OctetString);
	    return 1 + L_length + m_Length;
	}	
}
