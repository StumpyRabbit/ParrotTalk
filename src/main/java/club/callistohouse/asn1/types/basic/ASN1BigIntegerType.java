package club.callistohouse.asn1.types.basic;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1BigIntegerType extends ASN1BasicType {

	public ASN1BigIntegerType() { super("ASN1BigIntegerType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof BigInteger;
	}
	public Integer sizeOfObject(Object obj) {
		ASN1OutputStream out = new ASN1OutputStream();
		try {
			encodeValue(obj, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.stream.size();
	}
	public int asn1Tag() { return 2; }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		int length = 0;
		BigInteger myInt = (BigInteger) obj;
		if(myInt == BigInteger.ZERO) {
			derStream.write(0);
			return;
		}
		byte[] fullBytes = ByteBuffer.wrap(myInt.toByteArray()).array();
		for(int i = 0; i < fullBytes.length; i++) {
				if((fullBytes[i] != 0) & (fullBytes[i] != -1)) {
					length = fullBytes.length - i;
					break;
				}
			}

		if(length == 0) { length = 1; }
		byte[] bytes = new byte[length];
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = fullBytes[fullBytes.length - length + i];
		}

		if(myInt.compareTo(BigInteger.ZERO) >= 0) {
			if(0x80 == (bytes[0] & 0x80)) {
				derStream.write(0);
			}
		} else {
			if(0x00 == (bytes[0] & 0x80)) {
				derStream.write(255);
			}
		}
		for(byte b : bytes) {
			derStream.write(b);
			length += 1;
		}
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		byte[] bytes = new byte[length];
		boolean twosCompl = (derStream.peek() & 0x80) != 0;
		derStream.read(bytes);
		if(twosCompl) {
			return new BigInteger(ByteBuffer.wrap(bytes).array());
		} else {
			return new BigInteger(ByteBuffer.wrap(bytes).array());
		}
	}
}
