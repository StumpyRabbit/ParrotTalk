package club.callistohouse.asn1.types.basic;

import club.callistohouse.asn1.objects.ASN1GraphicString;

public class ASN1GraphicStringType extends ASN1WrappedStringType<ASN1GraphicString> {

	public ASN1GraphicStringType() { super("ASN1GraphicStringType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof ASN1GraphicString;
	}

	@Override
	public int asn1Tag() { return 25; }

	@Override
	public Class<ASN1GraphicString> octetsClass() {
		// TODO Auto-generated method stub
		return ASN1GraphicString.class;
	}

}
