package club.callistohouse.asn1.objects;

public class ASN1UniversalString extends ASN1AbstractString<ASN1UniversalString> {

	public ASN1UniversalString() {
		this("");
	}
	public ASN1UniversalString(String string) {
		super(string);
	}

	public int asn1Tag() { return 28; }
}
