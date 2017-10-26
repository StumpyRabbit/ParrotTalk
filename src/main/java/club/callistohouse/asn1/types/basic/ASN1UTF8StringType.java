package club.callistohouse.asn1.types.basic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1UTF8StringType extends ASN1OctetsType {

	public ASN1UTF8StringType() { super("ASN1UTF8StringType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof String;
	}

	@Override
	public Integer sizeOfObject(Object obj) {
		ASN1OutputStream out = new ASN1OutputStream();
		try {
			encodeValue(obj, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.stream.size();
	}

	@Override
	public int asn1Tag() { return 12; }

	public Object decode(ASN1InputStream derStream) throws IOException {
		Object obj = null;
		int tag = derStream.peekTag();
		int numericTag = tag & 0x1F;
		if(numericTag != (asn1Tag() & 0x1F)) {
			throw new IOException("bad tag");
		}
		derStream.nextTag();
		if((tag & 0x20) > 0) {
			try {
				obj = decodeConstructedValue(derStream, derStream.nextLength());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			obj = decodeValue(derStream, derStream.nextLength());
		}
		return obj;
	}

	public Object decodeConstructedValue(ASN1InputStream derStream, int length) throws InstantiationException, IllegalAccessException, IOException {
		if(length == -1) {
			return decodeValueIndefiniteLength(derStream);
		}
		String octets = new String();
		int stopPosition = derStream.getPosition() + length;
		while(derStream.getPosition() < stopPosition) {
			octets = octets + decode(derStream);
		}
		return octets;
	}

	private Object decodeValueIndefiniteLength(ASN1InputStream derStream) throws InstantiationException, IllegalAccessException, IOException {
		String octets = new String();
		boolean loopTest = true;
		do {
			if(derStream.peekTag() == 0) {
				new ASN1EndOfIndefiniteLengthType().decode(derStream);
				loopTest = false;
			} else {
				octets = octets + String.valueOf((byte[]) decode(derStream));
			}
		} while(loopTest);
		return octets;
	}

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		try {
			Writer writer = new OutputStreamWriter(derStream, "UTF-8");
			BufferedWriter fout = new BufferedWriter(writer);
			fout.write(((String)obj).toCharArray());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(length);
		byte[] bytes = new byte[length];
		derStream.read(bytes);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			Reader reader = new InputStreamReader(in,"UTF-8");
			BufferedReader fin = new BufferedReader(reader);
			Stream<String> s = fin.lines();
			Consumer<String> cons = new Consumer<String>() {
				public void accept(String t) {
					try {
						out.write(t.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}};
			s.forEach(cons);
            fin.close();
			return new String(out.toByteArray());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}
}
