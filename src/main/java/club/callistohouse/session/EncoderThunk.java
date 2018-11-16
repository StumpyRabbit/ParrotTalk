package club.callistohouse.session;

import java.io.IOException;

import club.callistohouse.session.payload.Encoded;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.RawData;
import club.callistohouse.session.protocol.ThunkLayer;
import club.callistohouse.session.protocol.ThunkRoot;

public abstract class EncoderThunk extends ThunkRoot implements Cloneable {
	protected String encoderName;

	public EncoderThunk(String encoderName) { this.encoderName = encoderName; }

	public String getEncoderName() { return encoderName; }

	public abstract Object serializeThunk(Object chunk) throws IOException;
	public abstract Object materializeThunk(Object chunk) throws IOException, ClassNotFoundException;

	public void downcall(Frame frame) {
		try {
			frame.setPayload(serializeThunk(frame.getPayload()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setHeader(new Encoded());
	}
	public void upcall(Frame frame) {
		try {
			frame.setPayload(materializeThunk(frame.getPayload()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
