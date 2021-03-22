package softnet.asn;

class BMPStringEncoder implements ElementEncoder
{	
	private byte[] m_ValueBytes;

    private BMPStringEncoder(byte[] valueBytes)
    {
        m_ValueBytes = valueBytes;
    }

    public static BMPStringEncoder create(String value)
    {
        byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_16BE);
        return new BMPStringEncoder(valueBytes);
    }

    public int estimateTLV()
    {
        return 1 + LengthEncoder.estimate(m_ValueBytes.length) + m_ValueBytes.length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        binStack.stack(m_ValueBytes);
        int L_length = LengthEncoder.encode(m_ValueBytes.length, binStack);
        binStack.stack(UniversalTag.BMPString);
        return 1 + L_length + m_ValueBytes.length;
    }
}
