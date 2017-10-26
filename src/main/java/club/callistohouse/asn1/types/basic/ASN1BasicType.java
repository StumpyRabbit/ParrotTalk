package club.callistohouse.asn1.types.basic;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1Type;

public abstract class ASN1BasicType extends ASN1Type {

	public ASN1BasicType(String name) { super(name); }

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		derStream.nextPutTag(asn1Tag());
		derStream.nextPutLength(sizeOfObject(obj));
		encodeValue(obj, derStream);
	}
	public abstract Integer sizeOfObject(Object obj) throws IOException;
	@Override
	public Object decodeConstructedValue(ASN1InputStream derStream, int length)
			throws IOException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
