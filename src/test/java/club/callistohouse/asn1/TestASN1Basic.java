package club.callistohouse.asn1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import club.callistohouse.asn1.objects.ASN1ObjectId;
import club.callistohouse.asn1.objects.OrderedSet;
import club.callistohouse.asn1.types.basic.ASN1GeneralizedTimeType;

public class TestASN1Basic {

	@Test
	public void testNull() throws InstantiationException, IllegalAccessException, IOException {
		byte[] bytes = new byte[] {5, 0};
		Object obj = (Object) ASN1InputStream.decode(bytes);
		assertTrue(obj == null);
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testBoolean() throws InstantiationException, IllegalAccessException, IOException {
		byte[] bytes = new byte[] {1, 1, (byte)255};
		boolean testObj = true;
		boolean obj = (boolean) ASN1InputStream.decode(bytes);
		assertTrue(obj == testObj);
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testObjectIdentifier() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {6, 8, 43, 6, 1, 5, 5, 7, 8, 5};
		ASN1ObjectId testObj = new ASN1ObjectId();
		testObj.oid = "1.3.6.1.5.5.7.8.5";
		
		ASN1ObjectId obj = (ASN1ObjectId) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testInteger() throws IOException, InstantiationException, IllegalAccessException {
		testIntegerBytes(new byte[] {2, 2, 3, 21}, 789);
		testIntegerBytes(new byte[] {2, 1, 0}, 0);
		testIntegerBytes(new byte[] {2, 1, 1}, 1);
		testIntegerBytes(new byte[] {2, 1, (byte) 0xFF}, -1);

		testIntegerBytes(new byte[] {2, 1, 0x7F}, 127);
		testIntegerBytes(new byte[] {2, 2, 0, (byte) 0x80}, 128);
		testIntegerBytes(new byte[] {2, 2, 1, 0}, 256);
		testIntegerBytes(new byte[] {2, 1, (byte) 0x80}, -128);
		testIntegerBytes(new byte[] {2, 2, (byte) 0xFF, 0x7F}, -129);

		testIntegerBytes(new byte[] {2, 2, 0x7F, (byte)0xFF}, BigInteger.valueOf(32767));
		testIntegerBytes(new byte[] {2, 3, 0, (byte) 0x80, 0x00}, BigInteger.valueOf(32768));
		testIntegerBytes(new byte[] {2, 3, 1, 0, 0}, BigInteger.valueOf(65536));
		testIntegerBytes(new byte[] {2, 2, (byte) 0x80, 0x00}, BigInteger.valueOf(-32768));
		testIntegerBytes(new byte[] {2, 3, (byte) 0xFF, 0x7F, (byte) 0xFF}, BigInteger.valueOf(-32769));

		testIntegerBytes(new byte[] {2, 2, 0x7F, (byte)0xFF}, BigInteger.valueOf(32767));
		testIntegerBytes(new byte[] {2, 3, 0, (byte) 0x80, 0x00}, BigInteger.valueOf(32768));
		testIntegerBytes(new byte[] {2, 3, 1, 0, 0}, BigInteger.valueOf(65536));
		testIntegerBytes(new byte[] {2, 2, (byte) 0x80, 0x00}, BigInteger.valueOf(-32768));
		testIntegerBytes(new byte[] {2, 3, (byte) 0xFF, 0x7F, (byte) 0xFF}, BigInteger.valueOf(-32769));
	}

	private void testIntegerBytes(byte[] bytes, Object testObj) throws IOException, InstantiationException, IllegalAccessException {
		BigInteger localTestObj = null;
		if(testObj instanceof Integer) {
			localTestObj = BigInteger.valueOf((Integer) testObj);
		} else {
			localTestObj = (BigInteger) testObj;
		}
		Object obj = (BigInteger) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(localTestObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testReal() throws IOException, InstantiationException, IllegalAccessException {
		testRealBytes(new byte[] {9, 0}, (double)0.0);
		testRealBytes(new byte[] {9, 1, 0x40}, Double.POSITIVE_INFINITY);
		testRealBytes(new byte[] {9, 1, 0x41}, Double.NEGATIVE_INFINITY);
		testRealBytes(new byte[] {9, 5, (byte) 0x80, (byte) 0xFE, (byte) 0x55, (byte) 0x55, (byte) 0x55}, (double)1398101.25);
		testRealBytes(new byte[] {9, 3, (byte) 0x80, (byte) 0xFC, 1}, (double)0.0625);
	}

	private void testRealBytes(byte[] bytes, Object testObj) throws IOException, InstantiationException, IllegalAccessException {
		Double localTestObj = (Double) testObj;
		Object obj = (Double) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(localTestObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testSequence() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {48, 11, 1, 1, (byte)255, 2, 2, 3, 21, 2, 2, 108, 100};
/*		byte[] bytes = new byte[] {48, 11, 1, 1, (byte)255, 2, 2, 3, 21, 4, 2, 108, 100};*/
		List testObj = new ArrayList();
		testObj.add(true);
		testObj.add(BigInteger.valueOf(789));
		testObj.add(BigInteger.valueOf(27748));
		
		List obj = (List) ASN1InputStream.decode(bytes);
		assertTrue(testObj.equals(obj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
		newBytes = ASN1OutputStream.encodeObject(testObj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testSet() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {49, 11, 2, 2, 108, 100, 2, 2, 3, 21, 1, 1, (byte) 255 };
/*		byte[] bytes = new byte[] {49, 11, 1, 1, 255, 4, 2, 108, 100, 2, 2, 3, 21 };*/
		OrderedSet testObj = new OrderedSet<>();
		testObj.add(true);
		testObj.add(BigInteger.valueOf(27748));
		testObj.add(BigInteger.valueOf(789));
		
		OrderedSet obj = (OrderedSet) ASN1InputStream.decode(bytes);
		assertTrue(testObj.equals(obj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
 
		newBytes = ASN1OutputStream.encodeObject(testObj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testUTCTime() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {23, 13, 48, 55, 48, 51, 50, 50, 49, 53, 53, 56, 49, 55, 90};
		Date testObj = Date.from(OffsetDateTime.parse("2007-03-22T15:58:17+00:00").toInstant());

		Date obj = (Date) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(testObj);
		assertTrue(Arrays.equals(bytes, newBytes));
		newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testGeneralizedTime() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {24, 15, 50, 48, 48, 55, 48, 51, 50, 50, 49, 53, 53, 56, 49, 55, 90};
		Date testObj = new Date(OffsetDateTime.parse("2007-03-22T15:58:17+00:00").toEpochSecond() * 1000);

		Date obj = (Date) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(testObj, new ASN1GeneralizedTimeType());
		assertTrue(Arrays.equals(bytes, newBytes));
		newBytes = ASN1OutputStream.encodeObject(obj, new ASN1GeneralizedTimeType());
		assertTrue(Arrays.equals(bytes, newBytes));
	}
}
