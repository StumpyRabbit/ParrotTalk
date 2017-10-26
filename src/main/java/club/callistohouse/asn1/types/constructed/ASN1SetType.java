package club.callistohouse.asn1.types.constructed;

import java.util.Set;

import club.callistohouse.asn1.objects.OrderedSet;

public class ASN1SetType extends ASN1UnmappedType {

	public ASN1SetType() { super(); }

	@Override
	public int asn1Tag() { return 49; }

	@SuppressWarnings("rawtypes")
	@Override
	protected Set<?> newContainer() {
		// TODO Auto-generated method stub
		return new OrderedSet();
	}

}
