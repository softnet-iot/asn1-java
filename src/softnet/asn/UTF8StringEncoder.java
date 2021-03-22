package softnet.asn;

class UTF8StringEncoder implements ElementEncoder
{
	private byte[] m_ValueBytes;

    private UTF8StringEncoder(byte[] valueBytes)
    {
        m_ValueBytes = valueBytes;
    }

    public static UTF8StringEncoder create(String value)
    {
        byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return new UTF8StringEncoder(valueBytes);
    }

    public int estimateTLV()
    {
        return 1 + LengthEncoder.estimate(m_ValueBytes.length) + m_ValueBytes.length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        binStack.stack(m_ValueBytes);
        int L_length = LengthEncoder.encode(m_ValueBytes.length, binStack);
        binStack.stack(UniversalTag.UTF8String);
        return 1 + L_length + m_ValueBytes.length;
    }
}
