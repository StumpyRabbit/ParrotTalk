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
		byte[] encodedBytes = new byte[] {0, 0, 0, 65, 0, 0, 0, 29, -88, 19, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111};
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
		byte[] encodedBytes = new byte[] {0, 0, 0, 73, 0, 0, 1, 44, (byte)169, (byte)130, 1, 32, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111, 48, (byte)130, 1, 9, 2, (byte)130, 1, 0, 115, (byte)231, (byte)153, (byte)187, 59, (byte)135, (byte)204, 18, 127, 9, 12, 71, (byte)230, 103, 111, (byte)173, (byte)138, (byte)207, (byte)196, (byte)239, 59, 28, 52, (byte)143, 84, (byte)156, (byte)199, (byte)153, 38, 6, 5, (byte)171, 95, 32, 86, (byte)226, (byte)220, 15, 113, 75, (byte)219, 7, (byte)231, 107, 127, (byte)151, (byte)131, (byte)167, 61, 47, (byte)254, 81, 15, (byte)154, (byte)249, (byte)130, 72, 109, 9, (byte)238, 96, (byte)163, (byte)166, (byte)246, (byte)177, 27, 120, (byte)235, (byte)164, (byte)185, 46, (byte)195, 118, (byte)137, 64, 96, 5, 6, 114, (byte)135, 40, (byte)130, (byte)212, 7, 122, 117, (byte)210, (byte)245, 75, 11, 96, 124, 58, (byte)251, 42, (byte)195, 125, (byte)250, (byte)206, 101, 83, 61, 31, (byte)144, 103, 79, 70, 23, (byte)254, (byte)144, (byte)145, 119, (byte)235, (byte)187, (byte)244, (byte)164, 75, 53, 41, (byte)191, 97, (byte)154, 47, 86, 87, (byte)143, (byte)193, 82, (byte)134, 68, 40, (byte)131, (byte)148, 65, (byte)211, 9, (byte)229, 16, (byte)199, 95, (byte)202, 97, (byte)244, (byte)139, (byte)199, (byte)219, 4, 86, (byte)230, (byte)164, (byte)243, 111, 16, (byte)251, 29, (byte)137, 99, (byte)165, (byte)234, 109, (byte)174, (byte)141, 111, 81, 12, 112, 71, (byte)153, (byte)220, 88, (byte)216, (byte)180, 91, 87, 57, (byte)250, 118, 102, 81, (byte)139, (byte)159, (byte)242, 4, 84, 100, 13, (byte)226, (byte)171, (byte)231, (byte)153, (byte)218, 77, (byte)157, 61, (byte)170, 119, (byte)222, 28, 84, 110, (byte)202, (byte)214, (byte)175, (byte)135, (byte)243, 39, (byte)174, 31, 126, (byte)223, 39, 44, (byte)249, (byte)194, (byte)187, 127, 53, 122, 124, 55, 25, (byte)222, 42, (byte)224, 98, (byte)135, (byte)168, 13, 109, (byte)202, (byte)167, 18, 37, 6, (byte)204, (byte)211, 46, 102, 48, 10, 3, (byte)145, (byte)189, 10, (byte)200, (byte)145, 85, (byte)137, (byte)192, 109, (byte)245, (byte)178, (byte)155, (byte)155, 119, 25, 2, 3, 1, 0, 1};
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
		assertTrue(in.getPublicKeyImpl().equals(hdr.getPublicKeyImpl()));
	}
	@Test
	public void testGiveInfo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 81, 0, 0, 1, 42, -86, -127, 31, 12, 10, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 12, 5, 104, 101, 108, 108, 111, 48, -127, 9, 2, -126, 0, 1, 115, -25, -103, -69, 59, -121, -52, 18, 127, 9, 12, 71, -26, 103, 111, -83, -118, -49, -60, -17, 59, 28, 52, -113, 84, -100, -57, -103, 38, 6, 5, -85, 95, 32, 86, -30, -36, 15, 113, 75, -37, 7, -25, 107, 127, -105, -125, -89, 61, 47, -2, 81, 15, -102, -7, -126, 72, 109, 9, -18, 96, -93, -90, -10, -79, 27, 120, -21, -92, -71, 46, -61, 118, -119, 64, 96, 5, 6, 114, -121, 40, -126, -44, 7, 122, 117, -46, -11, 75, 11, 96, 124, 58, -5, 42, -61, 125, -6, -50, 101, 83, 61, 31, -112, 103, 79, 70, 23, -2, -112, -111, 119, -21, -69, -12, -92, 75, 53, 41, -65, 97, -102, 47, 86, 87, -113, -63, 82, -122, 68, 40, -125, -108, 65, -45, 9, -27, 16, -57, 95, -54, 97, -12, -117, -57, -37, 4, 86, -26, -92, -13, 111, 16, -5, 29, -119, 99, -91, -22, 109, -82, -115, 111, 81, 12, 112, 71, -103, -36, 88, -40, -76, 91, 87, 57, -6, 118, 102, 81, -117, -97, -14, 4, 84, 100, 13, -30, -85, -25, -103, -38, 77, -99, 61, -86, 119, -34, 28, 84, 110, -54, -42, -81, -121, -13, 39, -82, 31, 126, -33, 39, 44, -7, -62, -69, 127, 53, 122, 124, 55, 25, -34, 42, -32, 98, -121, -88, 13, 109, -54, -89, 18, 37, 6, -52, -45, 46, 102, 48, 10, 3, -111, -67, 10, -56, -111, 85, -119, -64, 109, -11, -78, -101, -101, 119, 25, 2, 3, 1, 0, 1};
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
		assertTrue(in.getPublicKeyImpl().equals(hdr.getPublicKeyImpl()));
	}
	@Test
	public void testReplyInfo() throws InstantiationException, IllegalAccessException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = new byte[] {0, 0, 0, 89, 0, 0, 0, 51, -85, 41, 48, 21, 12, 6, 65, 69, 83, 101, 100, 101, 12, 6, 68, 69, 83, 101, 100, 101, 12, 3, 68, 69, 83, 48, 16, 12, 7, 97, 115, 110, 49, 100, 101, 114, 12, 5, 66, 121, 116, 101, 115};
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
		byte[] encodedBytes = new byte[] {0, 0, 0, 97, 0, 0, 0, -94, -84, -127, -105, 12, 6, 65, 69, 83, 101, 100, 101, 12, 7, 97, 115, 110, 49, 100, 101, 114, 4, 65, 0, -98, -103, 56, 98, -63, 70, 103, 122, 108, 115, -42, -116, 114, -83, -9, -59, -52, -117, -27, -81, -4, -97, -75, 90, 1, 57, 88, -49, 121, 99, -60, -77, -39, 49, -112, 77, 63, 114, 106, 40, -30, 34, 23, -48, 42, 78, 6, 123, -33, 102, -116, 114, -117, -81, 121, 103, -76, 79, -19, -116, -36, -25, 40, 91, 4, 65, 0, -98, -103, 56, 98, -63, 70, 103, 122, 108, 115, -42, -116, 114, -83, -9, -59, -52, -117, -27, -81, -4, -97, -75, 90, 1, 57, 88, -49, 121, 99, -60, -77, -39, 49, -112, 77, 63, 114, 106, 40, -30, 34, 23, -48, 42, 78, 6, 123, -33, 102, -116, 114, -117, -81, 121, 103, -76, 79, -19, -116, -36, -25, 40, 91};
		Go hdr = new Go();
		hdr.setCryptoProtocol("AESede");
		hdr.setDataEncoder("asn1der");
		byte[] dhBytes = new byte[] {0, -98, -103, 56, 98, -63, 70, 103, 122, 108, 115, -42, -116, 114, -83, -9, -59, -52, -117, -27, -81, -4, -97, -75, 90, 1, 57, 88, -49, 121, 99, -60, -77, -39, 49, -112, 77, 63, 114, 106, 40, -30, 34, 23, -48, 42, 78, 6, 123, -33, 102, -116, 114, -117, -81, 121, 103, -76, 79, -19, -116, -36, -25, 40, 91};
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
		byte[] encodedBytes = new byte[] {0, 0, 0, 105, 0, 0, 0, -96, -83, -127, -107, 12, 6, 65, 69, 83, 101, 100, 101, 12, 7, 97, 115, 110, 49, 100, 101, 114, 4, 64, 47, -14, 90, -76, -79, 44, -104, -79, 121, -92, 3, 38, -14, 123, -101, -38, -66, 16, -126, -54, -73, 58, -115, 24, -67, -30, 97, 125, -109, -105, 25, -127, 122, -128, -60, -106, -8, -72, 89, -42, -73, -93, -99, -20, 87, 47, 10, -96, 90, 10, 23, 100, -115, -126, -74, -6, -89, -66, 113, -17, -47, 107, 6, -89, 4, 64, 47, -14, 90, -76, -79, 44, -104, -79, 121, -92, 3, 38, -14, 123, -101, -38, -66, 16, -126, -54, -73, 58, -115, 24, -67, -30, 97, 125, -109, -105, 25, -127, 122, -128, -60, -106, -8, -72, 89, -42, -73, -93, -99, -20, 87, 47, 10, -96, 90, 10, 23, 100, -115, -126, -74, -6, -89, -66, 113, -17, -47, 107, 6, -89};
		GoToo hdr = new GoToo();
		hdr.setCryptoProtocol("AESede");
		hdr.setDataEncoder("asn1der");
		byte[] dhBytes = new byte[] {47, -14, 90, -76, -79, 44, -104, -79, 121, -92, 3, 38, -14, 123, -101, -38, -66, 16, -126, -54, -73, 58, -115, 24, -67, -30, 97, 125, -109, -105, 25, -127, 122, -128, -60, -106, -8, -72, 89, -42, -73, -93, -99, -20, 87, 47, 10, -96, 90, 10, 23, 100, -115, -126, -74, -6, -89, -66, 113, -17, -47, 107, 6, -89};
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
