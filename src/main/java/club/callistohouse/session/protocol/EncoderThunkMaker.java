package club.callistohouse.session.protocol;

import java.io.IOException;

import club.callistohouse.session.marshmuck.AbstractScope;
import club.callistohouse.session.payload.Encoded;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.PhaseHeader;

public abstract class EncoderThunkMaker implements Cloneable {
	protected String encoderName;
	protected AbstractScope scope;
	
	public EncoderThunkMaker(String encoderName) { this.encoderName = encoderName; }

	public String getEncoderName() { return encoderName; }

	public PhaseHeader headerThunk(Frame frame) { return new Encoded(); }
	public abstract Object preSerializeThunk(Frame frame) throws IOException;
	public abstract Object postSerializeThunk(Object frame) throws IOException;
	public abstract Object preMaterializeThunk(Frame frame) throws IOException;
	public abstract Object postMaterializeThunk(Object frame) throws IOException;

	public Object serializeThunk(Object chunk) { return externalize(chunk); }
	public Object materializeThunk(Object chunk) { return internalize(chunk); }

	public Object externalize(Object obj) {
		return (scope == null) ? obj : scope.externalize(obj);
	}
	public Object internalize(Object obj) {
		return (scope == null) ? obj : scope.internalize(obj);
	}

	public EncoderThunkMaker cloneOnScope(AbstractScope scope) {
		this.scope = scope;
		EncoderThunkMaker clone = null;
		try {
			clone = (EncoderThunkMaker) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.scope = null;
		return clone;
	}
	
	public Thunk makeThunk() {
		return new Thunk() {
			public Object downThunk(Frame frame) {
				Object chunk = null;
				try {
					chunk = preSerializeThunk(frame);
					chunk = serializeThunk(chunk);
					chunk = postSerializeThunk(chunk);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return chunk; 
			}
			public Object upThunk(Frame frame) {
				Object chunk = null;
				try {
					chunk = preMaterializeThunk(frame);
					chunk = materializeThunk(chunk);
					chunk = postMaterializeThunk(chunk);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return chunk; 
			}
			public PhaseHeader getHeader(Frame frame) { return headerThunk(frame); }
		};
	}

	public EncoderThunkMaker newOnScope(AbstractScope scope) throws CloneNotSupportedException {
		EncoderThunkMaker maker = (EncoderThunkMaker) clone();
		maker.scope = scope;
		return maker;
	}
}
