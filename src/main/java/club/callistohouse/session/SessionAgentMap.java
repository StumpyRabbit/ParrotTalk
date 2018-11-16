	package club.callistohouse.session;

import java.util.ArrayList;
import java.util.List;

public class SessionAgentMap {

	private String selectedProtocolName;
	private String selectedEncoderName;
	private List<CipherThunkMaker> cryptoProtocols = new ArrayList<CipherThunkMaker>();
	private List<EncoderThunkMaker> dataEncoders = new ArrayList<EncoderThunkMaker>();

	public SessionAgentMap() {
		this(new ArrayList<CipherThunkMaker>(), new ArrayList<EncoderThunkMaker>());
	}
	public SessionAgentMap(CipherThunkMaker cipherThunkMaker, EncoderThunkMaker encoderThunkMaker) {
		cryptoProtocols.add(cipherThunkMaker);
		dataEncoders.add(encoderThunkMaker);
	}
	public SessionAgentMap(List<CipherThunkMaker> cipherThunks, List<EncoderThunkMaker> encoderThunks) {
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
		return lookupDataEncoder(selectedEncoderName).makeThunkOnFarKey(farKey);
	}

	public CipherThunkMaker lookupCryptoProtocol(String proto) {
		for (CipherThunkMaker crypto : cryptoProtocols) {
			if (crypto.shortCryptoProtocol.equals(proto))
				return crypto;
		}
		return null;
	}

	public EncoderThunkMaker lookupDataEncoder(String proto) {
		for (EncoderThunkMaker encoderMaker : dataEncoders) {
			if (encoderMaker.getEncoderName().equals(proto))
				return encoderMaker;
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
		for (EncoderThunkMaker encoderMaker : dataEncoders) {
			list.add(encoderMaker.getEncoderName());
		}
		return list;
	}
	public String getSelectedProtocolName() { return selectedProtocolName; }
	public void setSelectedProtocolName(String protocolName) { selectedProtocolName = protocolName; }
	public String getSelectedEncoderName() { return selectedEncoderName; }
	public void setSelectedEncoderName(String encoderName) { selectedEncoderName = encoderName; }

}
