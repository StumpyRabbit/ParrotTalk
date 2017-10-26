package club.callistohouse.asn1.types;

import club.callistohouse.asn1.ASN1Module;

public class ASN1Entity {
	public String symbol;
	public ASN1Module module;

	public ASN1Entity(String name) { symbol = name; }
	public ASN1Entity(String name, ASN1Module module) {
		symbol = name;
		module.add(this);
	}

	public ASN1Type typeFromString(String typeSpec) {
		if(module == null) {
			return ASN1Module.primitiveTypeFind(typeSpec);
		} else {
			return module.typeFrom(typeSpec);
		}
	}

	public String getSymbol() { return symbol; }
	public void setSymbol(String symbol) { this.symbol = symbol; }
	public ASN1Module getModule() { return module; }
	public void setModule(ASN1Module module) { this.module = module; }
}
