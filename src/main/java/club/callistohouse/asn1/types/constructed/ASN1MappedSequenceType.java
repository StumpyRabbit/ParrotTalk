package club.callistohouse.asn1.types.constructed;

public class ASN1MappedSequenceType<T> extends ASN1MappedType<T> {

	public ASN1MappedSequenceType(String name, Class<T> mapClass) { super(name, mapClass); }

	@Override
	public int asn1Tag() { return 48; }

}
