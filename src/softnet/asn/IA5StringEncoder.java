package softnet.asn;

import java.util.regex.Pattern;

class IA5StringEncoder implements ElementEncoder
{
	private byte[] m_ValueBytes;

    private IA5StringEncoder(byte[] valueBytes)
    {
        m_ValueBytes = valueBytes;
    }

    public static IA5StringEncoder create(String value)
    {
    	if (Pattern.matches("^[\\u0000-\\u007F]*$", value) == false)
            throw new IllegalArgumentException(String.format("The string '%s' contains characters that are not allowed in 'ASN1 IA5String'.", value));

    	byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.US_ASCII);    	
        return new IA5StringEncoder(valueBytes);
    }

    public int estimateTLV()
    {
        return 1 + LengthEncoder.estimate(m_ValueBytes.length) + m_ValueBytes.length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        binStack.stack(m_ValueBytes);
        int L_length = LengthEncoder.encode(m_ValueBytes.length, binStack);
        binStack.stack(UniversalTag.IA5String);
        return 1 + L_length + m_ValueBytes.length;
    }
}