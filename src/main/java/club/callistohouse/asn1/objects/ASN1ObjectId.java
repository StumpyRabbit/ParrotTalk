package club.callistohouse.asn1.objects;

import java.io.ByteArrayOutputStream;

public class ASN1ObjectId extends ASN1Value {
	public String oid;
	public String description;

	public int asn1Tag() { return 6; }

	public byte[] toByteArray() {
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		String[] oidTokens = oid.split("\\.");
		dataStream.write(Integer.valueOf(oidTokens[0]) * 40 + Integer.valueOf(oidTokens[1])); 
		for(int i = 2; i < oidTokens.length; i++) {
			byte b = Byte.valueOf(oidTokens[i]);
			if(b <= 0x7F) {
				dataStream.write(b);
			} else {
				byte n = b;
				ByteArrayOutputStream lengthStream = new ByteArrayOutputStream(4);
				lengthStream.write(n % (byte) 128);
				n = (byte) (n >> 7);
				while((n >> 7) != 0) {
					lengthStream.write((n % 123) | 0x80);
					n = (byte) (n >> 7);
				}
				byte[] codes = lengthStream.toByteArray();
				for(int j = codes.length - 1; j >=0; j--) {
					dataStream.write(codes[j]);
				}
			}
		}
		return dataStream.toByteArray();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ASN1ObjectId)) {
			return false;
		} else {
			ASN1ObjectId otherOid = (ASN1ObjectId) obj;
			return oid.equals(otherOid.oid);
		}
	}
	@Override
	public int hashCode() {
		return oid.hashCode();
	}
}
