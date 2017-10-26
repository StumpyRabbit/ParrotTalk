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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import club.callistohouse.utils.events.EventEngine;
import club.callistohouse.utils.transport.NIOConnection.ClientConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionConnected;
import club.callistohouse.utils.transport.NIOConnection.ConnectionDisconnected;

/**
 * The key to this transport subsystem is the use of the event framework. This class, as well 
 * as the NIOFrameConnection class, utilize public events, while the NIOEventfulConversation 
 * uses private events. The NIOFrameConnection class is what fires the FrameReceived events.
 * 
 * Public events: 
 * - NIOServer.ServerStarted 
 * - NIOServer.ServerStopped 
 * - NIOConnection.ClientConnected 
 * - NIOConnection.ConnectionConnected 
 * - NIOConnection.ConnectionDisconnected 
 * 
 * @author Robert Withers
 *
 */
public class NIOServer extends EventEngine {
//	private static Logger log = Logger.getLogger(NIOServer.class);

	private int port;
	private String nickname;
	private NIOConversation conversation;
	private Map<SocketChannel, NIOConnection> connections = new HashMap<SocketChannel, NIOConnection>();

	public NIOServer(int port) { this(port, ""); }
	public NIOServer(InetSocketAddress isa) { this(isa.getPort(), ""); }
	public NIOServer(InetSocketAddress isa, String nickname) { this(isa.getPort(), nickname); }
	public NIOServer(int port, String nickname) { 
		this.port = port; 
		this.nickname = nickname; 
	}

	public void start() {
		if (conversation != null)
			return;
		conversation = new NIOConversation(nickname, this);
		try {
			conversation.listen(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		conversation.start();
		fire(new NIOServerStarted());
	}

	public void stop() {
		if (conversation == null)
			return;
		conversation.stopListening();
		conversation = null;
		fire(new NIOServerStopped());
	}

	public NIOConnection connect(InetSocketAddress address, String nickname) {
		NIOConnection client = new NIOConnection(address, conversation);
		try {
			SocketChannel channel = conversation.connect(address.getAddress(), address.getPort());
			connections.put(channel, client);
			client.setChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.start();
		return client;
	}

	public boolean hasConnection(SocketChannel channel) { return connections.containsKey(channel); }
	public NIOConnection getConnection(SocketChannel channel) { return connections.get(channel); }

	public String toString() { return getClass().getSimpleName() + "(" + port + ")"; }

	public void handleClientChannelConnectedEvent(SocketChannel channel) {
		NIOConnection conn = getConnection(channel);
		if(conn == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		conn = getConnection(channel);
		if(conn == null) {
			throw new RuntimeException("no connection registered");
		}
		ClientConnected clientConnected = new ClientConnected(conn);
		fire(clientConnected);
		conn.fire(clientConnected);
	}
	public void handleConnectionChannelConnectedEvent(SocketChannel channel) {
		InetSocketAddress isa = new InetSocketAddress(channel.socket().getInetAddress(), channel.socket().getPort());
		NIOConnection conn = new NIOConnection(isa, NIOServer.this.conversation);
		conn.setChannel(channel);
		connections.put(channel, conn);
		ConnectionConnected connectionConnected = new ConnectionConnected(conn);
		fire(connectionConnected);
		conn.fire(connectionConnected);
		conn.start();
	}
	public void handleConnectionChannelDisconnectedEvent(SocketChannel channel) {
		NIOConnection conn = getConnection(channel);
		connections.remove(channel);		
		ConnectionDisconnected connectionDisconnected = new ConnectionDisconnected(conn);
		fire(connectionDisconnected);
		if(conn != null) {
			conn.fire(connectionDisconnected);
		}
	}

	public class NIOServerStarted {}
	public class NIOServerStopped {}
}
