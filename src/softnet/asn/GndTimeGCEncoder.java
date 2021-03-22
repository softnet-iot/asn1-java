package softnet.asn;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

class GndTimeGCEncoder implements ElementEncoder
{
	private GregorianCalendar m_Value;
	
	public GndTimeGCEncoder(GregorianCalendar value)
	{
		m_Value = value;
	}
	
	public int estimateTLV()
	{
		return 21;		
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		int LV_length = encodeLV(binStack);
		binStack.stack(UniversalTag.GeneralizedTime);
        return 1 + LV_length;
	}
	
	private int encodeLV(BinaryStack binStack)
	{
		m_Value.setTimeZone(TimeZone.getTimeZone("GMT"));
				
		int year = m_Value.get(Calendar.YEAR);
		int month = m_Value.get(Calendar.MONTH);
		int day = m_Value.get(Calendar.DAY_OF_MONTH);
		int hour = m_Value.get(Calendar.HOUR_OF_DAY);
		int minute = m_Value.get(Calendar.MINUTE);
		int second = m_Value.get(Calendar.SECOND);
		int millisecond = m_Value.get(Calendar.MILLISECOND);
		
		byte[] bytes = new byte[19];
		
		int digit = year / 1000;
		bytes[0] = (byte)(48 + digit);
		year = year - digit * 1000;
		digit = year / 100;
		bytes[1] = (byte)(48 + digit);
		year = year - digit * 100;
		digit = year / 10;
		bytes[2] = (byte)(48 + digit);
		digit = year % 10;
		bytes[3] = (byte)(48 + digit);
		
		digit = month + 1;
		bytes[4] = (byte)(48 + digit / 10);
		bytes[5] = (byte)(48 + digit % 10);
		
		bytes[6] = (byte)(48 + day / 10);
		bytes[7] = (byte)(48 + day % 10);

		bytes[8] = (byte)(48 + hour / 10);
		bytes[9] = (byte)(48 + hour % 10);

		bytes[10] = (byte)(48 + minute / 10);
		bytes[11] = (byte)(48 + minute % 10);

		bytes[12] = (byte)(48 + second / 10);
		bytes[13] = (byte)(48 + second % 10);

		int offset = 14;
		if(millisecond > 0)
		{
			int d1 = millisecond / 100;
			millisecond = millisecond - d1 * 100;
			int d2 = millisecond / 10;
			int d3 = millisecond % 10;

			bytes[offset] = '.';
			offset++;
			
			if(d3 != 0)
			{
				bytes[offset] = (byte)(48 + d1);
				offset++;
				bytes[offset] = (byte)(48 + d2);
				offset++;
				bytes[offset] = (byte)(48 + d3);
				offset++;
			}
			else if(d2 != 0)
			{
				bytes[offset] = (byte)(48 + d1);
				offset++;
				bytes[offset] = (byte)(48 + d2);
				offset++;				
			}
			else
			{
				bytes[offset] = (byte)(48 + d1);
				offset++;				
			}
		}
		
		bytes[offset] = 'Z';
		
		int length = offset + 1;
		
		binStack.stack(bytes, 0, length);
		binStack.stack((byte)length);
				
		return length + 1;
	}
}
