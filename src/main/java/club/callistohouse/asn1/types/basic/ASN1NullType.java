package club.callistohouse.asn1.types.basic;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.objects.ASN1Null;

public class ASN1NullType extends ASN1BasicType {

	public ASN1NullType() { super("ASN1NullType"); }

	public boolean isTypeFor(Object obj) { return (obj == null) | (obj instanceof ASN1Null); }
	public Integer sizeOfObject(Object obj) { return 0; }
	public int asn1Tag() { return 5; }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) {
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) {
		// TODO Auto-generated method stub
		return ASN1Null.NULL;
	}

}
