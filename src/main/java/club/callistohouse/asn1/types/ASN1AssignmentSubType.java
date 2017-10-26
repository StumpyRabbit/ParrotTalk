package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1AssignmentSubType extends ASN1SubType {

	public ASN1AssignmentSubType() { super(null); }

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		getParent().encode(obj, derStream);
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
