package club.callistohouse.asn1.types.basic;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1RealType extends ASN1BasicType {

	public ASN1RealType() { super("ASN1IntegerType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof Float | obj instanceof Double;
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
	public int asn1Tag() { return 9; }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		Double doubleObj = 0.0;            
		if(obj instanceof Float) {
			doubleObj = ((Float)obj).doubleValue();
		} else {
			doubleObj = (Double) obj;
		}
		long bits = Double.doubleToLongBits(doubleObj);
		if(doubleObj == 0) {return; }
		if(doubleObj == Float.POSITIVE_INFINITY) { derStream.write(0x40); return; }
		if(doubleObj == Float.NEGATIVE_INFINITY) { derStream.write(0x41); return; }
		long mantissa = (bits & 0x000fffffffffffffL);
		int exponent = Integer.valueOf(Long.valueOf(((bits & 0x7FF0000000000000L) >> 52) - 1023).toString());
		int base = 2;
		byte signByte = (byte) (((bits & 0x8000000000000000L) > 1) ? 0x40 : 0x00);
		ASN1OutputStream exponentStream = derStream.newStream();
		new ASN1IntegerType().encodeValue(exponent, exponentStream);
		byte[] exponentBytes = exponentStream.toByteArray();
		switch(exponentBytes.length) {
			case 1: 
				derStream.write(0x80 | (byte) signByte | 0x00);
				break;
			case 2:
				derStream.write(0x80 | (byte) signByte | 0x01);
				break;
			case 3:
				derStream.write(0x80 | (byte) signByte | 0x02);
				break;
			default:
				derStream.write(0x80 | (byte) signByte | 0x03);
				derStream.write(exponentBytes.length);
				break;
		}
		derStream.write(exponentBytes);

		byte[] leftBytes = Long.toUnsignedString(mantissa, 2).getBytes();
		int length = leftBytes.length;
		byte[] mantissaBytes = new byte[length];
		for(int i = 0; i < length; i++) {
			mantissaBytes[i] = leftBytes[length - i - 1];
		}
		for(byte b : mantissaBytes) {
			derStream.write(b);
		}
	}

	private Double decodeBinaryEncodedValue(ASN1InputStream derStream, int length, byte specialByte) throws IOException {
		int sign = ((specialByte & 0x40) == 0) ? 1 : -1;
		int base = 0;
		byte baseFlag = (byte) (specialByte & 0x30);
		switch (baseFlag) {
		case 0:
			base = 2;
			break;
		case 1:
			base = 8;
			break;
		case 2:
			base = 16;
			break;
		default:
			throw new IOException("bad base encoding of float");
		}
		int scalingFactor = specialByte & 0x0C;
		int exponentSizeCode = specialByte & 0x03;
		Integer exponent;
		if (exponentSizeCode == 3) {
			exponentSizeCode = derStream.nextLength();
			exponent = (Integer) new ASN1IntegerType().decodeValue(derStream, exponentSizeCode); 
		} else {
			exponent = (Integer) new ASN1IntegerType().decodeValue(derStream, exponentSizeCode + 1); 
		}
		int mantissaLength = length - (exponentSizeCode + 2);
		byte[] bytes = new byte[mantissaLength];
		derStream.read(bytes);
		long mantissa = new BigInteger(ByteBuffer.wrap(bytes).array()).longValue();
		return sign * mantissa * Math.pow(2, scalingFactor) * Math.pow(base, exponent);
	}

	@Override
	public Double decodeValue(ASN1InputStream derStream, int length) throws IOException {
		if(length == 0) { return 0.0; }
		byte specialByte = (byte) derStream.read();
		if((specialByte & 0x80) == 128) { return decodeBinaryEncodedValue(derStream, length, specialByte); }
		if((specialByte & 0xC0) == 0) {
			if((specialByte ^ 0x01) == 0) { return decodeDecimalEncodedNR1Value(derStream, length, specialByte); }
			if((specialByte ^ 0x02) == 0) { return decodeDecimalEncodedNR2Value(derStream, length, specialByte); }
			if((specialByte ^ 0x03) == 0) { return decodeDecimalEncodedNR3Value(derStream, length, specialByte); }
		}
		if((specialByte & 0x40) == 64) { return decodeSpecialEncodedValue(derStream, length, specialByte); }
		throw new IOException("bad construction of float");
	}

	private Double decodeDecimalEncodedNR1Value(ASN1InputStream derStream, int length, byte specialByte) throws IOException {
		throw new IOException("unsupported NR1 construction of float");
	}
	private Double decodeDecimalEncodedNR2Value(ASN1InputStream derStream, int length, byte specialByte) throws IOException {
		throw new IOException("unsupported NR2 construction of float");
	}
	private Double decodeDecimalEncodedNR3Value(ASN1InputStream derStream, int length, byte specialByte) throws IOException {
		throw new IOException("unsupported NR3 construction of float");
	}

	private Double decodeSpecialEncodedValue(ASN1InputStream derStream, int length, byte specialByte) throws IOException {
		if((length == 1) && ((specialByte ^ 0x40) == 0)) { return Double.POSITIVE_INFINITY; }
		if((length == 1) && ((specialByte ^ 0x41) == 0)) { return Double.NEGATIVE_INFINITY; }
		throw new IOException("bad construction of specially encoded float");
	}

	public static double getMantissa(double x) {
	    int exponent = Math.getExponent(x);
	    return x / Math.pow(2, exponent);
	}}
