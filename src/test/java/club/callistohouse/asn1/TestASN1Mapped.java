package club.callistohouse.asn1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import club.callistohouse.asn1.types.ASN1Type;
import club.callistohouse.asn1.types.constructed.ASN1ChoiceType;

public class TestASN1Mapped {

	@Test
	public void testMappedChoiceType() throws IOException, InstantiationException, IllegalAccessException {
		ASN1ChoiceType choice = ASN1Module.name("test").choice("TestChoice");
		choice.addTypeString("boolean", "ASN1BooleanType");
		choice.addTypeString("integer", "ASN1IntegerType");
		ASN1Module.name("test").sequenceMappingClass("TestChoiceSequence", ASN1TestModel.class)
			.addTypeString("testSlot1", "TestChoice");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestChoiceSequence");
		byte[] bytes = new byte[] {48, 3, 1, 1, (byte)255};
		ASN1TestModel explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue(explicit.testSlot1 instanceof Boolean);
		assertTrue((Boolean)explicit.testSlot1);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));

		bytes = new byte[] {48, 4, 2, 2, 3, 21};
		explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue(explicit.testSlot1 instanceof Integer);
		assertTrue((Integer)explicit.testSlot1 == 789);
		newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testMappedSequenceType() throws IOException, InstantiationException, IllegalAccessException {
		ASN1Module.name("test").sequenceMappingClass("TestMapped", ASN1TestModel.class)
			.addTypeString("testSlot1", "ASN1BooleanType");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestMapped");
		byte[] bytes = new byte[] {48, 3, 1, 1, (byte)255};
		ASN1TestModel explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue(explicit.testSlot1 instanceof Boolean);
		assertTrue((Boolean)explicit.testSlot1);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testMappedSequenceType1() throws IOException, InstantiationException, IllegalAccessException {
		ASN1Module.name("test").sequenceMappingClass("TestSequence", ASN1TestModel.class)
			.addTypeString("testSlot1", "ASN1BooleanType");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSequence");
		byte[] bytes = new byte[] {48, 3, 1, 1, (byte)255};
		ASN1TestModel explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue(explicit.testSlot1 instanceof Boolean);
		assertTrue((Boolean)explicit.testSlot1);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testMappedSequenceType1Default() throws IOException, InstantiationException, IllegalAccessException {
		ASN1Module.name("test").sequenceMappingClass("TestSequence", ASN1TestModel.class)
			.addTypeStringDefault("testSlot1", "ASN1BooleanType", false);

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSequence");
		byte[] bytes = new byte[] {48, 0};
		ASN1TestModel explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue(explicit.testSlot1 instanceof Boolean);
		assertTrue(!(Boolean)explicit.testSlot1);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

	@Test
	public void testMappedSequenceType1Optional() throws IOException, InstantiationException, IllegalAccessException {
		ASN1Module.name("test").sequenceMappingClass("TestSequence", ASN1TestModel.class)
			.addTypeStringOptional("testSlot1", "ASN1BooleanType");

		ASN1Type type = (ASN1Type) ASN1Module.name("test").find("TestSequence");
		byte[] bytes = new byte[] {48, 0};
		ASN1TestModel explicit = (ASN1TestModel)ASN1InputStream.decode(bytes, type);
		assertTrue(explicit instanceof ASN1TestModel);
		assertTrue((Boolean)explicit.testSlot1 == null);
		byte[] newBytes = ASN1OutputStream.encodeObject(explicit, type);
		assertTrue(Arrays.equals(bytes,  newBytes));
	}

}
