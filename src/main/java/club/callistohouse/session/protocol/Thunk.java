package club.callistohouse.session.protocol;

public abstract class Thunk extends ThunkRoot {
	public boolean doesPop = true;
	public boolean doesPush = true;

	public Thunk() { this(true, true); }
	public Thunk(boolean push, boolean pop) { super(); this.doesPush = push; this.doesPop = pop; }

	protected boolean doesPush() { return doesPush; }
	protected boolean doesPop() { return doesPop; }
}
