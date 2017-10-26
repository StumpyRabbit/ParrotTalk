package club.callistohouse.asn1.objects;

public class ASN1GeneralString extends ASN1AbstractString<ASN1GeneralString> {

	public ASN1GeneralString() {
		this("");
	}
	public ASN1GeneralString(String string) {
		super(string);
	}

	public int asn1Tag() { return 27; }
}
