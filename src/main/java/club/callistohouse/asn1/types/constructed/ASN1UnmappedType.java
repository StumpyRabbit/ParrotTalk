package club.callistohouse.asn1.types.constructed;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1EndOfIndefiniteLengthMarker;
import club.callistohouse.asn1.types.basic.ASN1EndOfIndefiniteLengthType;

public abstract class ASN1UnmappedType extends ASN1ConstructedType {

	public ASN1UnmappedType() { super(); }

	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		for(Object o : (Collection)obj) {
			derStream.encode(o);
		}
	}

	protected abstract Collection newContainer();
	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException, InstantiationException, IllegalAccessException {
		if(length == -1) {
			return decodeValueIndefiniteLength(derStream);
		}
		int stopPosition = derStream.getPosition() + length;
		Collection target = (Collection) newContainer();
		for(int index = 0; derStream.getPosition() < stopPosition; index++) {
			Object obj = derStream.decode();
			target.add(obj);
		}
		if(derStream.getPosition() < stopPosition) { throw new IOException("invalid length"); }
		return target;
	}
	private Object decodeValueIndefiniteLength(ASN1InputStream derStream) throws InstantiationException, IllegalAccessException, IOException {
		List target = (List) newContainer();

		Object value = null;
		int markIndex = 0;
		do {
			if(derStream.peekTag() == 0) {
				value = new ASN1EndOfIndefiniteLengthType().decode(derStream);
			} else {
				target.add(derStream.decode());
			}
		} while(! (value instanceof ASN1EndOfIndefiniteLengthMarker));
		return target;
	}
}
