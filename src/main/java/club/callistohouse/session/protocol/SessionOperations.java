package club.callistohouse.session.protocol;

import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.apache.log4j.Logger;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action;

import club.callistohouse.session.Session;
import club.callistohouse.session.Session.Identified;
import club.callistohouse.session.SessionAgentMap;
import club.callistohouse.session.SessionIdentity;
import club.callistohouse.session.marshmuck.State;
import club.callistohouse.session.marshmuck.Trigger;
import club.callistohouse.session.payload.DuplicateConnection;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.GiveInfo;
import club.callistohouse.session.payload.Go;
import club.callistohouse.session.payload.GoToo;
import club.callistohouse.session.payload.IAm;
import club.callistohouse.session.payload.IWant;
import club.callistohouse.session.payload.InternalChangeEncryption;
import club.callistohouse.session.payload.MAC;
import club.callistohouse.session.payload.MessageEnum;
import club.callistohouse.session.payload.NotMe;
import club.callistohouse.session.payload.PhaseHeader;
import club.callistohouse.session.payload.ProtocolAccepted;
import club.callistohouse.session.payload.ProtocolOffered;
import club.callistohouse.session.payload.ReplyInfo;
import club.callistohouse.utils.ClassUtil;

public class SessionOperations extends ThunkLayer {
	private static Logger log = Logger.getLogger(SessionOperations.class);

	private ThunkStack stack; 
	private Session session; 
	private SecurityOps securityOps; 
	StateMachine<State, Trigger> stateMachine;
	boolean isIncoming = false;

	public SessionOperations(ThunkStack stack, Session session, SessionAgentMap map) {
		super();
		this.stack = stack;
		this.session = session;
		this.securityOps = new SecurityOps(map);
    	this.stateMachine = new StateMachine<State, Trigger>(State.Initial, buildStateMachineConfig());
	}
	public void call() {
		stateMachine.fire(Trigger.Calling);
	}

	public void answer() {
    	stateMachine.fire(Trigger.Answering); 
	}
	SessionIdentity getLocalIdentity() { return session.getNearKey(); }
	SessionIdentity getRemoteIdentity() { return session.getFarKey(); }

	public boolean doesPop() { return false; }
	public boolean doesPush() { return false; }
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

	synchronized void send(PhaseHeader header) {
		log.debug("session msg sending: " + header);
		stack.downcall(header.toFrame(), this); }

	void sendProtocolOffered() {
		PhaseHeader header = new ProtocolOffered("Whisper-1", "Whisper-1");
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectProtocolAccepted);
		send(header);
	}
	void sendProtocolAccepted() {
		PhaseHeader header = new ProtocolAccepted("Whisper-1");
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectIWant);
		send(header);
	}
	void sendIWant() {
		PhaseHeader header = new IWant(getRemoteIdentity().getVatId());
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectIAm);
		send(header);
	}
	void sendIAm() {
		PhaseHeader header = new IAm(getLocalIdentity());
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectGiveInfo);
		send(header);
	}
	void sendGiveInfo() {
		PhaseHeader header = new GiveInfo(getLocalIdentity());
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectReplyInfo);
    	send(header);
	}
	void sendReplyInfo() {
		PhaseHeader header = new ReplyInfo(securityOps.getSessionAgentMap().getProtocolNames(), securityOps.getSessionAgentMap().getDataEncoderNames());
		securityOps.addLocalMessage(header.toFrame().toByteArray());
		stateMachine.fire(Trigger.ExpectGo);
		send(header);
	}
	void sendGo() {
		try {
			byte[] signature = null;
			byte[] dhParam = securityOps.getDhParam();
			securityOps.addLocalMessage(dhParam);
			byte[] msgBytes = securityOps.getLocalMessagesBytes();
			try {
				signature = getLocalIdentity().getSignatureBytes(msgBytes);
			} catch (SignatureException e) {
				e.printStackTrace();
				stateMachine.fire(Trigger.SendBye);
				return;
			}
			stateMachine.fire(Trigger.ExpectGoToo);
	    	send(new Go(securityOps.map.getSelectedProtocolName(), securityOps.map.getSelectedEncoderName(), dhParam, signature));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
	}
	void sendGoToo() {
		try {
			byte[] signature = null;
			byte[] dhParam = securityOps.getDhParam();
			securityOps.addLocalMessage(dhParam);
			byte[] msgBytes = securityOps.getLocalMessagesBytes();
			try {
				signature = getLocalIdentity().getSignatureBytes(msgBytes);
			} catch (SignatureException e) {
				e.printStackTrace();
				stateMachine.fire(Trigger.SendBye);
				return;
			}
	    	send(new GoToo(securityOps.map.getSelectedProtocolName(), securityOps.map.getSelectedEncoderName(), dhParam, signature));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
	}
	void sendNotMe() {
		stateMachine.fire(Trigger.Disconnect);
		send(new NotMe());
	}

	public void handleHeader(PhaseHeader header) { 
		log.debug("session msg received: " + header);
		if(ClassUtil.isAssignableFrom(header, ProtocolOffered.class)) {
			handleMessage((ProtocolOffered) header);
		} else if(ClassUtil.isAssignableFrom(header, ProtocolAccepted.class)) {
			handleMessage((ProtocolAccepted) header);
		} else if(ClassUtil.isAssignableFrom(header, MAC.class)) {
			handleMessage((MAC) header);
		} else if(ClassUtil.isAssignableFrom(header, IWant.class)) {
			handleMessage((IWant) header);
		} else if(ClassUtil.isAssignableFrom(header, IAm.class)) {
			handleMessage((IAm) header);
		} else if(ClassUtil.isAssignableFrom(header, GiveInfo.class)) {
			handleMessage((GiveInfo) header);
		} else if(ClassUtil.isAssignableFrom(header, ReplyInfo.class)) {
			handleMessage((ReplyInfo) header);
		} else if(ClassUtil.isAssignableFrom(header, Go.class)) {
			handleMessage((Go) header);
		} else if(ClassUtil.isAssignableFrom(header, GoToo.class)) {
			handleMessage((GoToo) header);
		} else if(ClassUtil.isAssignableFrom(header, DuplicateConnection.class)) {
			handleMessage((DuplicateConnection) header);
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
    		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    		stateMachine.fire(Trigger.ReceivedProtocolOffered);
    	} else {
    		log.debug("Terminal in wrong connection state for ReceivedProtocolOffered trigger; in state: " + stateMachine.getState() + "; expecting: AnswerReceiveProtocolOffered");
    		throw new RuntimeException("Terminal in wrong connection state for ReceivedProtocolOffered trigger; in state: " + stateMachine.getState() + "; expecting: AnswerReceiveProtocolOffered");
    	}
	}
	public void handleMessage(ProtocolAccepted body) {
    	if(stateMachine.isInState(State.CallReceiveProtocolAccepted)) {
    		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    		stateMachine.fire(Trigger.ReceivedProtocolAccepted);
    	} else {
    		log.debug("Terminal in wrong connection state for ReceivedProtocolAccepted trigger; in state: " + stateMachine.getState() + "; expecting: CallReceiveProtocolAccepted");
    		throw new RuntimeException("Terminal in wrong connection state for ReceivedProtocolAccepted trigger; in state: " + stateMachine.getState() + "; expecting: CallReceiveProtocolAccepted");
    	}
	}
	public void handleMessage(MAC body) {
    	if(!stateMachine.isInState(State.EncryptedConnected)) {
    		log.debug("Terminal in wrong connection state for EncryptedConnected trigger; in state: " + stateMachine.getState());
    		throw new RuntimeException("Terminal in wrong connection state for ReceivedProtocolAccepted trigger; in state: " + stateMachine.getState() + "; expecting: CallReceiveProtocolAccepted");
    	}
	}

	public void handleMessage(IWant body) {
		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    	if(stateMachine.isInState(State.StartupReceiveIWant)) {
    		isIncoming = true;
    		securityOps.setIsIncoming(true);
    		if(!body.getVatId().equals(getLocalIdentity().getVatId())) {
    			stateMachine.fire(Trigger.SendNotMe);
    		} else {
    			stateMachine.fire(Trigger.ReceivedIWant);
    		}
    	} else {
    		log.debug("Terminal in wrong connection state for IWant msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveIWant");
    		throw new RuntimeException("Terminal in wrong connection state for IWant msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveIWant");
    	}
	}
	public void handleMessage(IAm body) {
		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    	if(stateMachine.isInState(State.StartupReceiveIAm)) {
    		isIncoming = false;
    		securityOps.setIsIncoming(false);
    		getRemoteIdentity().setVatId(body.getVatId());
    		getRemoteIdentity().setPublicKey(body.getPublicKeyImpl());
    		session.fire(new Identified());
    		stateMachine.fire(Trigger.ReceivedIAm);
    	} else {
    		log.debug("Terminal in wrong connection state for IAm msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveIAm");
    		throw new RuntimeException("Terminal in wrong connection state for IAm msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveIAm");
    	}
	}
	public void handleMessage(GiveInfo body) {
		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    	if(stateMachine.isInState(State.StartupReceiveGiveInfo)) {
    		getRemoteIdentity().setVatId(body.getVatId());
    		getRemoteIdentity().setPublicKey(body.getPublicKeyImpl());
    		session.fire(new Identified());
    		stateMachine.fire(Trigger.ReceivedGiveInfo);
    	} else {
    		log.debug("Terminal in wrong connection state for GiveInfo msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveGiveInfo");
    		throw new RuntimeException("Terminal in wrong connection state for GiveInfo msg; in state: " + stateMachine.getState() + "; expecting: StartupReceiveGiveInfo");
    	}
	}
	public void handleMessage(ReplyInfo body) {
		securityOps.addRemoteMessage(body.toFrame().toByteArray());
    	if(stateMachine.isInState(State.IdentifiedStartupReceiveReplyInfo)) {
			securityOps.map.setSelectedProtocolName(securityOps.matchBestCryptoProtocol(body.getCryptoProtocols()));
			securityOps.map.setSelectedEncoderName(securityOps.matchBestDataEncoder(body.getDataEncoders()));

    		stateMachine.fire(Trigger.ReceivedReplyInfo);
    	} else {
    		log.debug("Terminal in wrong connection state for ReplyInfo msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveReplyInfo");
    		throw new RuntimeException("Terminal in wrong connection state for ReplyInfo msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveReplyInfo");
    	}
	}
	public void handleMessage(Go body) {
    	if(stateMachine.isInState(State.IdentifiedStartupReceiveGo)) {
    		byte[] sig = body.getSignature();
    		try {
				byte[] dhParam = securityOps.getDhParam();
			} catch (NoSuchAlgorithmException e2) {
				e2.printStackTrace();
			}
    		securityOps.addRemoteMessage(body.getDiffieHellmanParam());
    		try {
    			getRemoteIdentity().verifySignature(securityOps.getRemoteMessagesBytes(), sig);
    		} catch (SignatureException e) {
    			stateMachine.fire(Trigger.SendBye);
    			return;
    		}
			try {
				securityOps.processOtherSideDhParam(body.getDiffieHellmanParam(), isIncoming);
				securityOps.map.setSelectedProtocolName(body.getCryptoProtocol());
				securityOps.map.setSelectedEncoderName(body.getDataEncoder());
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
    		stateMachine.fire(Trigger.ReceivedGo);
    		try {
    			startupSuccessful(isIncoming);
    			SecurityOps secrets = securityOps.clone();
    			secrets.makeNullLogging();
    			session.fire(new InternalChangeEncryption(secrets));
    		} catch (CloneNotSupportedException e) {
    			e.printStackTrace();
    		}
       	} else {
    		log.debug("Terminal in wrong connection state for Go msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveGo");
    		throw new RuntimeException("Terminal in wrong connection state for Go msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveGo");
    	}
	}

	public void handleMessage(GoToo body) {
    	if(stateMachine.isInState(State.IdentifiedStartupReceiveGoToo)) {
    		securityOps.addRemoteMessage(body.getDiffieHellmanParam());
    		byte[] msgBytes = securityOps.getRemoteMessagesBytes();
    		try {
    			getRemoteIdentity().verifySignature(msgBytes, body.getSignature());
    		} catch (SignatureException e) {
    			stateMachine.fire(Trigger.SendBye);
    			return;
    		}
    		try {
    			securityOps.processOtherSideDhParam(body.getDiffieHellmanParam(), isIncoming);
				securityOps.map.setSelectedProtocolName(body.getCryptoProtocol());
				securityOps.map.setSelectedEncoderName(body.getDataEncoder());
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
    		stateMachine.fire(Trigger.ReceivedGoToo);
    		try {
    			startupSuccessful(isIncoming);
    			SecurityOps secrets = securityOps.clone();
    			secrets.makeNullLogging();
    			session.fire(new InternalChangeEncryption(secrets));
    		} catch (CloneNotSupportedException e) {
    			e.printStackTrace();
    		}
    	} else {
    		log.debug("Terminal in wrong connection state for GoToo msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveGoToo");
    		throw new RuntimeException("Terminal in wrong connection state for GoToo msg; in state: " + stateMachine.getState() + "; expecting: IdentifiedStartupReceiveGoToo");
    	}
	}
	private void startupSuccessful(boolean isIncoming2) {
		securityOps.installOn(session, stack, isIncoming2);
		securityOps.clearSensitiveInfo();
	}
	public void handleMessage(NotMe body) {
	}
	public void handleMessage(DuplicateConnection body) {
	}

	public StateMachineConfig<State,Trigger> buildStateMachineConfig() {
		StateMachineConfig<State, Trigger> sessionConnectionConfig = new StateMachineConfig<State, Trigger>();

		sessionConnectionConfig.configure(State.Initial)
			.permit(Trigger.Calling, State.CallInProgress)
			.permit(Trigger.Answering, State.AnswerInProgress);
		sessionConnectionConfig.configure(State.EncryptedConnected)
			.permit(Trigger.Disconnect, State.Closed);
		sessionConnectionConfig.configure(State.Closed)
			.onEntry(new Action() {
				public void doIt() {
					session.stop();
				}});
		sessionConnectionConfig.configure(State.Startup)
			.permit(Trigger.SendBye, State.IdentifiedStartupSendingBye)
			.permit(Trigger.Disconnect, State.Closed);
		sessionConnectionConfig.configure(State.IdentifiedStartup)
			.permit(Trigger.SendBye, State.IdentifiedStartupSendingBye)
			.permit(Trigger.Disconnect, State.Closed);
		sessionConnectionConfig.configure(State.StartupSendingNotMe)
			.substateOf(State.Startup)
			.onEntry(new Action() {
				public void doIt() {
					sendNotMe();
				}});
		sessionConnectionConfig.configure(State.IdentifiedStartupSendingBye)
			.substateOf(State.IdentifiedStartup)
			.onEntry(new Action() {
				public void doIt() {
					stateMachine.fire(Trigger.Disconnect);
				}});

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
			.permit(Trigger.ReceivedProtocolAccepted, State.StartupSendingIWant);
		sessionConnectionConfig.configure(State.StartupSendingIWant)
			.substateOf(State.Startup)
			.onEntry(new Action() {
				public void doIt() {
					sendIWant();
				}})
			.permit(Trigger.ExpectIAm, State.StartupReceiveIAm);
		sessionConnectionConfig.configure(State.StartupReceiveIAm)
			.substateOf(State.Startup)
			.permit(Trigger.ReceivedIAm, State.StartupSendingGiveInfo);
		sessionConnectionConfig.configure(State.StartupSendingGiveInfo)
			.substateOf(State.Startup)
			.onEntry(new Action() {
				public void doIt() {
					sendGiveInfo();
				}})
			.permit(Trigger.ExpectReplyInfo, State.IdentifiedStartupReceiveReplyInfo);
		sessionConnectionConfig.configure(State.IdentifiedStartupReceiveReplyInfo)
			.substateOf(State.IdentifiedStartup)
			.permit(Trigger.ReceivedReplyInfo, State.IdentifiedStartupSendingGo);
		sessionConnectionConfig.configure(State.IdentifiedStartupSendingGo)
			.substateOf(State.IdentifiedStartup)
			.onEntry(new Action() {
				public void doIt() {
					sendGo();
				}})
			.permit(Trigger.SendBye, State.IdentifiedStartupSendingBye)
			.permit(Trigger.ExpectGoToo, State.IdentifiedStartupReceiveGoToo);
		sessionConnectionConfig.configure(State.IdentifiedStartupReceiveGoToo)
			.substateOf(State.IdentifiedStartup)
			.permit(Trigger.SendBye, State.IdentifiedStartupSendingBye)
			.permit(Trigger.ReceivedGoToo, State.IdentifiedStartupConnecting);
		sessionConnectionConfig.configure(State.IdentifiedStartupConnecting)
			.substateOf(State.IdentifiedStartup)
			.onEntry(new Action() {
				public void doIt() {
					stateMachine.fire(Trigger.Connect);
				}})
			.permit(Trigger.Connect, State.EncryptedConnected);

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
			.permit(Trigger.ExpectIWant, State.StartupReceiveIWant);
		sessionConnectionConfig.configure(State.StartupReceiveIWant)
			.substateOf(State.Startup)
			.permit(Trigger.SendNotMe, State.StartupSendingNotMe)
			.permit(Trigger.ReceivedIWant, State.StartupSendingIAm);
		sessionConnectionConfig.configure(State.StartupSendingIAm)
			.substateOf(State.Startup)
			.onEntry(new Action() {
				public void doIt() {
					sendIAm();
				}})
			.permit(Trigger.ExpectGiveInfo, State.StartupReceiveGiveInfo);
		sessionConnectionConfig.configure(State.StartupReceiveGiveInfo)
			.substateOf(State.Startup)
			.permit(Trigger.ReceivedGiveInfo, State.IdentifiedStartupSendingReplyInfo);
		sessionConnectionConfig.configure(State.IdentifiedStartupSendingReplyInfo)
			.substateOf(State.IdentifiedStartup)
			.onEntry(new Action() {
				public void doIt() {
					sendReplyInfo();
				}})
			.permit(Trigger.ExpectGo, State.IdentifiedStartupReceiveGo);
		sessionConnectionConfig.configure(State.IdentifiedStartupReceiveGo)
			.substateOf(State.IdentifiedStartup)
			.permit(Trigger.SendBye, State.IdentifiedStartupSendingBye)
			.permit(Trigger.ReceivedGo, State.IdentifiedStartupSendingGoToo);
		sessionConnectionConfig.configure(State.IdentifiedStartupSendingGoToo)
			.substateOf(State.IdentifiedStartup)
			.onEntry(new Action() {
				public void doIt() {
					sendGoToo();
					stateMachine.fire(Trigger.Connect);
				}})
			.permit(Trigger.Connect, State.EncryptedConnected);

		return sessionConnectionConfig;
	}
}
