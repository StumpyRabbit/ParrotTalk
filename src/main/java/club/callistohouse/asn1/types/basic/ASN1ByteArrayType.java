package club.callistohouse.asn1.types.basic;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.utils.ArrayUtil;

public class ASN1ByteArrayType extends ASN1OctetsType {

	public ASN1ByteArrayType() { super("ASN1ByteArrayType"); }

	public boolean isTypeFor(Object obj) { return obj instanceof byte[]; }
	public Integer sizeOfObject(Object obj) { return ((byte[]) obj).length; }
	public int asn1Tag() { return 4; }

	public Object decode(ASN1InputStream derStream) throws IOException {
		Object obj = null;
		int tag = derStream.peekTag();
		int numericTag = tag & 0x1F;
		if(numericTag != (asn1Tag() & 0x1F)) {
			throw new IOException("bad tag");
		}
		derStream.nextTag();
		if((tag & 0x20) > 0) {
			try {
				obj = decodeConstructedValue(derStream, derStream.nextLength());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			obj = decodeValue(derStream, derStream.nextLength());
		}
		return obj;
	}

	public Object decodeConstructedValue(ASN1InputStream derStream, int length) throws InstantiationException, IllegalAccessException, IOException {
		if(length == -1) {
			return decodeValueIndefiniteLength(derStream);
		}
		byte[] octets = new byte[0];
		int stopPosition = derStream.getPosition() + length;
		while(derStream.getPosition() < stopPosition) {
			octets = ArrayUtil.concatAll(octets, (byte[]) decode(derStream));
		}
		return octets;
	}

	private Object decodeValueIndefiniteLength(ASN1InputStream derStream) throws InstantiationException, IllegalAccessException, IOException {
		byte[] octets = new byte[0];
		byte[] bytes = new byte[0];
		boolean loopTest = true;
		do {
			octets = ArrayUtil.concatAll(octets, bytes);
			if(derStream.peekTag() == 0) {
				new ASN1EndOfIndefiniteLengthType().decode(derStream);
				loopTest = false;
			} else {
				bytes = (byte[]) decode(derStream);
			}
		} while(loopTest);
		return octets;
	}

	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		derStream.write((byte[]) obj);
	}
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		byte[] bytes = new byte[length];
		derStream.read(bytes);
		return bytes;
	}

}
