/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2003, 2016 Robert Withers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ******************************************************************************
 * murmur/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
******************************************************************************
	murmur/whisper would not be possible without the ideas, implementation, 
	brilliance and passion of the erights.org community, which is also this software's 
	foundation.  In particular, I would like to thank the following individuals:
	        Mark Miller
	        Marc Stiegler
	        Bill Franz
	        Tyler Close 
	        Kevin Reid
 *******************************************************************************/

package club.callistohouse.session.parrotttalk;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import club.callistohouse.session.multiprotocol_core.Frame;
import club.callistohouse.session.multiprotocol_core.InternalChangeEncryption;
import club.callistohouse.session.multiprotocol_core.MessageEnum;
import club.callistohouse.session.multiprotocol_core.PhaseHeader;
import club.callistohouse.session.multiprotocol_core.RawData;
import club.callistohouse.session.thunkstack_core.ReceivingFrameBuffer;
import club.callistohouse.session.thunkstack_core.SendFramesBuffer;
import club.callistohouse.session.thunkstack_core.SocketThunk;
import club.callistohouse.session.thunkstack_core.ThunkLayer;
import club.callistohouse.session.thunkstack_core.ThunkStack;
import club.callistohouse.utils.ClassUtil;
import club.callistohouse.utils.events.EventEngine;
import club.callistohouse.utils.events.EventEngineInterface;
import club.callistohouse.utils.events.Listener;
import club.callistohouse.utils.transport.NIOConnection;

/**
 * The key to this session subsystem is again the use of the event framework. This class fires
 * the following events
 * 
 * Public events: 
 * - SessionTerminal.Connected 
 * - SessionTerminal.Disconnected 
 * - SessionTerminal.Identified 
 * - SessionTerminal.Encrypted 
 * - SessionTerminal.DataReceived  // This only fires if the state machine is Encrypted_Connected in a secure connection
 * 
 * @author Robert Withers
 *
 */
public class Session extends ThunkLayer implements EventEngineInterface {
//	private static Logger log = Logger.getLogger(Session.class);

	private SessionIdentity farKey;
	private SessionAgent agent;
	private SessionAgentMap map;
	private ThunkStack stack;

    private NIOConnection connection;
	protected BlockingQueue<byte[]> outgoingMessageQueue = new LinkedBlockingQueue<byte[]>();
	private EventEngine eventEngine = new EventEngine();
	boolean isEncrypted = false;

    public Session(SessionIdentity farKey, SessionAgent agent, SessionAgentMap map) {
     	this.farKey = farKey;
     	this.agent = agent;
     	this.map = map;
    	this.addListener(new Listener<Frame>(Frame.class) {
			@Override
			protected void handle(Frame frame) {
				handleFrame(frame);
			}
		});
    	this.addListener(new Listener<InternalChangeEncryption>(InternalChangeEncryption.class) {
			@Override
			protected void handle(InternalChangeEncryption ice) {
				handleSessionTerminalEncrypted(ice);
			}
		});
	}

    public Session(SessionIdentity farKey, SessionAgent agent, SessionAgentMap map, NIOConnection conn) {
    	this(farKey, agent, map);
    	setConnection(conn);
	}

	public ThunkStack getStack() { return stack; }
	public void setStack(ThunkStack aStack) { stack = aStack;}
	public SessionIdentity getNearKey() { return agent.getNearKey(); }
    public SessionIdentity getFarKey() { return farKey; }

	public Object upThunk(Frame frame) {
		if(frame.getHeaderType() != MessageEnum.RAW_DATA.getCode()) {
    		throw new RuntimeException("bad protocol header");
		} else {
			handleFrame(frame);
		}
		return null;
	}

	public void start() throws IOException {
        connection.start();
    }

	public void stop() {
    	NIOConnection conn = connection;
        connection = null;
        if (conn != null) {
            conn.stop(null);
        }
    }

    public void send(byte[] payload) {
		try {
			if(outgoingMessageQueue != null) {
				outgoingMessageQueue.put(payload);
			} else {
				sendFrame(new Frame(new RawData(), payload));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(PhaseHeader msg) throws IOException { sendFrame(msg.toFrame()); }
	void sendFrame(Frame frame) throws IOException { stack.downcall(frame, this); }

	void handleFrame(Frame frame) {
    	if(ClassUtil.isAssignableFrom(frame.getHeader(), RawData.class) && isEncrypted) {
    		fire(new DataReceived(frame.getPayload()));
    	}
	}

    void setConnection(NIOConnection connection) {
    	this.connection = connection;
    	SessionOperations ops = new SessionOperations(this, map);
    	ThunkStack stack = new ThunkStack();
		stack.push(new SocketThunk(connection));
		stack.push(new ReceivingFrameBuffer());
		stack.push(ops);
		stack.propertyAtPut("Ops", ops);
		stack.push(new SendFramesBuffer());
		stack.push(this);
		stack.install();
    }

	void handleSessionTerminalEncrypted(InternalChangeEncryption changeEncryption) {
		synchronized (Session.this) {
			/*reader.updateEncryption(changeEncryption);
			writer.updateEncryption(changeEncryption);*/
			fire(new Encrypted());
			isEncrypted = true;
			BlockingQueue<byte[]> tempQueue = outgoingMessageQueue;
			outgoingMessageQueue = null;
			agent.handleSessionRunning(this);
			while (!tempQueue.isEmpty()) {
				try {
					send(tempQueue.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    }

	void call() { ((SessionOperations) stack.propertyAt("Ops")).call(); }
    void answer() { ((SessionOperations) stack.propertyAt("Ops")).answer(); }

	public String toString() { 
    	return getClass().getSimpleName() 
    			+ "(localId " + getNearKey() 
    			+ "-> remoteId " + getFarKey() + ")"; 
    }

	public static class Running {
		public Session session;
		public Running(Session session) { this.session = session; }
	}
	public static class Connected {
		public Session terminal;
		public Connected(Session terminal) { this.terminal = terminal; }
	}
	public static class Disconnected {
		public Session terminal;
		public Disconnected(Session terminal) { this.terminal = terminal; }
	}
	public static class Identified { }
	public static class Encrypted { }
	public static class DataReceived {
		public Object data;
		public DataReceived(Object object) { this.data = object; }
	}

	@Override
	public <GEvent> void fire(GEvent event) { eventEngine.fire(event); }
	@Override
	public void addListener(Listener<?> handler) { eventEngine.addListener(handler); }
	@Override
	public void removeListener(Listener<?> handler) { eventEngine.removeListener(handler); }
	@Override
	public void removeAllListeners() { eventEngine.removeAllListeners(); }
	@Override
	public void forwardEventsTo(EventEngine engine) { eventEngine.forwardEventsTo(engine); }
	@Override
	public void unforwardEventsTo(EventEngine engine) { eventEngine.unforwardEventsTo(engine); }
}
