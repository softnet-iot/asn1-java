package softnet.asn;

import java.util.GregorianCalendar;
import java.util.UUID;

class SequenceDecoderImp implements SequenceDecoder
{
	private static int C_Mask_Class = 0xC0;
	private static int C_Flag_ContextSpecificTag = 0x80;
	private static int C_Flag_UniversalTag = 0;	
	private static int C_Flag_Container = 0x20;
	private static int C_Mask_Tag = 0x1F;

    private byte[] m_buffer = null;        
    private int m_offset = 0;
    private int m_data_begin = 0;
    private int m_data_end = 0;
        
    public SequenceDecoderImp(byte[] buffer, int offset, int length)
    {
    	m_buffer = buffer;
        m_offset = offset;
        m_data_begin = offset;
        m_data_end = offset + length;	
    }
        
    private int m_count = -1;
    public int count() throws FormatAsnException
    {
        if (m_count == -1)
        {
            m_count = 0;
            int offset = m_data_begin;
            while (offset < m_data_end)
            { 
                PairInt32  lengthPair = LengthDecoder.decode(m_buffer, offset + 1);
                offset += 1 + lengthPair.second + lengthPair.first;
                m_count++;
            }

            if(offset != m_data_end)
                throw new FormatAsnException();
        }
        return m_count;
    }
    
	public boolean hasNext()
	{
		if (m_offset < m_data_end)
    		return true;
		return false;
	}
    
    public boolean exists(int tag)
    {
        if (m_offset == m_data_end)
    		return false;

        int T = m_buffer[m_offset];
        if ((T & C_Mask_Class) != C_Flag_ContextSpecificTag)
    		return false;
    	
    	if((T & C_Mask_Tag) != tag)
    		return false;
    	
    	return true;
    }

    public void end() throws EndNotReachedAsnException
    {
    	if (m_offset < m_data_end)
            throw new EndNotReachedAsnException();
    }

    public SequenceDecoder Sequence() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
    	if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) == 0 || (T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            SequenceDecoderImp decoder = new SequenceDecoderImp(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return decoder;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if (V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) == 0 || (T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if(m_offset + V_Length != end_position)
                throw new FormatAsnException();

            SequenceDecoderImp decoder = new SequenceDecoderImp(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return decoder;
        }
        
        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");    	
    }
    
    public int Int32() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            int value = Int32Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            int value = Int32Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }

    public int Int32(int minValue, int maxValue) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, RestrictionAsnException
    {
    	int value = Int32();
        if (value < minValue || maxValue < value)
            throw new RestrictionAsnException(String.format("The value of the input integer must be in the range [%d, %d], while the actual value is %d.", minValue, maxValue, value));
        return value;
    }
    
    public long Int64() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            long value = Int64Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            long value = Int64Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public boolean Boolean() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Boolean)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();
            if(V_Length != 1)
            	throw new FormatAsnException();
            
            int value = m_buffer[m_offset] & 0xFF;
            m_offset++;
                        
            if (value == 255)
            {
                return true;
            }
            else if (value == 0)
            {
                return false;
            }
            
            throw new FormatAsnException();
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length != 3)
                throw new FormatAsnException();

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Boolean)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (V_Length != 1)
                throw new FormatAsnException();

            int value = m_buffer[m_offset] & 0xFF;
            m_offset++;
                        
            if (value == 255)
            {
                return true;
            }
            else if (value == 0)
            {
                return false;
            }
            
            throw new FormatAsnException();
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public float Real32() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real32(false);
    }
    
    public float Real32(boolean checkForUnderflow) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            float value = Real32Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if (V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            float value = Real32Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1 Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public double Real64() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real64(false);
    }
    
    public double Real64(boolean checkForUnderflow) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            double value = Real64Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if (V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            double value = Real64Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public String UTF8String() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.UTF8String)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            String value = UTF8StringDecoder.Decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.UTF8String)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            String value = UTF8StringDecoder.Decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public String UTF8String(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = UTF8String();
        if (value.length() != requiredLength)
            throw new RestrictionAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }
    
    public String UTF8String(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = UTF8String();
        if (value.length() < minLength || maxLength < value.length())
            throw new RestrictionAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }
    
    public String BMPString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.BMPString)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            String value = BMPStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.BMPString)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            String value = BMPStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public String BMPString(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = BMPString();
        if (value.length() != requiredLength)
            throw new RestrictionAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }
    
    public String BMPString(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = BMPString();
        if (value.length() < minLength || maxLength < value.length())
            throw new RestrictionAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }
    
    public String IA5String() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.IA5String)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            String value = IA5StringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.IA5String)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            String value = IA5StringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }

    public String IA5String(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = IA5String();
    	if (value.length() != requiredLength)
            throw new RestrictionAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String IA5String(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	String value = IA5String();
    	if (value.length() < minLength || maxLength < value.length())
            throw new RestrictionAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;    
    }

    public String PrintableString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.PrintableString)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            String value = PrintableStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.PrintableString)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            String value = PrintableStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }

    public GregorianCalendar GndTimeToGC() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
        if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.GeneralizedTime)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            GregorianCalendar value = GndTimeGCDecoder.decode(m_buffer, m_offset, V_Length); 
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.GeneralizedTime)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            GregorianCalendar value = GndTimeGCDecoder.decode(m_buffer, m_offset, V_Length); 
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1 codec does not support tag classes 'Application' and 'Private'.");
    }
    
    public byte[] OctetString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException
    {
    	if (m_offset == m_data_end)
            throw new EndOfSequenceAsnException();

        int T = m_buffer[m_offset];

        int tagClass = T & C_Mask_Class;
        if (tagClass == C_Flag_UniversalTag)
        {
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.OctetString)
                throw new TypeMismatchAsnException();

            int V_Length = DecodeLength();

            byte[] value = new byte[V_Length];
            System.arraycopy(m_buffer, m_offset, value, 0, V_Length);
            m_offset += V_Length;

            return value;
        }
        else if (tagClass == C_Flag_ContextSpecificTag)
        {
            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            int V_Length = DecodeLength();
            if(V_Length < 2)
                throw new FormatAsnException();

            int end_position = m_offset + V_Length;

            T = m_buffer[m_offset];
            if ((T & C_Flag_Container) != 0 || (T & C_Mask_Tag) != UniversalTag.OctetString)
                throw new TypeMismatchAsnException();

            V_Length = DecodeLength();

            if (m_offset + V_Length != end_position)
                throw new FormatAsnException();

            byte[] value = new byte[V_Length];
            System.arraycopy(m_buffer, m_offset, value, 0, V_Length);
            m_offset += V_Length;

            return value;
        }

        throw new FormatAsnException("The ASN1Codec does not support tag classes 'Application' and 'Private'.");
    }
        
    public byte[] OctetString(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	byte[] value = OctetString();
        if (value.length != requiredLength)
            throw new RestrictionAsnException(String.format("The length of the input octet string must be %d, while the actual length is %d.", requiredLength, value.length));
        return value;   
    }
    
    public byte[] OctetString(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	byte[] value = OctetString();
        if (value.length < minLength || maxLength < value.length)
            throw new RestrictionAsnException(String.format("The length of the input octet string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length));
        return value;   
    }
    
    public UUID OctetStringToUUID() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException
    {
    	byte[] value = OctetString();
        if (value.length != 16)
            throw new RestrictionAsnException(String.format("The length of the input octet string must be 16, while the actual length is %d.", value.length));
        java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(value);            
		return new UUID(nioBuffer.getLong(), nioBuffer.getLong());
    }
        
    private int DecodeLength() throws FormatAsnException
    {
    	m_offset++;
    	try
    	{
	        int L1_Byte = m_buffer[m_offset] & 0xFF;
	        m_offset++;
	
	        if (L1_Byte <= 127)
	        {
	        	if (m_offset + L1_Byte > m_data_end)
                    throw new FormatAsnException();
                return L1_Byte;
	        }
		        
	        if (L1_Byte == 128)
	        	throw new FormatAsnException("The ASN1 codec does not support the indefinite length form.");
	        
	        int bytes = L1_Byte & 0x7F;
	
	        if(bytes == 1)
	        {	        	
	        	int length = m_buffer[m_offset] & 0xFF;
                m_offset++;

                if (m_offset + length > m_data_end)
                    throw new FormatAsnException();

                return length;
	        }
	    	else if(bytes == 2)
	    	{        		
	    		int b1 = m_buffer[m_offset] & 0xFF;
                int b0 = m_buffer[m_offset + 1] & 0xFF;
                m_offset += 2;

                int length = (b1 << 8) | b0;

                if (m_offset + length > m_data_end)
                    throw new FormatAsnException();
                
                return length;
	    	}
	    	else if(bytes == 3)
	    	{
                int b2 = m_buffer[m_offset] & 0xFF;
                int b1 = m_buffer[m_offset + 1] & 0xFF;
                int b0 = m_buffer[m_offset + 2] & 0xFF;
                m_offset += 3;

                int length = (b2 << 16) | (b1 << 8) | b0;

                if (m_offset + length > m_data_end)
                    throw new FormatAsnException();

                return length;
	    	}
	    	else if(bytes == 4)
	    	{
	    		int b3 = m_buffer[m_offset] & 0xFF;
                if (b3 >= 128)
                    throw new FormatAsnException("The ASN1 codec does not support the length of content more than 2GB.");

                int b2 = m_buffer[m_offset + 1] & 0xFF;
                int b1 = m_buffer[m_offset + 2] & 0xFF;
                int b0 = m_buffer[m_offset + 3] & 0xFF;
                m_offset += 4;

                int length = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;

                if (m_offset + length > m_data_end)
                    throw new FormatAsnException();

                return length;
	    	}
	 
            throw new FormatAsnException("The ASN1 codec does not support the length of content more than 2GB.");
    	}
    	catch (ArrayIndexOutOfBoundsException e)
    	{
            throw new FormatAsnException("The size of the input buffer is not enough to contain all the Asn1 data.");
    	}
    }
}











