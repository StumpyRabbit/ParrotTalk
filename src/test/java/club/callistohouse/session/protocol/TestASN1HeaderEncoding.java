package club.callistohouse.session.protocol;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import club.callistohouse.session.marshmuck.DiffieHellman;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.GiveInfo;
import club.callistohouse.session.payload.Go;
import club.callistohouse.session.payload.GoToo;
import club.callistohouse.session.payload.IAm;
import club.callistohouse.session.payload.IWant;
import club.callistohouse.session.payload.ReplyInfo;
import club.callistohouse.session.payload.SessionASN1Bootstrap;

public class TestASN1HeaderEncoding {
	@Before
	public void setup() throws UnknownHostException {
		PropertyConfigurator.configure("log4j.properties");
		SessionASN1Bootstrap.bootstrap();
	}

	@Test
	public void testIWant() throws InstantiationException, IllegalAccessException, IOException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 65, 0, 0, 0, 31, (byte)168, 21, 48, 19, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111};
		IWant hdr = new IWant();
		hdr.setVatId("1234567890");
		hdr.setDomain("hello");
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		IWant in = (IWant) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getVatId().equals(hdr.getVatId()));
		assertTrue(in.getDomain().equals(hdr.getDomain()));
	}
	@Test
	public void testIAm() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 73, 0, 0, 1, 45, -87, -127, 34, 48, -127, 31, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111, 48, -127, 9, 2, 3, 1, 0, 1, 2, -126, 0, 1, 115, -25, -103, -69, 59, -121, -52, 18, 127, 9, 12, 71, -26, 103, 111, -83, -118, -49, -60, -17, 59, 28, 52, -113, 84, -100, -57, -103, 38, 6, 5, -85, 95, 32, 86, -30, -36, 15, 113, 75, -37, 7, -25, 107, 127, -105, -125, -89, 61, 47, -2, 81, 15, -102, -7, -126, 72, 109, 9, -18, 96, -93, -90, -10, -79, 27, 120, -21, -92, -71, 46, -61, 118, -119, 64, 96, 5, 6, 114, -121, 40, -126, -44, 7, 122, 117, -46, -11, 75, 11, 96, 124, 58, -5, 42, -61, 125, -6, -50, 101, 83, 61, 31, -112, 103, 79, 70, 23, -2, -112, -111, 119, -21, -69, -12, -92, 75, 53, 41, -65, 97, -102, 47, 86, 87, -113, -63, 82, -122, 68, 40, -125, -108, 65, -45, 9, -27, 16, -57, 95, -54, 97, -12, -117, -57, -37, 4, 86, -26, -92, -13, 111, 16, -5, 29, -119, 99, -91, -22, 109, -82, -115, 111, 81, 12, 112, 71, -103, -36, 88, -40, -76, 91, 87, 57, -6, 118, 102, 81, -117, -97, -14, 4, 84, 100, 13, -30, -85, -25, -103, -38, 77, -99, 61, -86, 119, -34, 28, 84, 110, -54, -42, -81, -121, -13, 39, -82, 31, 126, -33, 39, 44, -7, -62, -69, 127, 53, 122, 124, 55, 25, -34, 42, -32, 98, -121, -88, 13, 109, -54, -89, 18, 37, 6, -52, -45, 46, 102, 48, 10, 3, -111, -67, 10, -56, -111, 85, -119, -64, 109, -11, -78, -101, -101, 119, 25};
		IAm hdr = new IAm();
		hdr.setVatId("1234567890");
		hdr.setDomain("hello");
		hdr.setPublicKeyImpl(buildSampleVatId1PublicKey());
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		IAm in = (IAm) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getVatId().equals(hdr.getVatId()));
		assertTrue(in.getDomain().equals(hdr.getDomain()));
	}
	@Test
	public void testGiveInfo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 81, 0, 0, 1, 45, -86, -127, 34, 48, -127, 31, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111, 48, -127, 9, 2, 3, 1, 0, 1, 2, -126, 0, 1, 115, -25, -103, -69, 59, -121, -52, 18, 127, 9, 12, 71, -26, 103, 111, -83, -118, -49, -60, -17, 59, 28, 52, -113, 84, -100, -57, -103, 38, 6, 5, -85, 95, 32, 86, -30, -36, 15, 113, 75, -37, 7, -25, 107, 127, -105, -125, -89, 61, 47, -2, 81, 15, -102, -7, -126, 72, 109, 9, -18, 96, -93, -90, -10, -79, 27, 120, -21, -92, -71, 46, -61, 118, -119, 64, 96, 5, 6, 114, -121, 40, -126, -44, 7, 122, 117, -46, -11, 75, 11, 96, 124, 58, -5, 42, -61, 125, -6, -50, 101, 83, 61, 31, -112, 103, 79, 70, 23, -2, -112, -111, 119, -21, -69, -12, -92, 75, 53, 41, -65, 97, -102, 47, 86, 87, -113, -63, 82, -122, 68, 40, -125, -108, 65, -45, 9, -27, 16, -57, 95, -54, 97, -12, -117, -57, -37, 4, 86, -26, -92, -13, 111, 16, -5, 29, -119, 99, -91, -22, 109, -82, -115, 111, 81, 12, 112, 71, -103, -36, 88, -40, -76, 91, 87, 57, -6, 118, 102, 81, -117, -97, -14, 4, 84, 100, 13, -30, -85, -25, -103, -38, 77, -99, 61, -86, 119, -34, 28, 84, 110, -54, -42, -81, -121, -13, 39, -82, 31, 126, -33, 39, 44, -7, -62, -69, 127, 53, 122, 124, 55, 25, -34, 42, -32, 98, -121, -88, 13, 109, -54, -89, 18, 37, 6, -52, -45, 46, 102, 48, 10, 3, -111, -67, 10, -56, -111, 85, -119, -64, 109, -11, -78, -101, -101, 119, 25};
		GiveInfo hdr = new GiveInfo();
		hdr.setVatId("1234567890");
		hdr.setDomain("hello");
		hdr.setPublicKeyImpl(buildSampleVatId1PublicKey());
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		GiveInfo in = (GiveInfo) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getVatId().equals(hdr.getVatId()));
		assertTrue(in.getDomain().equals(hdr.getDomain()));
	}
	@Test
	public void testReplyInfo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 89, 0, 0, 0, 53, -85, 43, 48, 41, 48, 21, 12, 6, 65, 69, 83, 101, 100, 101, 12, 6, 68, 69, 83, 101, 100, 101, 12, 3, 68, 69, 83, 48, 16, 12, 7, 97, 115, 110, 49, 100, 101, 114, 12, 5, 66, 121, 116, 101, 115};
		ReplyInfo hdr = new ReplyInfo();
		ArrayList<String> list = new ArrayList<String>();
		list.add("AESede");
		list.add("DESede");
		list.add("DES");
		hdr.setCryptoProtocols(list);
		list = new ArrayList<String>();
		list.add("asn1der");
		list.add("Bytes");
		hdr.setDataEncoders(list);
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		ReplyInfo in = (ReplyInfo) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getCryptoProtocols().equals(hdr.getCryptoProtocols()));
		assertTrue(in.getDataEncoders().equals(hdr.getDataEncoders()));
	}
	@Test
	public void testGo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 97, 0, 0, 0, 35, -84, 25, 48, 23, 12, 6, 65, 69, 83, 101, 100, 101, 12, 7, 97, 115, 110, 49, 100, 101, 114, 4, 1, 1, 4, 1, 1};
		Go hdr = new Go();
		hdr.setCryptoProtocol("AESede");
		hdr.setDataEncoder("asn1der");
		DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(2), BigInteger.probablePrime(512, new Random()));
		byte[] dhBytes = dh.sendMessage();
		hdr.setDiffieHellmanParam(dhBytes);
		hdr.setSignature(dhBytes);
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		Go in = (Go) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getCryptoProtocol().equals(hdr.getCryptoProtocol()));
		assertTrue(in.getDataEncoder().equals(hdr.getDataEncoder()));
		assertTrue(Arrays.equals(in.getDiffieHellmanParam(), hdr.getDiffieHellmanParam()));
		assertTrue(Arrays.equals(in.getSignature(), hdr.getSignature()));
	}
	@Test
	public void testGoToo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 105, 0, 0, 0, 35, -83, 25, 48, 23, 12, 6, 65, 69, 83, 101, 100, 101, 12, 7, 97, 115, 110, 49, 100, 101, 114, 4, 1, 1, 4, 1, 1};
		GoToo hdr = new GoToo();
		hdr.setCryptoProtocol("AESede");
		hdr.setDataEncoder("asn1der");
		DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(2), BigInteger.probablePrime(512, new Random()));
		byte[] dhBytes = dh.sendMessage();
		hdr.setDiffieHellmanParam(dhBytes);
		hdr.setSignature(dhBytes);
		Frame frame = hdr.toFrame();
		byte[] bytes = frame.toByteArray();
		assertTrue(Arrays.equals(encodedBytes, bytes));
		Frame newFrame = Frame.readFromStream(new ByteArrayInputStream(bytes));
		GoToo in = (GoToo) newFrame.getHeader();
		assertTrue(in != null);
		assertTrue(in.getCryptoProtocol().equals(hdr.getCryptoProtocol()));
		assertTrue(in.getDataEncoder().equals(hdr.getDataEncoder()));
		assertTrue(Arrays.equals(in.getDiffieHellmanParam(), hdr.getDiffieHellmanParam()));
		assertTrue(Arrays.equals(in.getSignature(), hdr.getSignature()));
	}

	private PublicKey buildSampleVatId1PublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
				new BigInteger("14631611540685054051246828779474659279434795828979877100056741326852574061485199294988121017044511058150957383138883396176340034247939747961936993875787199951904902675151337040861984020136174965691597035416377590189950405767507320112179317911200971768673589874672666315699001559759593359858088499188421132948034888617685711165611182732555992037146239346746996434870589494285479881652847671542092722577703150166494760601888755745838507254088117517198055561562279008065104487719450273397328134480627880994280654832384199334978656423699129788360180335222193654848544660576393761816982999928331982769311793235220487698201"), 
				new BigInteger("65537"));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (java.security.interfaces.RSAPublicKey) kf.generatePublic(keySpec);
	}
}
