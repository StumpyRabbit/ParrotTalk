package club.callistohouse.session;

import java.io.IOException;

import club.callistohouse.session.payload.Encoded;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.RawData;
import club.callistohouse.session.protocol.ThunkRoot;

public abstract class EncoderThunk extends ThunkRoot implements Cloneable {
	protected String encoderName;

	public EncoderThunk(String encoderName) { this.encoderName = encoderName; }

	public String getEncoderName() { return encoderName; }

	public abstract Object serializeThunk(Object chunk) throws IOException;
	public abstract Object materializeThunk(Object chunk) throws IOException, ClassNotFoundException;
	protected boolean doesFrameEmbedding() { return true; }

	public void downcall(Frame frame) {
		try {
			frame.setPayload(serializeThunk(frame.getPayload()));
			frame.setHeader(new Encoded());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void upcall(Frame frame) {
		try {
			frame.setPayload(materializeThunk(frame.getPayload()));
			frame.setHeader(new RawData());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
