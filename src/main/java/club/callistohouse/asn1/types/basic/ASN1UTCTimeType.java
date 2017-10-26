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

public class ASN1UTCTimeType extends ASN1BasicType {

	public ASN1UTCTimeType() { super("ASN1UTCTimeType"); }

	public boolean isTypeFor(Object obj) {
		return obj instanceof Date;
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
	public int asn1Tag() { return 23; }

	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) throws IOException {
		Date date = (Date) obj;
		Instant instant = Instant.ofEpochMilli(date.getTime());
		DateTimeFormatter utcFormatter = DateTimeFormatter
			       .ofPattern ( "uuMMddHHmmssX" ) 
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
				DateTimeFormatter.ofPattern("uuMMddHHmmssX"));

		Date date = Date.from(actual.toInstant());
		return date;
	}

}
