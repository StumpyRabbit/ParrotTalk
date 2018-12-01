package club.callistohouse.session.thunkstack_core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import club.callistohouse.session.payload_core.Frame;
import club.callistohouse.session.payload_core.PhaseHeader;

public class ThunkRoot {

	public ThunkRoot() { super(); }
	public boolean isConnected() { return true; }

	public void close() { flush(); }
	public void flush() { }
	public void install() { }
	public void run() {}
	public void shutdown() { }

	public void setStack(ThunkStack aStack) {}

	public void downcall(Frame frame) {
		if(doesFrameEmbedding()) {
			frame.setPayload(downThunk(frame));
			frame.setHeader(getHeader(frame));
		} else {
			downThunk(frame);
		}
	}
	public void upcall(Frame frame) {
		if(doesFrameEmbedding()) {
			try {
				frame.readFrom(new ByteArrayInputStream((byte[]) upThunk(frame)));
			} catch (IOException e) {
				throw new ThunkFinishedException();
			}
		} else {
			upThunk(frame);
		}
	}
	protected PhaseHeader getHeader(Frame frame) { return frame.getHeader(); }
	protected Object downThunk(Frame frame) { return frame.toByteArray(); }
	protected Object upThunk(Frame frame) { return frame.getPayload(); }
	protected boolean doesFrameEmbedding() { return false; }
}
