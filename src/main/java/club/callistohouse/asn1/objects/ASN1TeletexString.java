package club.callistohouse.asn1.objects;

public class ASN1TeletexString extends ASN1AbstractString<ASN1TeletexString> {

	public ASN1TeletexString() {
		this("");
	}
	public ASN1TeletexString(String string) {
		super(string);
	}

	public int asn1Tag() { return 20; }
}
