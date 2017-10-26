package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1ImplicitSubType extends ASN1SubType {

	public ASN1ImplicitSubType() { super(null); }
	public ASN1ImplicitSubType(ASN1Type parent) { super("", parent); }

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		int myTag = asn1Tag() | 128;
		if(getParent().isConstructed()) {
			myTag = myTag | 32;
		}
		derStream.nextPutTag(myTag);
		ASN1OutputStream tempStream = derStream.newStream();
		getParent().encodeValue(obj, tempStream);
		derStream.nextPutLength(tempStream.stream.size());
		derStream.write(tempStream.stream.toByteArray());
	}

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) {
		// TODO Auto-generated method stub
		
	}
	public Object decode(ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		int thisTag = derStream.nextTag();
		if(!getParent().isConstructed() && ((thisTag & 0x20) > 0)) {
			return getParent().decodeConstructedValue(derStream, derStream.nextLength());
		} else {
			return getParent().decodeValue(derStream, derStream.nextLength());
		}
	}
	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) {
		// TODO Auto-generated method stub
		return null;
	}
}

