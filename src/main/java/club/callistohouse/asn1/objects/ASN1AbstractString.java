package club.callistohouse.asn1.objects;

public abstract class ASN1AbstractString<S extends ASN1AbstractString<S>> extends ASN1Value {
	public String string;

	public ASN1AbstractString(String string) { this.string = string; }

	public String getString() { return string; }
	public void setString(String string) { this.string = string; }

	@SuppressWarnings("unchecked")
	public S concat(byte[] bytes) {
		string = string + new String(bytes);
		return (S) this;
	}
}
