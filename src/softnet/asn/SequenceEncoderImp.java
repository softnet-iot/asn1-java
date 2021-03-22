package softnet.asn;

import java.util.ArrayList;
import java.util.GregorianCalendar;

class SequenceEncoderImp implements SequenceEncoder, ElementEncoder
{
	public SequenceEncoderImp()
	{
		m_childNodes = new ArrayList<ElementEncoder>();
	}
	
	private static byte C_Flag_Container = 0x20;
	private ArrayList<ElementEncoder> m_childNodes;
	
	public int count()
	{
		return m_childNodes.size();
	}
	
	public int estimateTLV()
	{
		int V_Length = 0;
        for(ElementEncoder encoder: m_childNodes)
        {
            V_Length += encoder.estimateTLV();
        }
        return 1 + LengthEncoder.estimate(V_Length) + V_Length;		
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		int v_length = 0;
        for (int i = m_childNodes.size() - 1; i >= 0; i--)
        {
        	v_length += m_childNodes.get(i).encodeTLV(binStack);
        }        
        int l_length = LengthEncoder.encode(v_length, binStack);
        binStack.stack((byte)(C_Flag_Container | UniversalTag.Sequence));

        return 1 + l_length + v_length;
	}
		
	public SequenceEncoder Sequence()
	{
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		m_childNodes.add(encoder);
		return encoder;
	}
		
	public void Int32(int value)
	{
		m_childNodes.add(new Int32Encoder(value));
	}
	
	public void Int64(long value)
	{
		m_childNodes.add(new Int64Encoder(value));
	}

	public void Boolean(boolean value)
	{
		m_childNodes.add(new BooleanEncoder(value));
	}

	public void Real32(float value) 
	{
		m_childNodes.add(Real32Encoder.create(value));
	}

	public void Real64(double value) 
	{
		m_childNodes.add(Real64Encoder.create(value));
	}

	public void UTF8String(String value)
	{
		m_childNodes.add(UTF8StringEncoder.create(value));
	}
	
	public void BMPString(String value)
	{
		m_childNodes.add(BMPStringEncoder.create(value));		
	}

	public void IA5String(String value)
	{
		m_childNodes.add(IA5StringEncoder.create(value));	
	}
	
	public void PrintableString(String value)
	{
		m_childNodes.add(PrintableStringEncoder.create(value));	
	}

	public void GndTime(java.util.GregorianCalendar value) 
	{
		if(value == null)
			throw new NullPointerException("The parameter 'value' must not be null.");
		
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		GndTimeGCEncoder encoder = new GndTimeGCEncoder(valueClone);
		m_childNodes.add(encoder);
	}

	public void OctetString(byte[] buffer)
	{
		m_childNodes.add(new OctetStringEncoder(buffer, 0, buffer.length));
	}
	
	public void OctetString(byte[] buffer, int offset, int length)
	{
		m_childNodes.add(new OctetStringEncoder(buffer, offset, length));
	}
	
	public void OctetString(java.util.UUID value)
	{
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		m_childNodes.add(new OctetStringEncoder(nioBuffer.array(), 0, 16));
	}

	public SequenceEncoder TaggedSequence(int tag)
	{
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);		
		return encoder;
	}
	
	public void TaggedInt32(int tag, int value)
	{
		Int32Encoder encoder = new Int32Encoder(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}

	public void TaggedInt64(int tag, long value)
	{
		Int64Encoder encoder = new Int64Encoder(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}

	public void TaggedBoolean(int tag, boolean value)
	{
		BooleanEncoder encoder = new BooleanEncoder(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
	
	public void TaggedReal32(int tag, float value) 
	{
		Real32Encoder encoder = Real32Encoder.create(value);
        m_childNodes.add(new TaggedElementEncoder(tag, encoder));
	}
	
	public void TaggedReal64(int tag, double value) 
	{
		Real64Encoder encoder = Real64Encoder.create(value);
        m_childNodes.add(new TaggedElementEncoder(tag, encoder));
	}

	public void TaggedUTF8String(int tag, String value)
	{
		UTF8StringEncoder encoder = UTF8StringEncoder.create(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
	
	public void TaggedBMPString(int tag, String value)
	{
		BMPStringEncoder encoder = BMPStringEncoder.create(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);	
	}

	public void TaggedIA5String(int tag, String value)
	{
		IA5StringEncoder encoder = IA5StringEncoder.create(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
	
	public void TaggedPrintableString(int tag, String value)
	{
		PrintableStringEncoder encoder = PrintableStringEncoder.create(value);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}		
	
	public void TaggedGndTime(int tag, java.util.GregorianCalendar value)
	{
		if(value == null)
			throw new NullPointerException("The parameter 'value' of type 'GregorianCalendar' must not be null.");

		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		GndTimeGCEncoder encoder = new GndTimeGCEncoder(valueClone);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}

	public void TaggedOctetString(int tag, byte[] buffer)
	{
		OctetStringEncoder encoder = new OctetStringEncoder(buffer, 0, buffer.length);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
	
	public void TaggedOctetString(int tag, byte[] buffer, int offset, int length)
	{
		OctetStringEncoder encoder = new OctetStringEncoder(buffer, offset, length);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
	
	public void TaggedOctetString(int tag, java.util.UUID value)
	{
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		OctetStringEncoder encoder = new OctetStringEncoder(nioBuffer.array(), 0, 16);
		TaggedElementEncoder teEncoder = new TaggedElementEncoder(tag, encoder);
		m_childNodes.add(teEncoder);
	}
}
