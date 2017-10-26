package club.callistohouse.asn1.objects;

public class ASN1VisibleString extends ASN1AbstractString<ASN1VisibleString> {

	public ASN1VisibleString() {
		this("");
	}
	public ASN1VisibleString(String string) {
		super(string);
	}

	public int asn1Tag() { return 26; }
}
