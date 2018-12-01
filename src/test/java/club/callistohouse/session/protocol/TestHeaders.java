package club.callistohouse.session.protocol;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1Module;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1Type;
import club.callistohouse.session.payload_core.Frame;
import club.callistohouse.session.payload_core.ProtocolOffered;
import club.callistohouse.session.payload_core.SessionASN1Bootstrap;

public class TestHeaders {
	@Before
	public void setup() throws UnknownHostException {
		PropertyConfigurator.configure("log4j.properties");
		SessionASN1Bootstrap.bootstrap();
	}

	@Test
	public void testHeaders() throws IOException, InstantiationException, IllegalAccessException {

		ASN1Type type = (ASN1Type) ASN1Module.name("Session").find("PhaseHeader");
		ProtocolOffered offered = new ProtocolOffered();
		offered.setOffered("Hello");
		offered.setPreferred("Hello, World");
		
		byte[] bytes = ASN1OutputStream.encodeObject(offered, type);
		ProtocolOffered off2 = (ProtocolOffered) ASN1InputStream.decode(bytes, type);
		
		assertTrue(off2.getOffered().equals(offered.getOffered()));
		assertTrue(off2.getPreferred().equals(offered.getPreferred()));
	}

	@Test
	public void testFrameHeaders() throws IOException, InstantiationException, IllegalAccessException {

		ASN1Type type = (ASN1Type) ASN1Module.name("Session").find("PhaseHeader");
		ProtocolOffered offered = new ProtocolOffered();
		offered.setOffered("Hello");
		offered.setPreferred("Hello, World");
		Frame frame = offered.toFrame();

		byte[] bytes = frame.toByteArray();
		Frame newFrame = Frame.onFrameSpecification(Arrays.copyOfRange(bytes, 0, 8));
		newFrame.readRemainderFrom(new ByteArrayInputStream((Arrays.copyOfRange(bytes, 8, bytes.length))));

		assertTrue(frame.getHeader() instanceof ProtocolOffered);
		assertTrue(((ProtocolOffered) frame.getHeader()).getOffered().equals(((ProtocolOffered) newFrame.getHeader()).getOffered()));
	}
}
