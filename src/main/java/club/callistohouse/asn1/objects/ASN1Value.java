package club.callistohouse.asn1.objects;

import java.io.IOException;

import club.callistohouse.asn1.ASN1OutputStream;

public abstract class ASN1Value {

	public abstract int asn1Tag();
	public byte[] asAsn1DerBytes() throws IOException { return ASN1OutputStream.encodeObject(this); }
}
