package club.callistohouse.session.protocol;

import java.util.ArrayList;
import java.util.List;

import club.callistohouse.session.payload.Frame;

public class SendFramesBuffer extends ThunkLayer {
	private List<Frame> frameList = new ArrayList<Frame>();

	public SendFramesBuffer() {}
	public List<Frame> bufferList() { return frameList; }
	protected boolean doesPush() { return false; }
	protected boolean doesPop() { return false; }
	@Override
	protected Object downThunk(Frame frame) { frameList.add(frame); throw new ThunkFinishedException(); }
}
