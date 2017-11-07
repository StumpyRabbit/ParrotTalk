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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.session.CipherThunkMaker;
import club.callistohouse.session.EncoderThunk;
import club.callistohouse.session.Session;
import club.callistohouse.session.SessionAgent;
import club.callistohouse.session.SessionAgentMap;
import club.callistohouse.session.SessionIdentity;
import club.callistohouse.session.TestSessionServer;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.SessionASN1Bootstrap;
import club.callistohouse.utils.events.Listener;

public class TestPharoSession {
	private static Logger log = Logger.getLogger(TestSessionServer.class);

	SessionIdentity server1Identity;
	SessionIdentity server2Identity;
	SessionAgent server1 = null, server2 = null;
	Session term1 = null, term2 = null;
	boolean server1Started = false, server1Stopped = false;
	boolean term1Connected = false, term1Disconnected = false, term1Identified = false, term1Encrypted = false;
	boolean dataReceived = false;
	String msg = "";
	int term1MsgReceived = 0;

	@Before
	public void setup() throws UnknownHostException {
		PropertyConfigurator.configure("log4j.properties");
		server1Identity = new SessionIdentity("first", 10101);
		server2Identity = new SessionIdentity("second", new InetSocketAddress(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), 12222));
		SessionASN1Bootstrap.bootstrap();
	}

	@Test(timeout=25000)
	public void test2ServersWithSingleConnect() {
		startServer1();
		assertTrue(server1Started);

		try {
			term1 = server1.connect(server2Identity);
			Thread.sleep(14000);
			assertTrue(term1Connected);
			assertTrue(term1Identified);
			assertTrue(term1Encrypted);

			term1.send("hello world".getBytes());
			Thread.sleep(500);
			assertTrue(dataReceived);
			log.info("message received: " + msg);

			if(term1 != null) { term1.stop(); }
			Thread.sleep(1000);
			assertTrue(term1Disconnected);
			server1.stop();
			assertTrue(server1Stopped);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public SessionAgentMap buildServer1Map() {
/**
 * 		Protocols.add(new CipherThunkMaker("DESede", "DESede/CBC/PKCS5Padding", 24, 8, true));
		Protocols.add(new CipherThunkMaker("DES", "DES/CBC/PKCS5Padding", 8, 8, true));
 */
		return new SessionAgentMap(
				new CipherThunkMaker("AESede", "AES/CBC/PKCS5Padding", 32, 16, true),
				new EncoderThunk("String") {
					public Object serializeThunk(Object chunk) {
						return chunk;
					}
					public Object materializeThunk(Object chunk) {
						return new String((byte[]) chunk);
					}});
	}

	private void startServer1() {
		try {
			server1 = new SessionAgent(server1Identity, buildServer1Map());
			server1.addListener(new Listener<SessionAgent.Started>(SessionAgent.Started.class) {
				protected void handle(SessionAgent.Started event) {
					server1Started = true;
				}});
			server1.addListener(new Listener<SessionAgent.Stopped>(SessionAgent.Stopped.class) {
				protected void handle(SessionAgent.Stopped event) {
					server1Stopped = true;
				}});
			server1.addListener(new Listener<Session.Connected>(Session.Connected.class) {
				protected void handle(Session.Connected event) {
					setupListenersOnTerminal1(event.terminal);
					term1Connected = true;
				}});
			server1.addListener(new Listener<Session.Disconnected>(Session.Disconnected.class) {
				protected void handle(Session.Disconnected event) {
					term1Disconnected = true;
				}});
			server1.start();
		} catch (Exception e) { assertTrue(false); }
	}

	private void setupListenersOnTerminal1(Session term) {
		term.addListener(new Listener<Session.Connected>(Session.Connected.class) {
			protected void handle(Session.Connected event) {
				term1Connected = true;
			}});
		term.addListener(new Listener<Session.Disconnected>(Session.Disconnected.class) {
			protected void handle(Session.Disconnected event) {
				term1Disconnected = true;
			}});
		term.addListener(new Listener<Session.Encrypted>(Session.Encrypted.class) {
			protected void handle(Session.Encrypted event) {
				term1Encrypted = true;
			}});
		term.addListener(new Listener<Session.Identified>(Session.Identified.class) {
			protected void handle(Session.Identified event) {
				term1Identified = true;
			}});
		term.addListener(new Listener<Session.DataReceived>(Session.DataReceived.class) {
			protected void handle(Session.DataReceived event) {
				dataReceived = true;
				msg = (String) event.data;
				term1MsgReceived++;
			}});
	}
}