package club.callistohouse.asn1.types.constructed;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1Type;

public abstract class ASN1ConstructedType extends ASN1Type {

	public ASN1ConstructedType() { this(""); }
	public ASN1ConstructedType(String name) { super(name); }
	public boolean isConstructed() { return true; }

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		derStream.nextPutTag(asn1Tag());
		ASN1OutputStream tempStream = derStream.newStream();
		encodeValue(obj, tempStream);
		derStream.nextPutLength(tempStream.stream.size());
		derStream.write(tempStream.stream.toByteArray(), 0, tempStream.stream.size());
	}
	public Object decode(ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		derStream.nextTag();
		return decodeValue(derStream, derStream.nextLength());
	}
	@Override
	public Object decodeConstructedValue(ASN1InputStream derStream, int length)
			throws IOException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
