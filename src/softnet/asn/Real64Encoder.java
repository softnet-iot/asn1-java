package softnet.asn;

class Real64Encoder implements ElementEncoder
{
    private Real64Encoder() {}

    private void init(double value)
    {
        m_doubleBits = Double.doubleToRawLongBits(value);
        if (m_doubleBits != 0)
        {
            m_insignificant_bits_number = CountInsignificantBits();
        }
    }

    public static Real64Encoder create(double value)
    {
    	if(Double.isFinite(value) == false)
			 throw new IllegalArgumentException("The argument 'value' must not be NaN or Infinity");

    	Real64Encoder encoder = new Real64Encoder();
        encoder.init(value);
        return encoder;
    }

    private long m_doubleBits = 0;
    private int m_insignificant_bits_number;

    public int estimateTLV()
    {
        if (m_doubleBits != 0)
        {
            return 12 - m_insignificant_bits_number / 8;
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
        if (m_doubleBits == 0)
        {
            binStack.stack((byte)0);
            return 1;
        }

        int byte_a = ((int)(m_doubleBits >> 56)) & 0x0000007F;
        int byte_b = ((int)(m_doubleBits >> 48)) & 0x000000F0;
        int exponent = ((byte_a << 4) | (byte_b >> 4));

        long mantissa;
        if (exponent >= 1) // normalized
        {
            exponent -= 1023;
            mantissa = (m_doubleBits & 0x000FFFFFFFFFFFFFL) | 0x0010000000000000L;
        }
        else
        {
            exponent = -1022;
            mantissa = m_doubleBits & 0x000FFFFFFFFFFFFFL;
        }

        exponent -= (52 - m_insignificant_bits_number);
        mantissa = mantissa >> m_insignificant_bits_number;

        int mantissa_bytes_number = EncodeMantissa(mantissa, binStack);
        int exponent_bytes_number = EncodeExponent(exponent, binStack);
        EncodeInfoByte(exponent_bytes_number, binStack);

        int V_length = 1 + exponent_bytes_number + mantissa_bytes_number;
        binStack.stack((byte)V_length);

        return 1 + V_length;
    }

    private int EncodeMantissa(long mantissa, BinaryStack binStack)
    {
        byte b0 = (byte)(mantissa & 0x00000000000000ffL);
        byte b1 = (byte)((mantissa & 0x000000000000ff00L) >> 8);
        byte b2 = (byte)((mantissa & 0x0000000000ff0000L) >> 16);
        byte b3 = (byte)((mantissa & 0x00000000ff000000L) >> 24);
        byte b4 = (byte)((mantissa & 0x000000ff00000000L) >> 32);
        byte b5 = (byte)((mantissa & 0x0000ff0000000000L) >> 40);
        byte b6 = (byte)((mantissa & 0x00ff000000000000L) >> 48);

        if (b6 == 0)
        {
            if (b5 == 0)
            {
                if (b4 == 0)
                {
                    if (b3 == 0)
                    {
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
                    else
                    {
                        binStack.stack(b0);
                        binStack.stack(b1);
                        binStack.stack(b2);
                        binStack.stack(b3);
                        return 4;
                    }
                }
                else
                {
                    binStack.stack(b0);
                    binStack.stack(b1);
                    binStack.stack(b2);
                    binStack.stack(b3);
                    binStack.stack(b4);
                    return 5;
                }
            }
            else
            {
                binStack.stack(b0);
                binStack.stack(b1);
                binStack.stack(b2);
                binStack.stack(b3);
                binStack.stack(b4);
                binStack.stack(b5);
                return 6;
            }
        }
        else
        {
            binStack.stack(b0);
            binStack.stack(b1);
            binStack.stack(b2);
            binStack.stack(b3);
            binStack.stack(b4);
            binStack.stack(b5);
            binStack.stack(b6);
            return 7;
        }        
    }

    private int EncodeExponent(int exponent, BinaryStack binStack)
    {
        int b0 = exponent & 0x000000FF;
        int b1 = (exponent & 0x0000FF00) >> 8;

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
        boolean isPositive = m_doubleBits >= 0 ? true : false;
        if (isPositive)
        {
            if (exponent_bytes_number == 1)
            {
                binStack.stack(0x80);
            }
            else
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
            else
            {
                binStack.stack(0xC1);
            }
        }
    }

    private int CountInsignificantBits()
    {
        for (int i = 0; i <= 6; i++)
        {
            int b = (int)((m_doubleBits >> (i * 8)) & 0xFFL);
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
        return 52;
    }
}












