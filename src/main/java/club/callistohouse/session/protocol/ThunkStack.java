package club.callistohouse.session.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import club.callistohouse.session.payload.Frame;

public class ThunkStack extends Stack<ThunkRoot> {
	private static final long serialVersionUID = -5274871473439808424L;

	private Map<String,Object> properties = new HashMap<String,Object>();

	public ThunkStack() { }

	public ThunkRoot head() { return firstElement(); }
	public ThunkRoot tail() { return lastElement(); }
	public boolean isConnected() { return tail().isConnected(); }
	@Override
	public ThunkRoot pop() { return super.pop(); }
	@Override
	public ThunkRoot push(ThunkRoot g) { return super.push(g); }

	public void install() {
		for(ThunkRoot g:this) { 
			g.install();
		}
	}
	public void run() {
		for(ThunkRoot g:this) { 
			g.run();
		}
	}
	public void stop() {
		for(ThunkRoot g:this) { 
			g.close();
		}
		shutdown();
	}
	public void shutdown() {
		while(!isEmpty()) {
			head().close();
			pop();
		}
		for(ThunkRoot g:this) { 
			g.run();
		}
	}

	public boolean upcall(Frame srcFrame, ThunkRoot sourceGate) {
		Frame frame = srcFrame;
		try {
			for(int i = indexOf(sourceGate) + 1; i < size(); i++) {
				get(i).upcall(frame);
			}
		} catch (ThunkFinishedException error) {
			return false;
		}
		return true;
	}
	public boolean downcall(Frame srcFrame, ThunkRoot sourceGate) {
		Frame frame = srcFrame;
		try {
			for(int i = indexOf(sourceGate) - 1; i >= 0; i--) {
				get(i).downcall(frame);
			}
		} catch (ThunkFinishedException error) {
			return false;
		}
		return true;
	}

	public Object propertyAt(String propName) { return properties.get(propName); }
	public void propertyAtPut(String propName, Object obj) { properties.put(propName, obj); }
}

