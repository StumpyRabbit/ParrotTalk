package club.callistohouse.session;

public class EncoderThunkMaker implements Cloneable {
	protected String encoderName;
	private EncoderThunk encoderThunk;

	public EncoderThunkMaker(String encoderName, EncoderThunk thunk) { this.encoderName = encoderName; encoderThunk = thunk; }

	public String getEncoderName() { return encoderName; }

	public EncoderThunk makeThunkOnFarKey(SessionIdentity farKey) {
		return encoderThunk;
	}
}
