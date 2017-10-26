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

import club.callistohouse.utils.events.EventEngine;

/**
 * The key to this subsystem is the use of the event framework. This class, as well
 * as the NIOServer class, utilize public events, while the EventfulConversation
 * uses private events. The NIOServer listeners, on the conversation, are the party
 * which fires the connection events, as it has the map between the channel and the
 * connections.  
 * 
 * Public events:
 * 	- NIOConnection.ClientConnected
 * 	- NIOConnection.ConnectionConnected
 * 	- NIOConnection.ConnectionDisconnected
 * 	- NIOConnection.DataReceived
 * 
 * @author Robert Withers
 *
 */
public class NIOConnection extends EventEngine {

	private NIOConversation conversation;
	private SocketChannel channel;
	private InetSocketAddress isa;

	public NIOConnection(InetSocketAddress address, NIOConversation conversation) {
		this.isa = address;
		this.conversation = conversation;
	}

	public InetSocketAddress getSocketAddress() { return isa; }
	public SocketChannel getChannel() { return channel; }
	public void setChannel(SocketChannel channel) { this.channel = channel; }

	public void send(byte[] bytes) throws InterruptedException { conversation.send(channel, bytes, bytes.length); }
	public void dataReceived(byte[] bytes) {
		fire(new DataReceived(bytes));
	}
	public void start() {}
	public void stop(Throwable e) { 
		try {
			conversation.disconnect(channel);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		}

	public static class ClientConnected {
		public NIOConnection connection;
		public ClientConnected(NIOConnection connection) { this.connection = connection; }
	}
	public static class ConnectionConnected {
		public NIOConnection connection;
		public ConnectionConnected(NIOConnection connection) { this.connection = connection; }
	}
	public static class ConnectionDisconnected {
		public NIOConnection connection;
		public ConnectionDisconnected(NIOConnection connection) { this.connection = connection; }
	}
	public static class DataReceived {
		public byte[] data;
		public DataReceived(byte[] data) { this.data = data; }
	}
}
