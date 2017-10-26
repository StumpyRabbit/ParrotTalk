package club.callistohouse.asn1.types.constructed;

import java.util.Collection;

import club.callistohouse.asn1.objects.OrderedSet;

public class ASN1SetOfType extends ASN1UnstructuredType {

	public ASN1SetOfType() { super(); }

	@Override
	public int asn1Tag() { return 49; }

	@SuppressWarnings("rawtypes")
	@Override
	protected Collection newContainer() {
		// TODO Auto-generated method stub
		return new OrderedSet();
	}

}
