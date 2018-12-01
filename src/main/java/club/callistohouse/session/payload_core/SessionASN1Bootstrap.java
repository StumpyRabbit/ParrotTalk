package club.callistohouse.session.payload_core;

import club.callistohouse.asn1.ASN1Module;
import club.callistohouse.asn1.types.basic.ASN1UTF8StringType;
import club.callistohouse.asn1.types.constructed.ASN1ChoiceType;
import club.callistohouse.asn1.types.constructed.ASN1MappedSequenceType;
import club.callistohouse.asn1.types.constructed.ASN1SequenceOfType;
import club.callistohouse.session.payload_v3_6.GiveInfo;
import club.callistohouse.session.payload_v3_6.Go;
import club.callistohouse.session.payload_v3_6.GoToo;
import club.callistohouse.session.payload_v3_6.IAm;
import club.callistohouse.session.payload_v3_6.IWant;
import club.callistohouse.session.payload_v3_6.ReplyInfo;
import club.callistohouse.session.payload_v3_7.Hello_v3_7;
import club.callistohouse.session.payload_v3_7.Response_v3_7;
import club.callistohouse.session.payload_v3_7.Signature_v3_7;

public class SessionASN1Bootstrap {

	public static void bootstrap() {
		ASN1Module.name("Session");
		defineASN1RSAPublicKey();
		defineASN1ProtocolOffered();
		defineASN1ProtocolAccepted();
		defineASN1Encoded();
		defineASN1Encrypted();
		defineASN1MAC();
		defineASN1IWant();
		defineASN1IAm();
		defineASN1GiveInfo();
		defineASN1ReplyInfo();
		defineASN1Go();
		defineASN1GoToo();
		defineASN1NotMe();
		defineASN1DupConn();
		defineASN1RawData();
		defineASN1Hello_v3_7();
		defineASN1Response_v3_7();
		defineASN1Signature_v3_7();
		defineASN1InternalChangeEncryption();
		defineASN1PhaseHeaderChoice();
	}
	private static void defineASN1RSAPublicKey() {
		ASN1MappedSequenceType<RSAPublicKey> type = ASN1Module.name("Session").sequenceMappingClass("RSAPublicKey", RSAPublicKey.class);
		type.addTypeString("modulo", "ASN1BigIntegerType");
		type.addTypeString("exponent", "ASN1BigIntegerType");
	}
	
	private static void defineASN1ProtocolOffered() {
		ASN1MappedSequenceType<ProtocolOffered> type = ASN1Module.name("Session").sequenceMappingClass("ProtocolOffered", ProtocolOffered.class);
		type.addTypeString("offered", "ASN1UTF8StringType");
		type.addTypeString("preferred", "ASN1UTF8StringType");
	}
	
	private static void defineASN1ProtocolAccepted() {
		ASN1MappedSequenceType<ProtocolAccepted> type = ASN1Module.name("Session").sequenceMappingClass("ProtocolAccepted", ProtocolAccepted.class);
		type.addTypeString("accepted", "ASN1UTF8StringType");
	}

	@SuppressWarnings("unused")
	private static void defineASN1Encoded() {
		ASN1MappedSequenceType<Encoded> type = ASN1Module.name("Session").sequenceMappingClass("Encoded", Encoded.class);
	}
	private static void defineASN1Encrypted() {
		ASN1MappedSequenceType<Encrypted> type = ASN1Module.name("Session").sequenceMappingClass("Encrypted", Encrypted.class);
		type.addTypeString("ivSequence", "ASN1ByteArrayType");
	}
	private static void defineASN1MAC() {
		ASN1MappedSequenceType<MAC> type = ASN1Module.name("Session").sequenceMappingClass("MAC", MAC.class);
		type.addTypeString("mac", "ASN1ByteArrayType");
	}

	private static void defineASN1IWant() {
		ASN1MappedSequenceType<IWant> type = ASN1Module.name("Session").sequenceMappingClass("IWant", IWant.class);
		type.addTypeString("vatId", "ASN1UTF8StringType");
		type.addTypeString("domain", "ASN1UTF8StringType");
	}

	private static void defineASN1IAm() {
		ASN1MappedSequenceType<IAm> type = ASN1Module.name("Session").sequenceMappingClass("IAm", IAm.class);
		type.addTypeString("vatId", "ASN1UTF8StringType");
		type.addTypeString("domain", "ASN1UTF8StringType");
		type.addTypeString("publicKey", "RSAPublicKey");
	}

	private static void defineASN1GiveInfo() {
		ASN1MappedSequenceType<GiveInfo> type = ASN1Module.name("Session").sequenceMappingClass("GiveInfo", GiveInfo.class);
		type.addTypeString("vatId", "ASN1UTF8StringType");
		type.addTypeString("domain", "ASN1UTF8StringType");
		type.addTypeString("publicKey", "RSAPublicKey");
	}

	private static void defineASN1ReplyInfo() {
		ASN1SequenceOfType seqType = ASN1Module.name("Session").sequenceOf("SequenceOfString", new ASN1UTF8StringType());
		ASN1MappedSequenceType<ReplyInfo> type = ASN1Module.name("Session").sequenceMappingClass("ReplyInfo", ReplyInfo.class);
		type.addTypeString("cryptoProtocols", "SequenceOfString");
		type.addTypeString("dataEncoders", "SequenceOfString");
	}

	private static void defineASN1Go() {
		ASN1MappedSequenceType<Go> type = ASN1Module.name("Session").sequenceMappingClass("Go", Go.class);
		type.addTypeString("cryptoProtocol", "ASN1UTF8StringType");
		type.addTypeString("dataEncoder", "ASN1UTF8StringType");
		type.addTypeString("diffieHellmanParam", "ASN1ByteArrayType");
		type.addTypeString("signature", "ASN1ByteArrayType");
	}

	private static void defineASN1GoToo() {
		ASN1MappedSequenceType<GoToo> type = ASN1Module.name("Session").sequenceMappingClass("GoToo", GoToo.class);
		type.addTypeString("cryptoProtocol", "ASN1UTF8StringType");
		type.addTypeString("dataEncoder", "ASN1UTF8StringType");
		type.addTypeString("diffieHellmanParam", "ASN1ByteArrayType");
		type.addTypeString("signature", "ASN1ByteArrayType");
	}

	private static void defineASN1Hello_v3_7() {
		ASN1MappedSequenceType<Hello_v3_7> type = ASN1Module.name("Session").sequenceMappingClass("Hello_v3_7", Hello_v3_7.class);
		type.addTypeString("vatId", "ASN1UTF8StringType");
		type.addTypeString("domain", "ASN1UTF8StringType");
		type.addTypeString("publicKey", "RSAPublicKey");
		type.addTypeString("cryptoProtocols", "SequenceOfString");
		type.addTypeString("dataEncoders", "SequenceOfString");
		type.addTypeString("diffieHellmanParam", "ASN1ByteArrayType");
	}

	private static void defineASN1Response_v3_7() {
		ASN1MappedSequenceType<Response_v3_7> type = ASN1Module.name("Session").sequenceMappingClass("Response_v3_7", Response_v3_7.class);
		type.addTypeString("vatId", "ASN1UTF8StringType");
		type.addTypeString("domain", "ASN1UTF8StringType");
		type.addTypeString("publicKey", "RSAPublicKey");
		type.addTypeString("cryptoProtocol", "ASN1UTF8StringType");
		type.addTypeString("dataEncoder", "ASN1UTF8StringType");
		type.addTypeString("diffieHellmanParam", "ASN1ByteArrayType");
		type.addTypeString("signature", "ASN1ByteArrayType");
	}

	private static void defineASN1Signature_v3_7() {
		ASN1MappedSequenceType<Signature_v3_7> type = ASN1Module.name("Session").sequenceMappingClass("Signature_v3_7", Signature_v3_7.class);
		type.addTypeString("signature", "ASN1ByteArrayType");
	}

	@SuppressWarnings("unused")
	private static void defineASN1NotMe() {
		ASN1MappedSequenceType<NotMe> type = ASN1Module.name("Session").sequenceMappingClass("NotMe", NotMe.class);
	}

	@SuppressWarnings("unused")
	private static void defineASN1DupConn() {
		ASN1MappedSequenceType<DuplicateConnection> type = ASN1Module.name("Session").sequenceMappingClass("DuplicateConnection", DuplicateConnection.class);
	}

	@SuppressWarnings("unused")
	private static void defineASN1RawData() {
		ASN1MappedSequenceType<RawData> type = ASN1Module.name("Session").sequenceMappingClass("RawData", RawData.class);
	}

	@SuppressWarnings("unused")
	private static void defineASN1InternalChangeEncryption() {
		ASN1MappedSequenceType<InternalChangeEncryption> type = ASN1Module.name("Session").sequenceMappingClass("InternalChangeEncryption", InternalChangeEncryption.class);
	}

	private static void defineASN1PhaseHeaderChoice() {
		ASN1ChoiceType type = ASN1Module.name("Session").choice("PhaseHeader");
		type.addTypeStringExplicit("offered", "ProtocolOffered", new ProtocolOffered().getId());
		type.addTypeStringExplicit("accepted", "ProtocolAccepted", new ProtocolAccepted().getId());
		type.addTypeStringExplicit("encoded", "Encoded", new Encoded().getId());
		type.addTypeStringExplicit("encrypted", "Encrypted", new Encrypted().getId());
		type.addTypeStringExplicit("mac", "MAC", new MAC().getId());
		type.addTypeStringExplicit("i-want", "IWant", new IWant().getId());
		type.addTypeStringExplicit("i-am", "IAm", new IAm().getId());
		type.addTypeStringExplicit("give-info", "GiveInfo", new GiveInfo().getId());
		type.addTypeStringExplicit("reply-info", "ReplyInfo", new ReplyInfo().getId());
		type.addTypeStringExplicit("go", "Go", new Go().getId());
		type.addTypeStringExplicit("go-too", "GoToo", new GoToo().getId());
		type.addTypeStringExplicit("not-me", "NotMe", new NotMe().getId());
		type.addTypeStringExplicit("duplicate-connection", "DuplicateConnection", new DuplicateConnection().getId());
		type.addTypeStringExplicit("raw-data", "RawData", new RawData().getId());
		type.addTypeStringExplicit("hello-v3.7", "RawData", new Hello_v3_7().getId());
		type.addTypeStringExplicit("response-v3.7", "RawData", new Response_v3_7().getId());
		type.addTypeStringExplicit("signature-v3.7", "RawData", new Signature_v3_7().getId());
		type.addTypeStringExplicit("internal-change-encryption", "InternalChangeEncryption", new InternalChangeEncryption().getId());
	}
}
