package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public abstract class ASN1WrapperType extends ASN1Type {

	public ASN1WrapperType(String name) { super(name); }

	@Override
	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object decodeConstructedValue(ASN1InputStream derStream, int length)
			throws IOException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
