package club.callistohouse.asn1.types.basic;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.objects.ASN1BitString;

public class ASN1BitStringType extends ASN1OctetsType {

	public ASN1BitStringType() { super("ASN1BitStringType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof ASN1BitString;
	}

	@Override
	public Integer sizeOfObject(Object obj) { return ((ASN1BitString)obj).bytes.length + 1; }
	@Override
	public int asn1Tag() { return 3; }

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
		ASN1BitString bitString = new ASN1BitString();
		int stopPosition = derStream.getPosition() + length;
		while(derStream.getPosition() < stopPosition) {
			ASN1BitString bits = (ASN1BitString) decode(derStream);
			bitString.bitsPadding = bits.bitsPadding;
			byte[] newBytes = new byte[bitString.bytes.length + bits.bytes.length];
			System.arraycopy(bitString.bytes, 0, newBytes, 0, bitString.bytes.length);
			System.arraycopy(bits.bytes, 0, newBytes, bitString.bytes.length, bits.bytes.length);
			bitString.bytes = newBytes;
		}
		return bitString;
	}

	private Object decodeValueIndefiniteLength(ASN1InputStream derStream) throws InstantiationException, IllegalAccessException, IOException {
		ASN1BitString octets = new ASN1BitString();
		byte[] bytes = new byte[0];
		boolean loopTest = true;
		do {
			octets = octets.concat(bytes);
			if(derStream.peekTag() == 0) {
				new ASN1EndOfIndefiniteLengthType().decode(derStream);
				loopTest = false;
			} else {
				bytes = (byte[]) decode(derStream);
			}
		} while(loopTest);
		return octets;
	}

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		derStream.write(((ASN1BitString)obj).bitsPadding);
		derStream.write(((ASN1BitString)obj).bytes);		
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		ASN1BitString bitString = new ASN1BitString();
		byte[] bytes = new byte[1];
		derStream.read(bytes);
		bitString.bitsPadding = bytes[0];
		bytes = new byte[length - 1];
		derStream.read(bytes);
		bitString.bytes = bytes;		
		return bitString;
	}

}
