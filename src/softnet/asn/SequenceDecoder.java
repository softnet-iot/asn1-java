package softnet.asn;

public interface SequenceDecoder
{
	int count() throws FormatAsnException;
	boolean hasNext();
	boolean exists(int tag);
	void end() throws EndNotReachedAsnException;

	SequenceDecoder Sequence() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
	int Int32() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException;
	int Int32(int minValue, int maxValue) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, RestrictionAsnException;
    long Int64() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException;
    boolean Boolean() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    float Real32() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, UnderflowAsnException;
    float Real32(boolean checkForUnderflow) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, UnderflowAsnException;
    double Real64() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, UnderflowAsnException;
    double Real64(boolean checkForUnderflow) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, OverflowAsnException, UnderflowAsnException;
    String UTF8String() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    String UTF8String(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String UTF8String(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String BMPString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    String BMPString(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String BMPString(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String IA5String() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    String IA5String(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String IA5String(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    String PrintableString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    java.util.GregorianCalendar GndTimeToGC() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    byte[] OctetString() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException;
    byte[] OctetString(int requiredLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    byte[] OctetString(int minLength, int maxLength) throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
    java.util.UUID OctetStringToUUID() throws FormatAsnException, EndOfSequenceAsnException, TypeMismatchAsnException, RestrictionAsnException;
}
