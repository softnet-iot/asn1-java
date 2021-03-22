package softnet.asn;

class Real64Decoder
{
	public static double decode(byte[] buffer, int offset, int V_length, boolean checkForUnderflow) throws FormatAsnException, UnderflowAsnException, OverflowAsnException
	{
        if (V_length == 0)
            return 0.0;

        int infoByte = buffer[offset];
        offset++;

        if ((infoByte & 0x80) == 0)
            throw new FormatAsnException("The ASN1 Codec supports only binary format with base 2 for floating-point number encoding.");

        if ((infoByte & 0x30) != 0)
            throw new FormatAsnException("The ASN1 Codec supports only binary format with base 2 for floating-point number encoding.");

        if ((infoByte & 0x0C) != 0)
            throw new FormatAsnException();

        long exponent;
        int exponent_flags = infoByte & 0x03;
        int exponent_bytes_number;

        if (exponent_flags == 0)
        {
            if (V_length < 3)
                throw new FormatAsnException();

            exponent = buffer[offset];
            offset++;
            exponent_bytes_number = 1;
        }
        else if (exponent_flags == 1)
        {
            if (V_length < 4)
                throw new FormatAsnException();

            int byte_a = buffer[offset] & 0xFF;
            int byte_b = buffer[offset + 1] & 0xFF;
            offset += 2;
            exponent_bytes_number = 2;

            if (byte_a >= 128)
            {
                if (byte_a == 255 && byte_b >= 128)
                    throw new FormatAsnException();

                exponent = /* 0xFFFF0000 */ -65536 | (byte_a << 8) | byte_b;
            }
            else
            {
                if (byte_a == 0 && byte_b <= 127)
                    throw new FormatAsnException();

                exponent = (byte_a << 8) | byte_b;
            }
        }
        else if (exponent_flags == 2)
        {
            if (V_length < 5)
                throw new FormatAsnException();

            int byte_a = buffer[offset] & 0xFF;
            int byte_b = buffer[offset + 1] & 0xFF;

            if (byte_a >= 128)
            {
                if (byte_a == 255 && byte_b >= 128)
                    throw new FormatAsnException();

                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

                return 0.0;
            }
            else
            {
                if (byte_a == 0 && byte_b <= 127)
                    throw new FormatAsnException();

                throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");
            }
        }
        else // exponent_flags == 3
        {
            if (V_length < 7)
                throw new FormatAsnException();

            if (buffer[offset] < 4)
                throw new FormatAsnException();

            int byte_a = buffer[offset + 1] & 0xFF;
            int byte_b = buffer[offset + 2] & 0xFF;

            if (byte_a >= 128)
            {
                if (byte_a == 255 && byte_b >= 128)
                    throw new FormatAsnException();

                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

                return 0.0;
            }
            else
            {
                if (byte_a == 0 && byte_b <= 127)
                    throw new FormatAsnException();

                throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");
            }
        }

        int mantissa_bytes_number = V_length - (1 + exponent_bytes_number);

        int lsb_index = offset + mantissa_bytes_number - 1;
        if ((buffer[lsb_index] & 0x01) == 0)
            throw new FormatAsnException();

        int ms_byte = buffer[offset] & 0xFF;
        if (ms_byte == 0)
            throw new FormatAsnException();

        int mantissa_width = (mantissa_bytes_number - 1) * 8;
        if (ms_byte >= 128) mantissa_width += 8;
        else if (ms_byte >= 64) mantissa_width += 7;
        else if (ms_byte >= 32) mantissa_width += 6;
        else if (ms_byte >= 16) mantissa_width += 5;
        else if (ms_byte >= 8) mantissa_width += 4;
        else if (ms_byte >= 4) mantissa_width += 3;
        else if (ms_byte >= 2) mantissa_width += 2;
        else mantissa_width += 1;

        boolean isPositive = (infoByte & 0x40) == 0 ? true : false;

        if (mantissa_width <= 53)
        {
            exponent = exponent + mantissa_width + 1022;

            if (exponent >= 1)
            {
                if (exponent > 2046)
                    throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");

                long mantissa = 0;
                for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                {
                    mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                }

                int left_shift = 53 - mantissa_width;
                mantissa = (mantissa << left_shift) & 0x000FFFFFFFFFFFFFL;

                long doubleBits = exponent << 52;

                if (isPositive == false)
                    doubleBits = doubleBits | -9223372036854775808L; /* 0x8000000000000000 */

                doubleBits = doubleBits | mantissa;

                return Double.longBitsToDouble(doubleBits);
            }
            else // exponent <= 0
            {
                int required_precision = mantissa_width - (int)exponent; // exponent < 0 ==> mantissa right shift
                int mantissa_shift = 52 - required_precision;

                long mantissa = 0;
                for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                {
                    mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                }

                if (mantissa_shift > 0)
                {
                    mantissa = mantissa << mantissa_shift;
                }
                else if (mantissa_shift < 0)
                {
                    if (checkForUnderflow)
                        throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

                    mantissa_shift = -mantissa_shift;

                    if (mantissa_shift >= mantissa_width)
                        return 0.0;

                    mantissa = mantissa >> mantissa_shift;
                }

                if (isPositive == false)
                    mantissa = mantissa | -9223372036854775808L; /* 0x8000000000000000 */

                return Double.longBitsToDouble(mantissa);
            }
        }
        else
        {
            exponent = exponent + mantissa_width + 1022;

            if (exponent >= 1)
            {
                if (exponent > 2046)
                    throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");

                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

                long mantissa = 0;

                if (mantissa_width <= 56)
                {
                    lsb_index = offset + 6;
                    for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                    {
                        mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                    }

                    // mantissa_width > 53
                    int right_shift = mantissa_width - 53;
                    mantissa = mantissa >> right_shift;
                    mantissa = mantissa & 0x000FFFFFFFFFFFFFL;
                }
                else
                {
                    lsb_index = offset + 7;
                    for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                    {
                        mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                    }

                    int right_shift = (mantissa_width % 8) + 3;
                    if (right_shift == 3)
                        right_shift = 11;
                    mantissa = mantissa >> right_shift;
                    mantissa = mantissa & 0x000FFFFFFFFFFFFFL;
                }

                long doubleBits = exponent << 52;

                if (isPositive == false)
                    doubleBits = doubleBits | -9223372036854775808L; /* 0x8000000000000000 */

                doubleBits = doubleBits | mantissa;

                return Double.longBitsToDouble(doubleBits);
            }
            else
            {
                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

                if (exponent <= -52)
                    return 0.0;

                long mantissa = 0;

                if (mantissa_width <= 56)
                {
                    lsb_index = offset + 6;
                    for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                    {
                        mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                    }

                    // mantissa_width > 53
                    int right_shift = mantissa_width - 52;
                    mantissa = mantissa >> right_shift;
                    mantissa = mantissa & 0x000FFFFFFFFFFFFFL;
                }
                else
                {
                    lsb_index = offset + 7;
                    for (int i = lsb_index, j = 0; i >= offset; i--, j++)
                    {
                        mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
                    }

                    int right_shift = (mantissa_width % 8) + 4;
                    if (right_shift == 4)
                        right_shift = 12;
                    mantissa = mantissa >> right_shift;
                    mantissa = mantissa & 0x000FFFFFFFFFFFFFL;
                }

                mantissa = mantissa >> -((int)exponent);

                if (isPositive == false)
                    mantissa = mantissa | -9223372036854775808L; /* 0x8000000000000000 */

                return Double.longBitsToDouble(mantissa);
            }
        }
	}		
}
