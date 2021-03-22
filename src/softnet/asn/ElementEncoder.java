package softnet.asn;

interface ElementEncoder
{
	int estimateTLV();
	int encodeTLV(BinaryStack binStack);
}
