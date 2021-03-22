package softnet.asn;

public class ASNEncoder 
{
    private SequenceEncoderImp m_Sequence;

	public ASNEncoder()
    {
        m_Sequence = new SequenceEncoderImp();
    }

    public SequenceEncoder Sequence()
    {
        return m_Sequence;
    }

    public byte[] getEncoding()
    {
    	int estimatedlength = m_Sequence.estimateTLV();
        BinaryStack binStack = new BinaryStack();
        binStack.allocate(estimatedlength);
        m_Sequence.encodeTLV(binStack);
        
        if(binStack.position() == 0)
        	return binStack.buffer();
        
        byte[] trimmedBuffer = new byte[binStack.count()];
        System.arraycopy(binStack.buffer(), binStack.position(), trimmedBuffer, 0, binStack.count());
        return trimmedBuffer;
    }

    public AsnEncoding getHeadedEncoding()
    {
        int estimatedlength = m_Sequence.estimateTLV();
        BinaryStack binStack = new BinaryStack();
        binStack.allocate(estimatedlength);
        m_Sequence.encodeTLV(binStack);

        return new AsnEncoding(binStack.buffer(), binStack.position());
    }	

    public AsnEncoding getHeadedEncoding(int headerSize)
    {
        if (headerSize < 0)
            throw new IllegalArgumentException("The value of 'headerSize' must not be negative.");

        int estimatedlength = m_Sequence.estimateTLV();
        BinaryStack binStack = new BinaryStack();
        binStack.allocate(headerSize + estimatedlength);
        m_Sequence.encodeTLV(binStack);

        return new AsnEncoding(binStack.buffer(), binStack.position());
    }	
}























