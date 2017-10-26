package club.callistohouse.asn1.types.basic;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1Type;

public class ASN1AnyDefinedByType extends ASN1BasicType {

	public String definedBy;

	public ASN1AnyDefinedByType() { super("ASN1AnyDefinedByType"); }

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		throw new IOException("must encode in scope of a container");
	}
	public void encode(Object obj, ASN1OutputStream derStream, Object owner, ASN1Type ownerType) throws IOException {
		ownerType.encode(obj, derStream);
	}

	@Override
	public Integer sizeOfObject(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int asn1Tag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
