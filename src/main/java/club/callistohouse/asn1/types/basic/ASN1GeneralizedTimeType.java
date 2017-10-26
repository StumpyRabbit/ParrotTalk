package club.callistohouse.asn1.types.basic;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1GeneralizedTimeType extends ASN1BasicType {

	public ASN1GeneralizedTimeType() { super("ASN1GeneralizedTimeType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof Date;
	}

	public Integer sizeOfObject(Object obj) {
		ASN1OutputStream out = new ASN1OutputStream();
		try {
			encodeValue(obj, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.stream.size();
	}
	public int asn1Tag() { return 24; }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		Date date = (Date) obj;
		Instant instant = Instant.ofEpochMilli(date.getTime());
		DateTimeFormatter utcFormatter = DateTimeFormatter
			       .ofPattern ( "uuuuMMddHHmmssX" ) 
			       .withLocale( Locale.US )
			       .withZone( ZoneId.of("UTC"));
		String formattedDate = utcFormatter.format(instant);
		derStream.write(formattedDate.getBytes());
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) throws IOException {
		byte[] bytes = new byte[length];
		derStream.read(bytes);

		OffsetDateTime actual = OffsetDateTime.parse(
				new String(bytes),
				DateTimeFormatter.ofPattern("uuuuMMddHHmmssX"));

		Date date = Date.from(actual.toInstant());
		return date;
	}

}
