package club.callistohouse.asn1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import club.callistohouse.asn1.objects.ASN1BMPString;
import club.callistohouse.asn1.objects.ASN1BitString;
import club.callistohouse.asn1.objects.ASN1GeneralString;
import club.callistohouse.asn1.objects.ASN1GraphicString;
import club.callistohouse.asn1.objects.ASN1IA5String;
import club.callistohouse.asn1.objects.ASN1NumericString;
import club.callistohouse.asn1.objects.ASN1PrintableString;
import club.callistohouse.asn1.objects.ASN1TeletexString;
import club.callistohouse.asn1.objects.ASN1UniversalString;
import club.callistohouse.asn1.objects.ASN1VideotexString;
import club.callistohouse.asn1.objects.ASN1VisibleString;

public class TestASN1Octets {

	@Test
	public void testBitString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {3, 3, 3, (byte) 168, 121};
		ASN1BitString testObj = new ASN1BitString();
		testObj.bytes = new byte[] {(byte) 168, 121};
		testObj.bitsPadding = 3;
		
		ASN1BitString obj = (ASN1BitString) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testByteArray() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {4, 5, 3, 21, 55, 43, 76};
		byte[] testObj = new byte[] {3, 21, 55, 43, 76};
		
		byte[] obj = (byte[]) ASN1InputStream.decode(bytes);
		assertTrue(Arrays.equals(obj, testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testUTF8String() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {12, 30, 66, 106, (byte)195, (byte)182, 114, 110, 45, 84, 104, 97, 100, 100, (byte)195, (byte)164, 117, 115, 32, 68, (byte)195, (byte)188, 110, 103, 101, 114, 115, 116, 114, 97, (byte)195, (byte)159};
		String testObj = "Björn-Thaddäus Düngerstraß";
		
		String obj = (String) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

	@Test
	public void testBMPString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {30, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1BMPString obj = (ASN1BMPString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testIA5String() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {22, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1IA5String obj = (ASN1IA5String) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testGeneralString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {27, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1GeneralString obj = (ASN1GeneralString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testGraphicString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {25, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1GraphicString obj = (ASN1GraphicString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testNumericString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {18, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1NumericString obj = (ASN1NumericString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testPrintableString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {19, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1PrintableString obj = (ASN1PrintableString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testTeletexString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {20, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1TeletexString obj = (ASN1TeletexString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testUniversalString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {28, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1UniversalString obj = (ASN1UniversalString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testVideotexString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {21, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1VideotexString obj = (ASN1VideotexString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}
	@Test
	public void testVisibleString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {26, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100};
		String testObj = "hello world";
		
		ASN1VisibleString obj = (ASN1VisibleString) ASN1InputStream.decode(bytes);
		assertTrue(obj.string.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(obj);
		assertTrue(Arrays.equals(bytes, newBytes));
	}

}
