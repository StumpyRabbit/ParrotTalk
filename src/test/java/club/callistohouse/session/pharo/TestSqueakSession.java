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
 *******************************************************************************/

package club.callistohouse.session.pharo;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import club.callistohouse.session.CipherThunkMaker;
import club.callistohouse.session.EncoderThunk;
import club.callistohouse.session.Session;
import club.callistohouse.session.SessionAgent;
import club.callistohouse.session.SessionAgentMap;
import club.callistohouse.session.SessionIdentity;
import club.callistohouse.session.TestSessionServer;
import club.callistohouse.session.payload.SessionASN1Bootstrap;
import club.callistohouse.utils.events.Listener;

public class TestSqueakSession {
	private static Logger log = Logger.getLogger(TestSessionServer.class);

	SessionIdentity server1Identity;
	SessionIdentity server2Identity;
	SessionAgent server2 = null;
	Session term1 = null, term2 = null;
	boolean server2Started = false, server2Stopped = false;
	boolean term2Connected = false, term2Disconnected = false, term2Identified = false, term2Encrypted = false;
	boolean dataReceived = false;
	String msg = "";
	int term2MsgReceived = 0;

	@Before
	public void setup() throws UnknownHostException {
		PropertyConfigurator.configure("log4j.properties");
		server1Identity = new SessionIdentity("first", new InetSocketAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 10011));
		server2Identity = new SessionIdentity("second", 10001);
		SessionASN1Bootstrap.bootstrap();
	}

	@Test(timeout=250000)
	public void test2ServersWithSingleConnect() {
		startServer2();
		assertTrue(server2Started);

		try {
			Thread.sleep(140000);
			assertTrue(term2Connected);
			assertTrue(term2Identified);
			assertTrue(term2Encrypted);

			Thread.sleep(500);
			assertTrue(dataReceived);
			log.info("message received: " + msg);

			if(term1 != null) { term1.stop(); }
			Thread.sleep(1000);
			assertTrue(term2Disconnected);
			server2.stop();
			assertTrue(server2Stopped);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public SessionAgentMap buildServer2Map() {
/**
 * 		Protocols.add(new CipherThunkMaker("DESede", "DESede/CBC/PKCS5Padding", 24, 8, true));
		Protocols.add(new CipherThunkMaker("DES", "DES/CBC/PKCS5Padding", 8, 8, true));
 */
		return new SessionAgentMap(
				new CipherThunkMaker("AESede", "AES/CBC/PKCS5Padding", 32, 16, true),
				new EncoderThunk("Bytes") {
					public Object serializeThunk(Object chunk) {
						return chunk;
					}
					public Object materializeThunk(Object chunk) {
						return chunk;
					}});
	}

	private void startServer2() {
		try {
			server2 = new SessionAgent(server2Identity, buildServer2Map());
			server2.addListener(new Listener<SessionAgent.Started>(SessionAgent.Started.class) {
				protected void handle(SessionAgent.Started event) {
					server2Started = true;
				}});
			server2.addListener(new Listener<SessionAgent.Stopped>(SessionAgent.Stopped.class) {
				protected void handle(SessionAgent.Stopped event) {
					server2Stopped = true;
				}});
			server2.addListener(new Listener<Session.Connected>(Session.Connected.class) {
				protected void handle(Session.Connected event) {
					setupListenersOnTerminal2(event.terminal);
					term2Connected = true;
				}});
			server2.addListener(new Listener<Session.Disconnected>(Session.Disconnected.class) {
				protected void handle(Session.Disconnected event) {
					term2Disconnected = true;
				}});
			server2.start();
		} catch (Exception e) { assertTrue(false); }
	}

	private void setupListenersOnTerminal2(Session term) {
		term.addListener(new Listener<Session.Connected>(Session.Connected.class) {
			protected void handle(Session.Connected event) {
				term2Connected = true;
			}});
		term.addListener(new Listener<Session.Disconnected>(Session.Disconnected.class) {
			protected void handle(Session.Disconnected event) {
				term2Disconnected = true;
			}});
		term.addListener(new Listener<Session.Encrypted>(Session.Encrypted.class) {
			protected void handle(Session.Encrypted event) {
				term2Encrypted = true;
			}});
		term.addListener(new Listener<Session.Identified>(Session.Identified.class) {
			protected void handle(Session.Identified event) {
				term2Identified = true;
			}});
		term.addListener(new Listener<Session.DataReceived>(Session.DataReceived.class) {
			protected void handle(Session.DataReceived event) {
				term2MsgReceived++;
			}});
	}
}
