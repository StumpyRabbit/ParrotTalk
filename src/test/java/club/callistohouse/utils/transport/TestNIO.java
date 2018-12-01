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

package club.callistohouse.utils.transport;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import club.callistohouse.utils.events.Listener;
import club.callistohouse.utils.transport.NIOConnection;
import club.callistohouse.utils.transport.NIOServer;
import club.callistohouse.utils.transport.NIOConnection.ClientConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionDisconnected;
import club.callistohouse.utils.transport.NIOConnection.DataReceived;
import club.callistohouse.utils.transport.NIOServer.NIOServerStarted;
import club.callistohouse.utils.transport.NIOServer.NIOServerStopped;

public class TestNIO {
	NIOServer server1 = null, server2 = null;
	NIOConnection client1 = null;
	NIOConnection client2 = null;
	boolean server1Started = false, server1Stopped = false;
	boolean server2Started = false, server2Stopped = false;
	boolean client1Connected = false, client1Disconnected = false, client1DataReceived = false;
	boolean client2Connected = false, client2Disconnected = false, client2DataReceived = false;

	@Before
	public void setup() {
		PropertyConfigurator.configure("log4j.properties");
	}

	@Test
	public void test2ServersWithSingleConnect() throws Exception {
		assertFalse(server1Started);
		assertFalse(server2Started);
		startServers();
		Thread.sleep(100);
		assertTrue(server1Started);
		assertTrue(server2Started);

		assertFalse(client1Connected);
		assertFalse(client2Connected);
		client1 = server1.connect(getServer2ISA(), "number-2");
		Thread.sleep(300);
		assertTrue(client1Connected);
		assertTrue(client2Connected);
		assertNotNull(client1);
		assertNotNull(client2);

		assertFalse(client1DataReceived);
		assertFalse(client2DataReceived);

		client1.send("hello world".getBytes());
		Thread.sleep(100);
		assertTrue(client2DataReceived);

		client2.send("hello world".getBytes());
		Thread.sleep(50);
		assertTrue(client1DataReceived);

		assertFalse(client1Disconnected);
		assertFalse(client2Disconnected);
		if(client1 != null) { client1.stop(null); }
		Thread.sleep(150);
		assertTrue(client1Disconnected);
		assertTrue(client2Disconnected);

		assertFalse(server1Stopped);
		assertFalse(server2Stopped);
		server1.stop();
		server2.stop();
		Thread.sleep(20);
		assertTrue(server1Stopped);
		assertTrue(server2Stopped);
	}

	private InetSocketAddress getServer1ISA() throws UnknownHostException { return new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 11001); }
	private InetSocketAddress getServer2ISA() throws UnknownHostException { return new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 11002); }

	private void startServers() {
		try {
			server1 = new NIOServer(getServer1ISA(), "first");
			server1.addListener(new Listener<NIOServerStarted>(NIOServerStarted.class) {
				protected void handle(NIOServerStarted event) {
					server1Started = true;
				}});
			server1.addListener(new Listener<NIOServerStopped>(NIOServerStopped.class) {
				protected void handle(NIOServerStopped event) {
					server1Stopped = true;
				}});
			server1.addListener(new Listener<ClientConnected>(ClientConnected.class) {
				protected void handle(ClientConnected event) {
					event.connection.addListener(new Listener<DataReceived>(DataReceived.class) {
						protected void handle(DataReceived event) {
							client1DataReceived = true;
						}});
					client1Connected = true;
				}});
			server1.addListener(new Listener<ConnectionConnected>(ConnectionConnected.class) {
				protected void handle(ConnectionConnected event) {
				}});
			server1.addListener(new Listener<ConnectionDisconnected>(ConnectionDisconnected.class) {
				protected void handle(ConnectionDisconnected event) {
					client1Disconnected = true;
				}});
			server1.start();

			server2 = new NIOServer(getServer2ISA(), "second");
			server2.addListener(new Listener<NIOServerStarted>(NIOServerStarted.class) {
				protected void handle(NIOServerStarted event) {
					server2Started = true;
				}});
			server1.addListener(new Listener<NIOServerStopped>(NIOServerStopped.class) {
				protected void handle(NIOServerStopped event) {
					server2Stopped = true;
				}});
			server2.addListener(new Listener<ClientConnected>(ClientConnected.class) {
				protected void handle(ClientConnected event) {
				}});
			server2.addListener(new Listener<ConnectionConnected>(ConnectionConnected.class) {
				protected void handle(ConnectionConnected event) {
					client2 = event.connection;
					event.connection.addListener(new Listener<DataReceived>(DataReceived.class) {
						protected void handle(DataReceived event) {
							client2DataReceived = true;
						}});
					client2Connected = true;
				}});
			server2.addListener(new Listener<ConnectionDisconnected>(ConnectionDisconnected.class) {
				protected void handle(ConnectionDisconnected event) {
					client2Disconnected = true;
				}});
			server2.start();
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
