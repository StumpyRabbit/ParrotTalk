package club.callistohouse.asn1.types.basic;

import club.callistohouse.asn1.objects.ASN1VideotexString;

public class ASN1VideotexStringType extends ASN1WrappedStringType<ASN1VideotexString> {

	public ASN1VideotexStringType() { super("ASN1VideotexStringType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof ASN1VideotexString;
	}

	@Override
	public int asn1Tag() { return 21; }

	@Override
	public Class<ASN1VideotexString> octetsClass() {
		// TODO Auto-generated method stub
		return ASN1VideotexString.class;
	}

}
