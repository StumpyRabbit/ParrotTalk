package club.callistohouse.asn1.types.basic;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1Module;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1IntegerType extends ASN1BasicType {

	public ASN1IntegerType() { super("ASN1IntegerType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof Integer;
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

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		BigInteger big = new BigInteger(((Integer)obj).toString());
		ASN1Module.typeForObject(big).encode(big, derStream);
	}
	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		int length = 0;
		int myInt = ((Integer) obj).intValue();
		if(myInt == 0) {
			derStream.write(0);
			return;
		}
		byte[] fullBytes = ByteBuffer.allocate(4).putInt(myInt).array();
		boolean isNeg = myInt < 0;
		if(isNeg) {
			for(int i = 0; i < fullBytes.length; i++) {
				if((fullBytes[i] != 0) & (fullBytes[i] != -1)) {
					length = fullBytes.length - i;
					break;
				}
			}
		} else {
			for(int i = 0; i < fullBytes.length; i++) {
				if(fullBytes[i] != 0) {
					length = fullBytes.length - i;
					break;
				}
			}
		}
		if(length == 0) { length = 1; }
		byte[] bytes = new byte[length];
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = fullBytes[fullBytes.length - length + i];
		}
		if(!isNeg) {
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
			return new BigInteger(ByteBuffer.wrap(bytes).array()).intValue();
		} else {
			return new BigInteger(ByteBuffer.wrap(bytes).array()).intValue();
		}
	}
}
