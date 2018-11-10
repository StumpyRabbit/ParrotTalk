	package club.callistohouse.session;

import java.util.ArrayList;
import java.util.List;

public class SessionAgentMap {

	private String selectedProtocolName;
	private String selectedEncoderName;
	private List<CipherThunkMaker> cryptoProtocols = new ArrayList<CipherThunkMaker>();
	private List<EncoderThunk> dataEncoders = new ArrayList<EncoderThunk>();

	public SessionAgentMap() {
		this(new ArrayList(), new ArrayList());
	}
	public SessionAgentMap(CipherThunkMaker cipherThunk, EncoderThunk encoderThunk) {
		cryptoProtocols.add(cipherThunk);
		dataEncoders.add(encoderThunk);
	}
	public SessionAgentMap(List<CipherThunkMaker> cipherThunks, List<EncoderThunk> encoderThunks) {
		this.cryptoProtocols = cipherThunks;
		this.dataEncoders = encoderThunks;
	}

	public CipherThunkMaker buildProtocol() {
		try {
			return lookupCryptoProtocol(selectedProtocolName).newMaker();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public EncoderThunk buildEncoder(SessionIdentity farKey) {
		return lookupDataEncoder(selectedEncoderName);
	}

	public CipherThunkMaker lookupCryptoProtocol(String proto) {
		for (CipherThunkMaker crypto : cryptoProtocols) {
			if (crypto.shortCryptoProtocol.equals(proto))
				return crypto;
		}
		return null;
	}

	public EncoderThunk lookupDataEncoder(String proto) {
		for (EncoderThunk encoder : dataEncoders) {
			if (encoder.getEncoderName().equals(proto))
				return encoder;
		}
		return null;
	}

	public List<String> getProtocolNames() {
		List<String> list = new ArrayList<String>();
		for (CipherThunkMaker crypto : cryptoProtocols) {
			list.add(crypto.shortCryptoProtocol);
		}
		return list;
	}
	public List<String> getDataEncoderNames() {
		List<String> list = new ArrayList<String>();
		for (EncoderThunk encoder : dataEncoders) {
			list.add(encoder.getEncoderName());
		}
		return list;
	}
	public String getSelectedProtocolName() { return selectedProtocolName; }
	public void setSelectedProtocolName(String protocolName) { selectedProtocolName = protocolName; }
	public String getSelectedEncoderName() { return selectedEncoderName; }
	public void setSelectedEncoderName(String encoderName) { selectedEncoderName = encoderName; }

}
