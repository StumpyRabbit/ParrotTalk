package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.objects.ASN1ExplicitContextValue;

public class ASN1WrapperPrimitiveType extends ASN1WrapperType {

	public ASN1WrapperPrimitiveType() { super(""); }
	public ASN1WrapperPrimitiveType(String name) { super(name); }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		derStream.write(((ASN1ExplicitContextValue)obj).tagValue);
	}
	@Override
	public int asn1Tag() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		ASN1ExplicitContextValue aValue = new ASN1ExplicitContextValue();
		aValue.tagIndex = derStream.currentTag & 0x1F;
		aValue.tagIsPrimitive = true;
		byte[] bytes = new byte[length];
		derStream.read(bytes);
		aValue.tagValue = bytes;
		return aValue;
	}
}
