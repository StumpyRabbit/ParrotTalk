package club.callistohouse.asn1.types.constructed;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1ChoiceElement;

public class ASN1ChoiceType extends ASN1StructuredType<ASN1ChoiceElement> {

	public ASN1ChoiceType(String name) { super(name); }
	@Override
	public int asn1Tag() { return -1; }
	public boolean matchesTag(int tag) {
		for(ASN1ChoiceElement element : elements) {
			if(element.matchesTag(tag)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTypeFor(Object obj) {
		for(ASN1ChoiceElement type : elements) {
			if(type.isTypeFor(obj)) {
				return true;
			}
		}
		return false;
	}

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		ASN1OutputStream tempStream = derStream.newStream();
		encodeValue(obj, tempStream);
		derStream.write(tempStream.stream.toByteArray(), 0, tempStream.stream.size());
	}
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		for(ASN1ChoiceElement element : elements) {
			if(element.isTypeFor(obj)) {
				element.encode(obj, derStream);
				return;
			}
		}
		throw new IOException("no choice for object");
	}
	public Object decode(ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		if(matchesTag(derStream.peekTag())) {
			return decodeValue(derStream, -1);
		} else {
			throw new IOException("bad type for object");
		}
	}
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException, InstantiationException, IllegalAccessException {
		int tag = derStream.peekTag();
		for(ASN1ChoiceElement element : elements) {
			if(element.matchesTag(tag)) {
				return element.decode(derStream);
			}
		}
		throw new IOException("no choice for tag");
	}
	@Override
	public ASN1ChoiceElement newElement() { return new ASN1ChoiceElement(); }
}
