package club.callistohouse.session.thunkstack_core;

public abstract class Thunk extends ThunkRoot {
	public boolean isFrameEmbedded = true;

	public Thunk() { this(true); }
	public Thunk(boolean frameEmbedding) { super(); this.isFrameEmbedded = frameEmbedding; }

	protected boolean doesFrameEmbedding() { return isFrameEmbedded; }
}
