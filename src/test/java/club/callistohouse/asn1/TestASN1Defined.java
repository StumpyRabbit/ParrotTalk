package club.callistohouse.asn1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import club.callistohouse.asn1.types.ASN1Type;
import club.callistohouse.asn1.types.basic.ASN1BooleanType;
import club.callistohouse.asn1.types.basic.ASN1IntegerType;
import club.callistohouse.asn1.types.constructed.ASN1SequenceType;

public class TestASN1Defined {
	@Test
	public void testDefinedExplicitType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").explicitTypeTag("TestExplicit", new ASN1IntegerType(), 0);

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestExplicit");
		byte[] bytes = new byte[] {(byte)160, 4, 2, 2, 3, 21};
		Integer explicit = (Integer)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit == 789);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedImplicitConstructedType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").implicitTypeTag("TestImplicit", new ASN1SequenceType(), 0);

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestImplicit");
		byte[] bytes = new byte[] {(byte)160, 4, 2, 2, 3, 21};
		List explicit = (List)ASN1InputStream.decode(bytes, type);
		Object testObj = Arrays.asList(BigInteger.valueOf(789));
		assertTrue(explicit.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedImplicitPrimitiveType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").implicitTypeTag("TestImplicit", new ASN1IntegerType(), 0);

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestImplicit");
		byte[] bytes = new byte[] {(byte)128, 2, 3, 21};
		Integer explicit = (Integer)ASN1InputStream.decode(bytes, type);
		Object testObj = 789;
		assertTrue(explicit.equals(testObj));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedSequenceOfType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").sequenceOf("TestSequence", new ASN1BooleanType());

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSequence");
		byte[] bytes = new byte[] {48, 3, 1, 1, (byte)255};
		List explicit = (List)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof List);
		assertTrue((Boolean)explicit.get(0));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedSetOfType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").setOf("TestSet", new ASN1BooleanType());

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSet");
		byte[] bytes = new byte[] {49, 3, 1, 1, (byte)255};
		Set explicit = (Set)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof Set);
		assertTrue(explicit.contains(true));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedUnmappedSequenceType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").sequence("TestSequence");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSequence");
		byte[] bytes = new byte[] {48, 3, 1, 1, (byte)255};
		List explicit = (List)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof List);
		assertTrue((Boolean)explicit.get(0));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testDefinedUnmappedSetType() throws InstantiationException, IllegalAccessException, IOException {
		ASN1Module.name("test").set("TestSet");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSet");
		byte[] bytes = new byte[] {49, 3, 1, 1, (byte)255};
		Set explicit = (Set)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof Set);
		assertTrue(explicit.contains(true));
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}
}
