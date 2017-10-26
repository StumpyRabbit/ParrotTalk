package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.constructed.ASN1StructuredType;

public class ASN1ChoiceElement {
	public ASN1Type owner, type;
	public String symbol;

	public boolean isTypeFor(Object obj) { return type.isTypeFor(obj); }
	public boolean matchesTag(int tag) { return type.matchesTag(tag); }

	public ASN1Type explicitTag(int tag) {
		type = type.explicitSubType();
		((ASN1ExplicitSubType)type).setTag(tag);
		return type;
	}
	public ASN1Type implicitTag(int tag) {
		type = type.implicitSubType();
		((ASN1ImplicitSubType)type).setTag(tag);
		return type;
	}
	public void addedTo(@SuppressWarnings("rawtypes") ASN1StructuredType structType) {
		owner = structType;
	}
	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		type.encode(obj, derStream);
	}
	public Object decode(ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		int tag = derStream.peekTag();
		if(matchesTag(tag)) {
			return type.decode(derStream);
		}
		throw new IOException("bad tag");
	}
}
