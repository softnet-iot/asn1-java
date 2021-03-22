package softnet.asn;

public class AsnEncoding
{
	public final byte[] buffer;
	public final int offset;
	public AsnEncoding(byte[] _buffer, int _offset)
	{
		buffer = _buffer;
		offset = _offset;
	}
}
