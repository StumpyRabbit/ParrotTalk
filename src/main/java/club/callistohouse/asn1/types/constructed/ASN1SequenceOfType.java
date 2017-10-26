package club.callistohouse.asn1.types.constructed;

import java.util.ArrayList;
import java.util.Collection;

public class ASN1SequenceOfType extends ASN1UnstructuredType {

	public ASN1SequenceOfType() { super(); }

	@Override
	public int asn1Tag() { return 48; }

	@SuppressWarnings("rawtypes")
	@Override
	protected Collection newContainer() {
		// TODO Auto-generated method stub
		return new ArrayList();
	}

}
