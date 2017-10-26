package club.callistohouse.asn1.objects;

import java.util.Arrays;

public class ASN1BitString extends ASN1AbstractString<ASN1BitString> {

	public byte bitsPadding;
	public byte[] bytes = new byte[0];

	public ASN1BitString() {
		this("");
	}
	public ASN1BitString(String string) {
		super(string);
	}

	public int asn1Tag() { return 3; }

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ASN1BitString)) {
			return false;
		} else {
			ASN1BitString bitString = (ASN1BitString) obj;
			return (bitString.bitsPadding == bitsPadding) & (Arrays.equals(bitString.bytes, bytes));
		}
	}
	@Override
	public int hashCode() {
		return bitsPadding + bytes.hashCode();
	}
}
