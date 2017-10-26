package club.callistohouse.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.session.marshmuck.AbstractScope;
import club.callistohouse.session.marshmuck.DummyScope;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.protocol.CipherThunkMaker;
import club.callistohouse.session.protocol.EncoderThunkMaker;

public class SessionAgentMap {

	private List<String> protocolNames= new ArrayList<String>();
	private List<String> dataEncoderNames = new ArrayList<String>();
	private String selectedProtocolName;
	private String selectedEncoderName;
	private ScopeMaker scopeMaker;

	public SessionAgentMap() {
		this(availableProtocolNames(), availableEncoderNames());
	}
	public SessionAgentMap(List<String> protoNames, List<String> encoderNames) {
		this.protocolNames = protoNames;
		this.dataEncoderNames = encoderNames;
		this.scopeMaker = new ScopeMaker() {
			public AbstractScope scopeMaker(SessionIdentity farKey) {
				return new DummyScope();
			}};
	}

	public CipherThunkMaker buildProtocol() {
		try {
			return lookupProtocolByName(selectedProtocolName).newMaker();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public EncoderThunkMaker buildEncoder(SessionIdentity farKey) {
		try {
			return lookupEncoderByName(selectedEncoderName).newOnScope(scopeMaker.scopeMaker(farKey));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ScopeMaker getScopeMaker() { return scopeMaker; }
	public void setScopeMaker(ScopeMaker scopeMaker) { this.scopeMaker = scopeMaker; }
	public List<String> getProtocolNames() { return protocolNames; }
	public List<String> getDataEncoderNames() { return dataEncoderNames; }
	public String getSelectedProtocolName() { return selectedProtocolName; }
	public void setSelectedProtocolName(String encoderName) { selectedEncoderName = encoderName; }
	public String getSelectedEncoderName() { return selectedProtocolName; }
	public void setSelectedEncoderName(String protocolName) { selectedProtocolName = protocolName; }


	private static List<EncoderThunkMaker> Encoders = new ArrayList<EncoderThunkMaker>();
	private static List<CipherThunkMaker> Protocols = new ArrayList<CipherThunkMaker>();

	static {
		EncoderThunkMaker encoderMaker = new EncoderThunkMaker("asn1der") {
			@SuppressWarnings("resource")
			public Object preMaterializeThunk(Frame frame) throws IOException {
				byte[] data = (byte[]) frame.getPayload(); // obviously need to supply real data here
				ASN1InputStream input = new ASN1InputStream(data);
				try {
					return input.decode();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			public Object postMaterializeThunk(Object obj) { return obj; }
			public Object preSerializeThunk(Frame frame) { return frame.toByteArray(); }
			public Object postSerializeThunk(Object obj) { 
				try {
					return ASN1OutputStream.encodeObject(obj); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new byte[0];
				} }
		};
		
		Encoders.add(encoderMaker);
		Protocols.add(new CipherThunkMaker("AESede", "AES/CBC/PKCS5Padding", 32, 16, true));
		Protocols.add(new CipherThunkMaker("DESede", "DESede/CBC/PKCS5Padding", 24, 8, true));
		Protocols.add(new CipherThunkMaker("DES", "DES/CBC/PKCS5Padding", 8, 8, true));
	}
	public static List<String> availableEncoderNames() {
		List<String> encoderNames = new ArrayList<String>();
		Iterator<EncoderThunkMaker> iter = Encoders.iterator();
		while(iter.hasNext()) {
			encoderNames.add(iter.next().getEncoderName());
		}
		return encoderNames;
	}
	public static List<String> availableProtocolNames() {
		List<String> protocolNames = new ArrayList<String>();
		Iterator<CipherThunkMaker> iter = Protocols.iterator();
		while(iter.hasNext()) {
			protocolNames.add(iter.next().shortCryptoProtocol);
		}
		return protocolNames;
	}
	public static EncoderThunkMaker lookupEncoderByName(String encoderName) {
		Iterator<EncoderThunkMaker> iter = Encoders.iterator();
		while(iter.hasNext()) {
			EncoderThunkMaker maker = iter.next();
			if(maker.getEncoderName().equals(encoderName)) {
				return maker; 
			}
		}
		throw new RuntimeException("bad encoder");
	}
	public static CipherThunkMaker lookupProtocolByName(String protoName) {
		Iterator<CipherThunkMaker> iter = Protocols.iterator();
		while(iter.hasNext()) {
			CipherThunkMaker maker = iter.next();
			if(maker.shortCryptoProtocol.equals(protoName)) {
				return maker; 
			}
		}
		throw new RuntimeException("bad cipher");
	}
}
