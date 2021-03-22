package softnet.asn;

class TaggedElementEncoder implements ElementEncoder
{
	private int m_Tag;
	private ElementEncoder m_ElementEncoder;

	private static int C_ContextSpecific_Container = 0xA0;

    public TaggedElementEncoder(int tag, ElementEncoder elementEncoder)
    {
        m_Tag = tag;
        m_ElementEncoder = elementEncoder;
    }

    public int estimateTLV()
    {
        int V_length = m_ElementEncoder.estimateTLV();
        return 1 + LengthEncoder.estimate(V_length) + V_length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        int V_length = m_ElementEncoder.encodeTLV(binStack);
        int L_length = LengthEncoder.encode(V_length, binStack);
        binStack.stack((byte)(C_ContextSpecific_Container | m_Tag));
        return 1 + L_length + V_length;
    }
}
