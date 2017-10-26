package club.callistohouse.asn1.types.basic;

import club.callistohouse.asn1.objects.ASN1VisibleString;

public class ASN1VisibleStringType extends ASN1WrappedStringType<ASN1VisibleString> {

	public ASN1VisibleStringType() { super("ASN1VisibleStringType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof ASN1VisibleString;
	}

	@Override
	public int asn1Tag() { return 26; }

	@Override
	public Class<ASN1VisibleString> octetsClass() {
		// TODO Auto-generated method stub
		return ASN1VisibleString.class;
	}

}
