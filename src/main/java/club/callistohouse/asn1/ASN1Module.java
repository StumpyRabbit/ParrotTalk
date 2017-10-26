package club.callistohouse.asn1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import club.callistohouse.asn1.objects.ASN1BMPString;
import club.callistohouse.asn1.objects.ASN1BitString;
import club.callistohouse.asn1.objects.ASN1GeneralString;
import club.callistohouse.asn1.objects.ASN1GraphicString;
import club.callistohouse.asn1.objects.ASN1IA5String;
import club.callistohouse.asn1.objects.ASN1ISO646String;
import club.callistohouse.asn1.objects.ASN1NumericString;
import club.callistohouse.asn1.objects.ASN1ObjectId;
import club.callistohouse.asn1.objects.ASN1PrintableString;
import club.callistohouse.asn1.objects.ASN1T61String;
import club.callistohouse.asn1.objects.ASN1TeletexString;
import club.callistohouse.asn1.objects.ASN1UniversalString;
import club.callistohouse.asn1.objects.ASN1Value;
import club.callistohouse.asn1.objects.ASN1VideotexString;
import club.callistohouse.asn1.objects.ASN1VisibleString;
import club.callistohouse.asn1.objects.OrderedSet;
import club.callistohouse.asn1.types.ASN1AssignmentSubType;
import club.callistohouse.asn1.types.ASN1Entity;
import club.callistohouse.asn1.types.ASN1ExplicitSubType;
import club.callistohouse.asn1.types.ASN1ImplicitSubType;
import club.callistohouse.asn1.types.ASN1MappingSubType;
import club.callistohouse.asn1.types.ASN1Type;
import club.callistohouse.asn1.types.ASN1TypeReference;
import club.callistohouse.asn1.types.ASN1WrapperConstructedType;
import club.callistohouse.asn1.types.ASN1WrapperPrimitiveType;
import club.callistohouse.asn1.types.basic.ASN1AnyDefinedByType;
import club.callistohouse.asn1.types.basic.ASN1AnyType;
import club.callistohouse.asn1.types.basic.ASN1BMPStringType;
import club.callistohouse.asn1.types.basic.ASN1BigIntegerType;
import club.callistohouse.asn1.types.basic.ASN1BitStringType;
import club.callistohouse.asn1.types.basic.ASN1BooleanType;
import club.callistohouse.asn1.types.basic.ASN1ByteArrayType;
import club.callistohouse.asn1.types.basic.ASN1GeneralStringType;
import club.callistohouse.asn1.types.basic.ASN1GeneralizedTimeType;
import club.callistohouse.asn1.types.basic.ASN1GraphicStringType;
import club.callistohouse.asn1.types.basic.ASN1IA5StringType;
import club.callistohouse.asn1.types.basic.ASN1ISO646StringType;
import club.callistohouse.asn1.types.basic.ASN1IntegerType;
import club.callistohouse.asn1.types.basic.ASN1NullType;
import club.callistohouse.asn1.types.basic.ASN1NumericStringType;
import club.callistohouse.asn1.types.basic.ASN1ObjectIdentifierType;
import club.callistohouse.asn1.types.basic.ASN1PrintableStringType;
import club.callistohouse.asn1.types.basic.ASN1RealType;
import club.callistohouse.asn1.types.basic.ASN1T61StringType;
import club.callistohouse.asn1.types.basic.ASN1TeletexStringType;
import club.callistohouse.asn1.types.basic.ASN1UTCTimeType;
import club.callistohouse.asn1.types.basic.ASN1UTF8StringType;
import club.callistohouse.asn1.types.basic.ASN1UniversalStringType;
import club.callistohouse.asn1.types.basic.ASN1VideotexStringType;
import club.callistohouse.asn1.types.basic.ASN1VisibleStringType;
import club.callistohouse.asn1.types.constructed.ASN1ChoiceType;
import club.callistohouse.asn1.types.constructed.ASN1MappedSequenceType;
import club.callistohouse.asn1.types.constructed.ASN1MappedSetType;
import club.callistohouse.asn1.types.constructed.ASN1SequenceOfType;
import club.callistohouse.asn1.types.constructed.ASN1SequenceType;
import club.callistohouse.asn1.types.constructed.ASN1SetOfType;
import club.callistohouse.asn1.types.constructed.ASN1SetType;

public class ASN1Module {
	/** static methods */
	private static List<ASN1Module> modules = new ArrayList<ASN1Module>();
	public static ASN1Module name(String modName) {
		for(ASN1Module mod : modules) {
			if(mod.moduleName.equals(modName)) {
				return mod;
			}
		}
		ASN1Module mod = new ASN1Module();
		mod.moduleName = modName;
		modules.add(mod);
		return mod;
	}

	public static Map<Integer,ASN1Type> tagToTypeTable = new HashMap<Integer,ASN1Type>();
	static {
		tagToTypeTable.put(1,new ASN1BooleanType());
		tagToTypeTable.put(11022,new ASN1IntegerType());
		tagToTypeTable.put(2,new ASN1BigIntegerType());
		tagToTypeTable.put(3,new ASN1BitStringType());
		tagToTypeTable.put(4,new ASN1ByteArrayType());
		tagToTypeTable.put(5,new ASN1NullType());
		tagToTypeTable.put(6,new ASN1ObjectIdentifierType());
		tagToTypeTable.put(9,new ASN1RealType());
		tagToTypeTable.put(12,new ASN1UTF8StringType());
		tagToTypeTable.put(16,new ASN1SequenceType());
		tagToTypeTable.put(17,new ASN1SetType());
		tagToTypeTable.put(18,new ASN1NumericStringType());
		tagToTypeTable.put(19,new ASN1PrintableStringType());
		tagToTypeTable.put(20,new ASN1TeletexStringType());
		tagToTypeTable.put(21,new ASN1VideotexStringType());
		tagToTypeTable.put(22,new ASN1IA5StringType());
		tagToTypeTable.put(23,new ASN1UTCTimeType());
		tagToTypeTable.put(24,new ASN1GeneralizedTimeType());
		tagToTypeTable.put(25,new ASN1GraphicStringType());
		tagToTypeTable.put(26,new ASN1VisibleStringType());
		tagToTypeTable.put(27,new ASN1GeneralStringType());
		tagToTypeTable.put(28,new ASN1UniversalStringType());
		tagToTypeTable.put(30,new ASN1BMPStringType());
	}
	public static Map<Class<?>,ASN1Type> classToTypeTable = new HashMap<Class<?>,ASN1Type>();
	static {
		classToTypeTable.put(boolean.class, new ASN1BooleanType());
		classToTypeTable.put(Boolean.class, new ASN1BooleanType());
		classToTypeTable.put(int.class, new ASN1IntegerType());
		classToTypeTable.put(Integer.class, new ASN1IntegerType());
		classToTypeTable.put(BigInteger.class, new ASN1BigIntegerType());
		classToTypeTable.put(float.class, new ASN1RealType());
		classToTypeTable.put(Float.class, new ASN1RealType());
		classToTypeTable.put(double.class, new ASN1RealType());
		classToTypeTable.put(Double.class, new ASN1RealType());
		classToTypeTable.put(ASN1BitString.class, new ASN1BitStringType());
		classToTypeTable.put(byte[].class, new ASN1ByteArrayType());
		classToTypeTable.put(Byte[].class, new ASN1ByteArrayType());
		classToTypeTable.put(ASN1ObjectId.class, new ASN1ObjectIdentifierType());
		classToTypeTable.put(String.class, new ASN1UTF8StringType());
		classToTypeTable.put(OrderedSet.class, new ASN1SetType());
		classToTypeTable.put(List.class, new ASN1SequenceType());
		classToTypeTable.put(Set.class, new ASN1SetType());
		classToTypeTable.put(Date.class, new ASN1UTCTimeType());

		classToTypeTable.put(ASN1NumericString.class, new ASN1NumericStringType());
		classToTypeTable.put(ASN1PrintableString.class, new ASN1PrintableStringType());
		classToTypeTable.put(ASN1TeletexString.class, new ASN1TeletexStringType());
		classToTypeTable.put(ASN1VideotexString.class, new ASN1VideotexStringType());
		classToTypeTable.put(ASN1IA5String.class, new ASN1IA5StringType());
		classToTypeTable.put(ASN1GraphicString.class, new ASN1GraphicStringType());
		classToTypeTable.put(ASN1VisibleString.class, new ASN1VisibleStringType());
		classToTypeTable.put(ASN1GeneralString.class, new ASN1GeneralStringType());
		classToTypeTable.put(ASN1UniversalString.class, new ASN1UniversalStringType());
		classToTypeTable.put(ASN1BMPString.class, new ASN1BMPStringType());
		classToTypeTable.put(ASN1T61String.class, new ASN1T61StringType());
		classToTypeTable.put(ASN1ISO646String.class, new ASN1ISO646StringType());
	}

	protected static ASN1Type typeForTag(int tag) {
		int numericTag = tag & 0x1F;
		int tagClass = tag & 0xC0;
		boolean isConstructed = (tag & 0x20) > 0;
		ASN1Type type = null;
		if(tagClass > 0) {
			if(isConstructed) {
				type  = new ASN1WrapperConstructedType();
			} else {
				type = new ASN1WrapperPrimitiveType();
			}
		} else {
			type = basicTypeForTag(numericTag);
		}
		return type;
	}
	public static ASN1Type typeForObject(Object obj) {
		Object classKey;
		if(obj == null) {
			return new ASN1NullType();
		} else if((classKey = classKeyForObject(obj)) != null) {
			return classToTypeTable.get(classKey);
		} else if(obj instanceof ASN1Value) {
			return new ASN1AnyType();
		}
		throw new RuntimeException("unknown asn1 obj class");
	}
	public static int tagForObject(Object obj) {
		return typeForObject(obj).asn1Tag();
	}
	private static Object classKeyForObject(Object obj) {
		for(Class<?> clazz : classToTypeTable.keySet()) {
			if(clazz.isInstance(obj)) { return clazz; }
		}
		return null;
	}
	public static ASN1Type basicTypeForTag(int tag) {
		return tagToTypeTable.get(tag);
	}
	public static boolean isConstructed(ASN1Type parent) {
		// TODO Auto-generated method stub
		return false;
	}

	/* instance methods */
	private String moduleName = "";
	private Map<String,ASN1Entity> definitions = new HashMap<String,ASN1Entity>();
	private Map<String,ASN1Module> imports = new HashMap<String,ASN1Module>();

	public ASN1Type typeFrom(String typeSpec) {
		return (ASN1Type) find(typeSpec);
	}
	public ASN1Type typeFrom(ASN1Type typeSpec) {
		return typeSpec;
	}
	public <T extends ASN1Type> ASN1Type typeFrom(Class<T> typeSpecClass) {
		try {
			return typeSpecClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
	public ASN1Entity find(String typeSpecName) {
		/*check the following sources:
			- ASN1Type subclasses
			- my definitions
			- import definitions
		otherwise create a ASN1TypeReference*/

		ASN1Entity type = primitiveTypeFind(typeSpecName);
		if(type != null) { return type; }
		type = localFind(typeSpecName);
		if(type != null) { return type; }
		for(ASN1Module mod : imports.values()) {
			type = mod.localFind(typeSpecName);
			if(type != null) { return type; }
		}
		return new ASN1TypeReference(typeSpecName);
	}
	private ASN1Entity localFind(String typeSpecName) {
		return definitions.get(typeSpecName);
	}
	public static ASN1Type primitiveTypeFind(String typeSpecName) {
		for(ASN1Type type : classToTypeTable.values()) {
			if(type.symbol.equals(typeSpecName)) {
				return type;
			}
		}
		return null;
	}
	public ASN1Entity add(ASN1Entity asn1Entity) {
		asn1Entity.setModule(this);
		definitions.put(asn1Entity.symbol, asn1Entity);
		return asn1Entity;
	}

	public ASN1ExplicitSubType explicitTypeTag(String typeName, ASN1Type parentType, int tag) {
		ASN1ExplicitSubType type = new ASN1ExplicitSubType();
		type.setSymbol(typeName);
		type.setParent(parentType);
		type.setTag(tag);
		add(type);
		return type;
	}
	public ASN1ImplicitSubType implicitTypeTag(String typeName, ASN1Type parentType, int tag) {
		ASN1ImplicitSubType type = new ASN1ImplicitSubType();
		type.setSymbol(typeName);
		type.setParent(parentType);
		type.setTag(tag);
		add(type);
		return type;
	}
	public ASN1AssignmentSubType assignFrom(String typeName, ASN1Type parentType) {
		ASN1AssignmentSubType type = new ASN1AssignmentSubType();
		type.setSymbol(typeName);
		type.setParent(parentType);
		add(type);
		return type;
	}
	public ASN1MappingSubType mapped(String typeName) {
		ASN1MappingSubType type = new ASN1MappingSubType();
		type.setSymbol(typeName);
		add(type);
		return type;
	}

	/* defined types */
	public ASN1AnyType any(String string) {
		ASN1AnyType type = new ASN1AnyType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1AnyDefinedByType anyDefinedBy(String string, String definedBy) {
		ASN1AnyDefinedByType type = new ASN1AnyDefinedByType();
		type.setSymbol(string);
		type.definedBy = definedBy;
		add(type);
		return type;
	}
	public ASN1BitStringType bitString(String string) {
		ASN1BitStringType type = new ASN1BitStringType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1BooleanType booleanType(String string) {
		ASN1BooleanType type = new ASN1BooleanType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1ByteArrayType byteArray(String string) {
		ASN1ByteArrayType type = new ASN1ByteArrayType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1ChoiceType choice(String string) {
		ASN1ChoiceType type = new ASN1ChoiceType(string);
		add(type);
		return type;
	}
	public ASN1IntegerType integer(String string) {
		ASN1IntegerType type = new ASN1IntegerType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1NullType nullType(String string) {
		ASN1NullType type = new ASN1NullType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1ObjectIdentifierType objectIdentifier(String string) {
		ASN1ObjectIdentifierType type = new ASN1ObjectIdentifierType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1UTCTimeType utcTime(String string) {
		ASN1UTCTimeType type = new ASN1UTCTimeType();
		type.setSymbol(string);
		add(type);
		return type;
	}
	public ASN1UTF8StringType utf8String(String string) {
		ASN1UTF8StringType type = new ASN1UTF8StringType();
		type.setSymbol(string);
		add(type);
		return type;
	}

	public ASN1SequenceType sequence(String string) {
		ASN1SequenceType seq = new ASN1SequenceType();
		seq.setSymbol(string);
		add(seq);
		return seq;
	}
	public <M> ASN1MappedSequenceType<M> sequenceMappingClass(String string, Class<M> mappingClass) {
		ASN1MappedSequenceType<M> type = new ASN1MappedSequenceType<M>(string, mappingClass);
		add(type);
		return type;
	}
	public ASN1SequenceOfType sequenceOf(String string, ASN1Type asn1Type) {
		ASN1SequenceOfType seq = new ASN1SequenceOfType();
		seq.setSymbol(string);
		seq.elementType = asn1Type;
		add(seq);
		return seq;
	}
	public ASN1SetType set(String string) {
		ASN1SetType seq = new ASN1SetType();
		seq.setSymbol(string);
		add(seq);
		return seq;
	}
	public <M> ASN1MappedSetType<M> setMappingClass(String string, Class<M> mappingClass) {
		ASN1MappedSetType<M> type = new ASN1MappedSetType<M>(string, mappingClass);
		add(type);
		return type;
	}
	public ASN1SetOfType setOf(String string, ASN1Type asn1Type) {
		ASN1SetOfType seq = new ASN1SetOfType();
		seq.setSymbol(string);
		seq.elementType = asn1Type;
		add(seq);
		return seq;
	}
}
