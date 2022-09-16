package club.callistohouse.session;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action;

import club.callistohouse.session.marshmuck.State;
import club.callistohouse.session.marshmuck.Trigger;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.MAC;
import club.callistohouse.session.payload.MessageEnum;
import club.callistohouse.session.payload.NotMe;
import club.callistohouse.session.payload.PhaseHeader;
import club.callistohouse.session.payload.ProtocolAccepted;
import club.callistohouse.session.payload.ProtocolOffered;
import club.callistohouse.session.protocol.ThunkFinishedException;
import club.callistohouse.session.protocol.ThunkLayer;
import club.callistohouse.session.protocol.ThunkStack;
import club.callistohouse.utils.ClassUtil;

public class SessionProtocolSelector extends ThunkLayer {
	public static Logger log = Logger.getLogger(SessionProtocolSelector.class);

	private ThunkStack stack; 
	private Session session; 
	private SecurityOps securityOps; 
	StateMachine<State, Trigger> stateMachine;
	boolean isIncoming = false;

	public SessionProtocolSelector(Session session, SessionAgentMap map) {
		super();
		this.session = session;
		this.securityOps = new SecurityOps(map);
    	this.stateMachine = new StateMachine<State, Trigger>(State.Initial, buildStateMachineConfig());
	}
	public void setStack(ThunkStack aStack) { stack = aStack;}
	public void call() {
		stateMachine.fire(Trigger.Calling);
	}

	public void answer() {
    	stateMachine.fire(Trigger.Answering); 
	}
	SessionIdentity getLocalIdentity() { return session.getNearKey(); }
	SessionIdentity getRemoteIdentity() { return session.getFarKey(); }

	public Object upThunk(Frame frame) {
		if(frame.getHeaderType() != MessageEnum.MAC_DATA.getCode()) {
//			log.debug("handling: " + frame.getHeader());
			handleHeader(frame.getHeader());
    		throw new RuntimeException("protocol message");
		} else {
			handleHeader((MAC)frame.getHeader());
		}
		return null;
	}

	synchronized void send(PhaseHeader header) throws IOException {
		stack.downcall(header.toFrame(), this); }

	void sendProtocolOffered() {
		PhaseHeader header = new ProtocolOffered("ParrotTalk-3.6", "ParrotTalk-3.6");
		securityOps.addLocalFrame(header.toFrame());
		stateMachine.fire(Trigger.ExpectProtocolAccepted);
		try {
			send(header);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void sendProtocolAccepted() {
		PhaseHeader header = new ProtocolAccepted("ParrotTalk-3.6");
		securityOps.addLocalFrame(header.toFrame());
		stateMachine.fire(Trigger.SentProtocolAccepted);
		try {
			send(header);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void sendNotMe() {
		stateMachine.fire(Trigger.Disconnect);
		try {
			send(new NotMe());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleHeader(PhaseHeader header) { 
		if(ClassUtil.isAssignableFrom(header, ProtocolOffered.class)) {
			handleMessage((ProtocolOffered) header);
		} else if(ClassUtil.isAssignableFrom(header, ProtocolAccepted.class)) {
			handleMessage((ProtocolAccepted) header);
		} else if(ClassUtil.isAssignableFrom(header, NotMe.class)) {
			handleMessage((NotMe) header);
		} else {
			log.debug("session msg received not handled: " + header);
			throw new RuntimeException("session msg received not handled: " + header);
		}
		throw new ThunkFinishedException();
	}

	public void handleMessage(ProtocolOffered body) {
    	if(stateMachine.isInState(State.AnswerReceiveProtocolOffered)) {
    		securityOps.addRemoteFrame(body.toFrame());
    		stateMachine.fire(Trigger.ReceivedProtocolOffered);
    	} else {
    		log.debug("Terminal in wrong connection state for ReceivedProtocolOffered trigger; in state: " + stateMachine.getState() + "; expecting: AnswerReceiveProtocolOffered");
    		throw new RuntimeException("Terminal in wrong connection state for ReceivedProtocolOffered trigger; in state: " + stateMachine.getState() + "; expecting: AnswerReceiveProtocolOffered");
    	}
	}
	public void handleMessage(ProtocolAccepted body) {
    	if(stateMachine.isInState(State.CallReceiveProtocolAccepted)) {
    		securityOps.addRemoteFrame(body.toFrame());
    		stateMachine.fire(Trigger.ReceivedProtocolAccepted);
    	} else {
    		log.debug("Terminal in wrong connection state for ReceivedProtocolAccepted trigger; in state: " + stateMachine.getState() + "; expecting: CallReceiveProtocolAccepted");
    		throw new RuntimeException("Terminal in wrong connection state for ReceivedProtocolAccepted trigger; in state: " + stateMachine.getState() + "; expecting: CallReceiveProtocolAccepted");
    	}
	}
	public void handleMessage(NotMe body) {
	}
	private void startupSuccessful(boolean isIncoming2) throws IOException {
		securityOps.installOn(session, stack, isIncoming2);
		securityOps.clearSensitiveInfo();
	}

	public StateMachineConfig<State,Trigger> buildStateMachineConfig() {
		StateMachineConfig<State, Trigger> sessionConnectionConfig = new StateMachineConfig<State, Trigger>();

		sessionConnectionConfig.configure(State.Initial)
			.permit(Trigger.Calling, State.CallInProgress)
			.permit(Trigger.Answering, State.AnswerInProgress);
		sessionConnectionConfig.configure(State.ProtocolSelected)
			.permit(Trigger.Disconnect, State.Closed);
		sessionConnectionConfig.configure(State.Closed);

		/** 
		 * Calling states
		 */
		sessionConnectionConfig.configure(State.CallInProgress)
			.substateOf(State.Initial)
			.onEntry(new Action() {
				public void doIt() {
					sendProtocolOffered();
				}})
			.permit(Trigger.ExpectProtocolAccepted, State.CallReceiveProtocolAccepted);
		sessionConnectionConfig.configure(State.CallReceiveProtocolAccepted)
			.substateOf(State.CallInProgress)
			.permit(Trigger.ReceivedProtocolAccepted, State.ProtocolSelected);

		/** 
		 * Answering states
		 */
		sessionConnectionConfig.configure(State.AnswerInProgress)
			.substateOf(State.Initial)
			.onEntry(new Action() {
				public void doIt() {
					stateMachine.fire(Trigger.ExpectProtocolOffered);
				}})
			.permit(Trigger.ExpectProtocolOffered, State.AnswerReceiveProtocolOffered);
		sessionConnectionConfig.configure(State.AnswerReceiveProtocolOffered)
			.substateOf(State.AnswerInProgress)
			.permit(Trigger.ReceivedProtocolOffered, State.AnswerSendingProtocolAccepted);
		sessionConnectionConfig.configure(State.AnswerSendingProtocolAccepted)
			.substateOf(State.AnswerInProgress)
			.onEntry(new Action() {
				public void doIt() {
					sendProtocolAccepted();
				}})
			.permit(Trigger.SentProtocolAccepted, State.ProtocolSelected);

		return sessionConnectionConfig;
	}
}
