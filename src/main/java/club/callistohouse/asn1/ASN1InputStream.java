package club.callistohouse.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import club.callistohouse.asn1.types.ASN1Type;

public class ASN1InputStream extends InputStream {

	public ByteArrayInputStream stream;
	public int currentTag;
	public int count;

	public ASN1InputStream(byte[] bytes) { this(new ByteArrayInputStream(bytes)); }
	public ASN1InputStream(ByteArrayInputStream in) { count = in.available();  stream = in; }

	public Object realize(Object obj) { return obj; }
	public Object decode() throws IOException, InstantiationException, IllegalAccessException { return decode(ASN1Module.typeForTag(peekTag())); }
	public Object decode(ASN1Type type) throws IOException, InstantiationException, IllegalAccessException { return type.decode(this); }
	public int getPosition() { return count - stream.available(); }
	public void setPosition(int pos) { stream.reset(); stream.skip(pos); }
	public ByteArrayInputStream getStream() { return stream; }
	public void setStream(ByteArrayInputStream stream) { this.stream = stream; }

	public int peek() { int pos = getPosition(); int tag = stream.read(); setPosition(pos); return tag; }
	public int peekTag() { int pos = getPosition(); int tag = nextTag(); setPosition(pos); return tag; }

	public int nextTag() {
		int octet = 0;
		int tag = stream.read();
		if((tag & 0x1F) == 0x1F) {
			tag = 0;
			do {
				octet = stream.read();
				tag = (tag << 7) | (octet & 0x7F); 
			} while((octet & 0x80) > 0);
		}
		currentTag = tag;
		return tag;	
	}
	public int nextLength() {
		int value = 0;
		int first = stream.read();

		if(first == 0x80) { return -1; }
		if((first & 0x80) > 0) {
			int sizeCount = first & 0x7F;
			for(int i = 0; i < sizeCount; i++) {
				value = (value << 8) + stream.read();
			}
			return value;
		} else {  return first; }
	}

	@Override
	public int read() throws IOException { return stream.read(); }

	public static Object decode(byte[] bytes) throws IOException, InstantiationException, IllegalAccessException { try(ASN1InputStream stream = new ASN1InputStream(bytes)) { return stream.decode(); } }
	public static Object decode(byte[] bytes, ASN1Type type) throws IOException, InstantiationException, IllegalAccessException { try(ASN1InputStream stream = new ASN1InputStream(bytes)) { return stream.decode(type); } }
}
