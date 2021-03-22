package softnet.asn;

public interface SequenceEncoder
{
	int count();
	
	SequenceEncoder Sequence();
	void Int32(int value);
	void Int64(long value);
	void Boolean(boolean value);
	void Real32(float value);
	void Real64(double value);
	void UTF8String(String value);
	void BMPString(String value);
	void IA5String(String value);
	void PrintableString(String value);
	void GndTime(java.util.GregorianCalendar value);
	void OctetString(byte[] buffer);
	void OctetString(byte[] buffer, int offset, int length);
	void OctetString(java.util.UUID value);
	
	SequenceEncoder TaggedSequence(int tag);
	void TaggedInt32(int tag, int value);
	void TaggedInt64(int tag, long value);
	void TaggedBoolean(int tag, boolean value);
	void TaggedReal32(int tag, float value);
	void TaggedReal64(int tag, double value);
	void TaggedUTF8String(int tag, String value);
	void TaggedBMPString(int tag, String value);
	void TaggedIA5String(int tag, String value);
	void TaggedPrintableString(int tag, String value);
	void TaggedGndTime(int tag, java.util.GregorianCalendar value);
	void TaggedOctetString(int tag, byte[] buffer);
	void TaggedOctetString(int tag, byte[] buffer, int offset, int length);
	void TaggedOctetString(int tag, java.util.UUID uuid);
}
