package club.callistohouse.asn1.types.constructed;

import java.util.ArrayList;
import java.util.List;

import club.callistohouse.asn1.types.ASN1ChoiceElement;

public abstract class ASN1StructuredType<C extends ASN1ChoiceElement> extends ASN1ConstructedType {
	List<C> elements = new ArrayList<C>(5);

	public ASN1StructuredType(String name) { super(name); }

	public abstract C newElement();
	public C addTypeString(String elementName, String typeString) {
		C newElement = newElement();
		newElement.symbol = elementName;
		newElement.type = typeFromString(typeString);
		elements.add(newElement);
		return newElement;
	}
	public C addTypeStringExplicit(String elementName, String typeString, int tag) {
		C newElement = addTypeString(elementName, typeString);
		newElement.explicitTag(tag);
		return newElement;
	}
	public C addTypeStringImplicit(String elementName, String typeString, int tag) {
		C newElement = addTypeString(elementName, typeString);
		newElement.implicitTag(tag);
		return newElement;
	}
	protected C addElement(C element) {
		elements.add(element);
		element.addedTo(this);
		return element;
	}
}
