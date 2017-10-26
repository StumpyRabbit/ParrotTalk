package club.callistohouse.asn1.objects;

public class ASN1T61String extends ASN1AbstractString<ASN1T61String> {

	public ASN1T61String() {
		this("");
	}
	public ASN1T61String(String string) {
		super(string);
	}

	public int asn1Tag() { return 20; }
}
