package softnet.asn;

class Real32Encoder implements ElementEncoder
{
    private Real32Encoder() { }

    private void init(float value)
    {
        m_floatBits = Float.floatToRawIntBits(value);
        if (m_floatBits != 0)
        {
            m_mantissa_insignificant_bits_number = CountInsignificantBits();
        }
    }

    public static Real32Encoder create(float value)
    {
    	if(Float.isFinite(value) == false)
			 throw new IllegalArgumentException("The argument 'value' must not be NaN or Infinity");

    	Real32Encoder encoder = new Real32Encoder();
        encoder.init(value);
        return encoder;
    }

    private int m_floatBits = 0;
    private int m_mantissa_insignificant_bits_number;

    public int estimateTLV()
    {
        if (m_floatBits != 0)
        {
            return 8 - m_mantissa_insignificant_bits_number / 8;
        }
        else
        {
            return 2;
        }
    }

    public int encodeTLV(BinaryStack binStack)
    {
        int LV_length = encodeLV(binStack);
        binStack.stack(UniversalTag.Real);
        return 1 + LV_length;
    }

    private int encodeLV(BinaryStack binStack)
    {
        if (m_floatBits == 0)
        {
            binStack.stack((byte)0);
            return 1;
        }

        int byte_a = (m_floatBits >> 24) & 0x0000007F;
        int byte_b = (m_floatBits >> 16) & 0x00000080;
        int exponent = (byte_a << 1) + (byte_b >> 7);

        int mantissa;
        if (exponent >= 1) // normalized
        {
            exponent -= 127;
            mantissa = (m_floatBits & 0x007FFFFF) | 0x00800000;
        }
        else
        {
            exponent = -126;
            mantissa = m_floatBits & 0x007FFFFF;
        }

        exponent -= (23 - m_mantissa_insignificant_bits_number);
        mantissa = mantissa >> m_mantissa_insignificant_bits_number;

        int mantissa_bytes_number = EncodeMantissa(mantissa, binStack);
        int exponent_bytes_number = EncodeExponent(exponent, binStack);
        EncodeInfoByte(exponent_bytes_number, binStack);            

        int V_length = 1 + exponent_bytes_number + mantissa_bytes_number;
        binStack.stack((byte)V_length);

        return 1 + V_length;
    }

    private int EncodeMantissa(int mantissa, BinaryStack binStack)
    {
        int b0 = mantissa & 0x000000ff;
        int b1 = (mantissa & 0x0000ff00) >> 8;
        int b2 = (mantissa & 0x00ff0000) >> 16;

        if (b2 == 0)
        {
            if (b1 == 0)
            {
                binStack.stack(b0);
                return 1;
            }
            else
            {
                binStack.stack(b0);
                binStack.stack(b1);
                return 2;
            }
        }
        else
        {
            binStack.stack(b0);
            binStack.stack(b1);
            binStack.stack(b2);
            return 3;
        }
    }

    private int EncodeExponent(int exponent, BinaryStack binStack)
    {
        int b0 = exponent & 0x000000ff;
        int b1 = (exponent & 0x0000ff00) >> 8;

        if (b1 == 255)
        {
            if (b0 >= 128)
            {
                binStack.stack(b0);
                return 1;
            }
            else
            {
                binStack.stack(b0);
                binStack.stack(255);
                return 2;
            }
        }
        else if (b1 == 0)
        {
            if (b0 <= 127)
            {
                binStack.stack(b0);
                return 1;
            }
            else
            {
                binStack.stack(b0);
                binStack.stack(0);
                return 2;
            }
        }
        else
        {
            binStack.stack(b0);
            binStack.stack(b1);
            return 2;
        }
    }

    private void EncodeInfoByte(int exponent_bytes_number, BinaryStack binStack)
    {
        boolean isPositive = m_floatBits >= 0 ? true : false;
        if (isPositive)
        {
            if (exponent_bytes_number == 1)
            {
                binStack.stack(0x80);
            }
            else // exponent_bytes_number == 2
            {
                binStack.stack(0x81);
            }
        }
        else
        {
            if (exponent_bytes_number == 1)
            {
                binStack.stack(0xC0);
            }
            else // exponent_bytes_number == 2
            {
                binStack.stack(0xC1);
            }
        }
    }

    private int CountInsignificantBits()
    {
        for (int i = 0; i <= 2; i++)
        {
            int b = ((m_floatBits >> (i * 8)) & 0xFF);
            if (b > 0)
            {
                if ((b & 0x01) != 0) return i * 8;
                if ((b & 0x02) != 0) return i * 8 + 1;
                if ((b & 0x04) != 0) return i * 8 + 2;
                if ((b & 0x08) != 0) return i * 8 + 3;
                if ((b & 0x10) != 0) return i * 8 + 4;
                if ((b & 0x20) != 0) return i * 8 + 5;
                if ((b & 0x40) != 0) return i * 8 + 6;
                return i * 8 + 7;
            }
        }
        return 23;
    }
}
















