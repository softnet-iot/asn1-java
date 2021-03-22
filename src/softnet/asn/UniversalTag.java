package softnet.asn;

class UniversalTag
{
	final static byte Boolean = 1;
	final static byte Integer = 2;
	final static byte OctetString = 4;
	final static byte Real = 9;
	final static byte UTF8String = 12;           
	final static byte Sequence = 16;
	final static byte PrintableString = 19;	// A-Z a-z 0-9 space ' ( ) + , - . / : = ?
	final static byte IA5String = 22;		// ASCII 0-127
	final static byte GeneralizedTime = 24;
	final static byte BMPString = 30;		// Big-Endian UTF-16
}
