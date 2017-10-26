package club.callistohouse.asn1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class TestASN1Constructed {

	@Test
	public void testConstructedBitString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {35, 9, 3, 3, 0, 110, 93, 3, 2, 6, (byte)192};
		ASN1BitString testObj = new ASN1BitString();
		testObj.bytes = new byte[] {(byte) 110, 93, (byte)192};
		testObj.bitsPadding = 6;
		
		ASN1BitString obj = (ASN1BitString) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
	}

	@Test
	public void testConstructedByteArray() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {36, 12, 4, 4, 1, 35, 69, 103, 4, 4, (byte)137, (byte)171, (byte)205, (byte)239};
		byte[] testObj = new byte[] {1, 35, 69, 103, (byte)137, (byte)171, (byte)205, (byte)239};
		
		byte[] obj = (byte[]) ASN1InputStream.decode(bytes);
		assertTrue(Arrays.equals(obj, testObj));
	}

	@Test
	public void testConstructedUtf8String1() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {44, 15, 12, 5, 84, 101, 115, 116, 32, 12, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = (String) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
	}

	@Test
	public void testConstructedUtf8String2() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {44, 32, 12, 30, 66, 106, (byte)195, (byte)182, 114, 110, 45, 84, 104, 97, 100, 100, (byte)195, (byte)164, 117, 115, 32, 68, (byte)195, (byte)188, 110, 103, 101, 114, 115, 116, 114, 97, (byte)195, (byte)159};
		String testObj = "Björn-Thaddäus Düngerstraß";
		
		String obj = (String) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
	}

	@Test
	public void testConstructedUtf8String3() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {44, 34, 12, 25, 66, 106, (byte)195, (byte)182, 114, 110, 45, 84, 104, 97, 100, 100, (byte)195, (byte)164, 117, 115, 32, 68, (byte)195, (byte)188, 110, 103, 101, 114, 115, 12, 5, 116, 114, 97, (byte)195, (byte)159};
		String testObj = "Björn-Thaddäus Düngerstraß";
		
		String obj = (String) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
	}

	@Test
	public void testIndefiniteLengthSequence() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {48, (byte)128, 1, 1, (byte)255, 2, 2, 3, 21, 0, 0};
		List testObj = new ArrayList();
		testObj.add(true);
		testObj.add(BigInteger.valueOf(789));
		
		List obj = (List) ASN1InputStream.decode(bytes);
		assertTrue(obj.equals(testObj));
	}

	@Test
	public void testConstructedBMPString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {62, 15, 30, 5, 84, 101, 115, 116, 32, 30, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1BMPString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedGeneralString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {59, 15, 27, 5, 84, 101, 115, 116, 32, 27, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1GeneralString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedGraphicString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {57, 15, 25, 5, 84, 101, 115, 116, 32, 25, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1GraphicString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedIA5String() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {54, 15, 22, 5, 84, 101, 115, 116, 32, 22, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1IA5String) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedNumericString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {50, 15, 18, 5, 84, 101, 115, 116, 32, 18, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1NumericString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedPrintableString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {51, 15, 19, 5, 84, 101, 115, 116, 32, 19, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1PrintableString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedTeletexString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {52, 15, 20, 5, 84, 101, 115, 116, 32, 20, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1TeletexString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedUniversalString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {60, 15, 28, 5, 84, 101, 115, 116, 32, 28, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1UniversalString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedVideotexString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {53, 15, 21, 5, 84, 101, 115, 116, 32, 21, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1VideotexString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
	@Test
	public void testConstructedVisibleString() throws IOException, InstantiationException, IllegalAccessException {
		byte[] bytes = new byte[] {58, 15, 26, 5, 84, 101, 115, 116, 32, 26, 6, 85, 115, 101, 114, 32, 49};
		String testObj = "Test User 1";
		
		String obj = ((ASN1VisibleString) ASN1InputStream.decode(bytes)).string;
		assertTrue(obj.equals(testObj));
	}
}
