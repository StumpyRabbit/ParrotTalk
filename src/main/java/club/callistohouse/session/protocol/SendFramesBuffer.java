package club.callistohouse.session.protocol;

import java.util.ArrayList;
import java.util.List;

import club.callistohouse.session.payload_core.Frame;

public class SendFramesBuffer extends ThunkLayer {
	private List<Frame> frameList = new ArrayList<Frame>();

	public SendFramesBuffer() {}
	public List<Frame> bufferList() { return frameList; }
	@Override
	protected Object downThunk(Frame frame) { frameList.add(frame); throw new ThunkFinishedException(); }
}
