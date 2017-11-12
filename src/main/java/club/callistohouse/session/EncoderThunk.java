package club.callistohouse.session;

import club.callistohouse.session.payload.Encoded;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.RawData;
import club.callistohouse.session.protocol.ThunkRoot;

public abstract class EncoderThunk extends ThunkRoot implements Cloneable {
	protected String encoderName;

	public EncoderThunk(String encoderName) { this.encoderName = encoderName; }

	public String getEncoderName() { return encoderName; }

	public abstract Object serializeThunk(Object chunk);
	public abstract Object materializeThunk(Object chunk);

	public void downcall(Frame frame) {
		frame.setPayload(serializeThunk(frame.getPayload()));
		frame.setHeader(new Encoded());
	}
	public void upcall(Frame frame) {
		frame.setPayload(materializeThunk(frame.getPayload()));
		frame.setHeader(new RawData());
	}

	public EncoderThunk makeThunkOnFarKey(SessionIdentity farKey) {
		try {
			return (EncoderThunk) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
