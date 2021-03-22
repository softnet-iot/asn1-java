package softnet.asn;

import java.util.regex.Pattern;

class PrintableStringEncoder implements ElementEncoder
{
	private byte[] m_ValueBytes;

    private PrintableStringEncoder(byte[] valueBytes)
    {
        m_ValueBytes = valueBytes;
    }

    public static PrintableStringEncoder create(String value)
    {
    	if (Pattern.matches("^[\\u0020-\\u007F]+$", value) == false)
    		throw new IllegalArgumentException(String.format("The string '%s' contains characters that are not allowed in 'ASN1 PrintableString'.", value));
    		
		if (Pattern.matches("^[A-Za-z0-9\\s'()+,\\-./:=?]*$", value) == false)    	
    		throw new IllegalArgumentException(String.format("The string '%s' contains characters that are not allowed in 'ASN1 PrintableString'.", value));

		byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return new PrintableStringEncoder(valueBytes);
    }

    public int estimateTLV()
    {
        return 1 + LengthEncoder.estimate(m_ValueBytes.length) + m_ValueBytes.length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        binStack.stack(m_ValueBytes);
        int L_length = LengthEncoder.encode(m_ValueBytes.length, binStack);
        binStack.stack(UniversalTag.PrintableString);
        return 1 + L_length + m_ValueBytes.length;
    }
}
