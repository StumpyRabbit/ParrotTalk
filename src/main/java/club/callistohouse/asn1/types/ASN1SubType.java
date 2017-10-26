package club.callistohouse.asn1.types;

import java.io.IOException;

import club.callistohouse.asn1.ASN1InputStream;

public abstract class ASN1SubType extends ASN1Type {
	public ASN1Type parent;
	public int tag;

	public ASN1SubType(String name) { super(name); }
	public ASN1SubType(String name, ASN1Type parent) { super(name); this.parent = parent; }
	@Override
	public int asn1Tag() { return getTag(); }

	public boolean isTypeFor(Object obj) { return parent.isTypeFor(obj); }
	public ASN1Type getParent() { return parent; }
	public void setParent(ASN1Type asn1Entity) { this.parent = asn1Entity; }
	public int getTag() { return tag; }
	public void setTag(int tag) { this.tag = tag; }

	@Override
	public Object decodeConstructedValue(ASN1InputStream derStream, int length)
			throws IOException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
