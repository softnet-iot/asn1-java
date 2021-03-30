package softnet.asn;

import java.lang.ArrayIndexOutOfBoundsException;

public class ASNDecoder 
{
	private static int C_Mask_Class = 0xC0;        
	private static int C_Flag_UniversalTag = 0;
	private static int C_Flag_Container = 0x20;
	private static int C_Mask_Tag = 0x1F;

    private ASNDecoder() { }

    public static SequenceDecoder create(byte[] buffer) throws FormatAsnException
    {
        return create(buffer, 0); 
    }
    
    public static SequenceDecoder create(byte[] buffer, int offset) throws FormatAsnException
    {
        try
        {
            int T = buffer[offset];

            if ((T & C_Flag_Container) == 0)
                throw new FormatAsnException();

            if ((T & C_Mask_Class) != C_Flag_UniversalTag)
                throw new FormatAsnException();

            if ((T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new FormatAsnException();

            offset++;
            PairInt32 lengthPair = LengthDecoder.decode(buffer, offset);
            offset += lengthPair.second;

            if (offset + lengthPair.first > buffer.length)
                throw new FormatAsnException("The input buffer size is not enough to contain all the Asn1 data.");

            return new SequenceDecoderImp(buffer, offset, lengthPair.first); 
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new FormatAsnException("The input buffer size is not enough to contain all the Asn1 data.");
        }
    }
}
