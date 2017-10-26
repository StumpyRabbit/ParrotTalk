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

package club.callistohouse.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import club.callistohouse.utils.events.EventEngine;
import club.callistohouse.utils.events.Listener;
import club.callistohouse.utils.transport.NIOConnection;
import club.callistohouse.utils.transport.NIOConnection.ClientConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionDisconnected;
import club.callistohouse.utils.transport.NIOServer;

/**
 * The key to this session subsystem is again the use of the event framework. This class fires
 * the following events
 * 
 * Public events: 
 * - SessionServer.Started 
 * - SessionServer.Stopped 
 * - SessionTerminal.ClientConnected 
 * - SessionTerminal.ConnectionConnected 
 * - SessionTerminal.ConnectionDisconnected 
 * 
 * @author Robert Withers
 *
 */
public class SessionAgent extends EventEngine {
//	private static Logger log = Logger.getLogger(SessionServer.class);

	public static class Started {}
	public static class Stopped {}

	private SessionIdentity nearKey;
	private SessionAgentMap map;
	private NIOServer transportServer;
	private Map<InetSocketAddress,Session> sessionTable = new HashMap<InetSocketAddress,Session>();

	public SessionAgent(SessionIdentity id) {
		this(id, new SessionAgentMap());
	}
	public SessionAgent(SessionIdentity id, SessionAgentMap map) {
		this.nearKey = id;
		this.map = map;
		transportServer = new NIOServer(nearKey.getPort(), nearKey.getDomain());
		setupListenersOnTransportServer();
	}

	public void start() { transportServer.start(); fire(new Started()); }
	public void stop() { transportServer.stop(); fire(new Stopped()); }
	public SessionIdentity getNearKey() { return nearKey; }
	public SessionAgentMap getMap() { return map; }

	public Session connect(SessionIdentity remoteId) {
		if (remoteId.getVatId() == null) { throw new IllegalArgumentException("no VatId"); }
		Session session = new Session(remoteId, this, map);
		sessionTable.put(remoteId.getSocketAddress(), session);
		NIOConnection connection = transportServer.connect(remoteId.getSocketAddress(), remoteId.getDomain());
		session.setConnection(connection);
		try {
			session.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (remoteId.getVatId() != null) {
			session.call();
			try {
				Thread.sleep(25);
			} catch(InterruptedException e) {}
		} else
			throw new IllegalArgumentException("bad identity: " + remoteId);
		return session;
	}

	protected void handleSessionRunning(Session session) {
		Session.Running running = new Session.Running(session);
		fire(running);
		session.fire(running);
	}
	protected void handleClientConnected(NIOConnection connection) {
		SessionIdentity sessionId = new SessionIdentity(connection.getSocketAddress());
		if (sessionTable.containsKey(sessionId.getSocketAddress())) {
			Session term = sessionTable.get(sessionId.getSocketAddress());
			Session.Connected termConnected = new Session.Connected(term);
			fire(termConnected);
			term.fire(termConnected);
		} else {
			throw new RuntimeException("no terminal for connection");
		}
	}

	protected void handleConnectionConnected(NIOConnection connection) {
		SessionIdentity farKey = new SessionIdentity(connection.getSocketAddress());
		Session term = new Session(farKey, this, map, connection);
		sessionTable.put(farKey.getSocketAddress(), term);
		Session.Connected termConnected = new Session.Connected(term);
		fire(termConnected);
		term.fire(termConnected);
		try {
			term.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		term.answer();
		try {
			Thread.sleep(25);
		} catch(InterruptedException e) {}
	}

	protected void handleConnectionDisconnected(NIOConnection connection) {
		if(connection == null){
			return;
		}
		SessionIdentity sessionId = new SessionIdentity(connection.getSocketAddress());
		if (sessionTable.containsKey(sessionId)) {
			Session term = sessionTable.get(sessionId);
			sessionTable.remove(sessionId.getSocketAddress());
			Session.Disconnected termDisconnected = new Session.Disconnected(term); 
			fire(termDisconnected);
			term.fire(termDisconnected);
		} else {
			throw new RuntimeException("no terminal for disconnection");
		}
	}

	private void setupListenersOnTransportServer() {
		transportServer.addListener(new Listener<ClientConnected>(ClientConnected.class) {
			protected void handle(ClientConnected event) {
				handleClientConnected(event.connection);
			}
		});
		transportServer.addListener(new Listener<ConnectionConnected>(ConnectionConnected.class) {
			protected void handle(ConnectionConnected event) {
				handleConnectionConnected(event.connection);
			}
		});
		transportServer.addListener(new Listener<ConnectionDisconnected>(ConnectionDisconnected.class) {
			protected void handle(ConnectionDisconnected event) {
				handleConnectionDisconnected(event.connection);
			}
		});
	}
}

