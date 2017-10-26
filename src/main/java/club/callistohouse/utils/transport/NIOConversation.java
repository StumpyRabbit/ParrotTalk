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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Robert Withers
 *
 */
public class NIOConversation extends Thread {
	private static Logger log = Logger.getLogger(NIOConversation.class);

	private NIOServer server;
	private Selector socketSelector;
	private ServerSocketChannel serverSocketChannel;
	private boolean selectorRunning = true;
	private final List<NIOEventfulChangeRequest> pendingChanges = new LinkedList<NIOEventfulChangeRequest>();
	private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
	private ByteBuffer readBuffer = ByteBuffer.allocate(16384);

	public NIOConversation(String nickname, NIOServer server) {
		super(nickname + "-conversation");
		this.server = server;
	}

	@Override
	public void run() {
		while(selectorRunning) {
			x_select();
		}
	}

	public void send(SocketChannel socketChannel, byte[] data, int length) {
		if(data.length != length) {
			throw new RuntimeException("NIO send: bad msg length");
		}
		synchronized (pendingChanges) {
			pendingChanges.add(new NIOEventfulChangeRequest(
					socketChannel, 
					NIOEventfulChangeRequest.CHANGEOPS, 
					SelectionKey.OP_WRITE));
		}
		synchronized (pendingData) {
			List<ByteBuffer> queue = pendingData.get(socketChannel);
			if (queue == null) {
				queue = new ArrayList<ByteBuffer>();
				pendingData.put(socketChannel, queue);
			}
			ByteBuffer buf = ByteBuffer.wrap(data);
//			log.debug("scheduling writing byte buffer size: " + buf.remaining());
			queue.add(buf);
		}
		socketSelector.wakeup();
	}

	public void listen(int port) throws IOException {
		log.debug("listen: " + port);
		if(serverSocketChannel != null)
			return;
		socketSelector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().setReuseAddress(true);  // enable SO_REUSEADDR
		serverSocketChannel.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
		for (int i = 0; i < 3; i++) {
			try {
				serverSocketChannel.socket().bind(isa);
			} catch(Exception e) {}
		}
		log.debug("listening: " + isa);
		serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT, port);
	}

	public SocketChannel connect(InetAddress hostAddress, int port) throws IOException, InterruptedException {
//		log.info("connect: " + hostAddress + ":" + port);
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.setOption(StandardSocketOptions.SO_LINGER, 0);
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(hostAddress, port));
		synchronized (pendingChanges) {
			pendingChanges.add(new NIOEventfulChangeRequest(
					socketChannel, 
					NIOEventfulChangeRequest.REGISTER, 
					SelectionKey.OP_CONNECT));
		}
		socketSelector.wakeup();
		return socketChannel;
	}

	public void disconnect(SocketChannel channel) throws IOException, InterruptedException {
		channel.close();
		if(pendingData.containsKey(channel)) {
			pendingData.remove(channel);
		}
		server.handleConnectionChannelDisconnectedEvent(channel);
	}

	public void stopListening() {
		selectorRunning = false;
		if (serverSocketChannel != null) {
			try {
				serverSocketChannel.close();
				SelectionKey selectionKey = serverSocketChannel.keyFor(socketSelector);
				if (selectionKey != null) {
					selectionKey.cancel();
				}
				if (socketSelector != null) {
					for (SelectionKey key : socketSelector.selectedKeys()) {
						key.cancel();
						try {
							SocketChannel channel = (SocketChannel) key.channel();
							server.handleConnectionChannelDisconnectedEvent(channel);
						} catch(Exception e) { continue; }
					}
				}
				serverSocketChannel = null;
//				log.info("serverSocketChannel closed");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private int x_read(SelectionKey key) throws IOException {
		int bytesRead = 0;
		SocketChannel channel = (SocketChannel) key.channel();
		readBuffer.clear();
		try {
			bytesRead = channel.read(readBuffer);
			if(bytesRead == -1) {
				key.cancel();
				disconnect(channel);
			}
//				log.debug("NIO: raw read count: " + numRead);
		} catch (IOException e) {
//				log.debug("read blows up", e);
			channel.close();
			key.cancel();
			throw new ReadException("NIO read failed: connection closed", e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		byte[] data = new byte[readBuffer.position()];
		readBuffer.flip();
		readBuffer.get(data, 0, data.length);
		NIOConnection conn = server.getConnection(channel);
		if(conn != null) {
			conn.dataReceived(data);
		}
		return bytesRead;
	}

	private int x_write(SelectionKey key) throws IOException {
		synchronized (pendingData) {
			SocketChannel channel = (SocketChannel) key.channel();
			List<ByteBuffer> queue = pendingData.get(channel);
			int bytesWritten = 0;
			if (queue == null || queue.isEmpty()) {
				key.interestOps(SelectionKey.OP_READ);
				return bytesWritten;
			}
			while (!queue.isEmpty()) {
				ByteBuffer byteBuffer = queue.get(0);
				while(byteBuffer.remaining() > 0) {
					bytesWritten += channel.write(byteBuffer);
				}
				queue.remove(0);
			}
			if (queue.isEmpty()) {
				key.interestOps(SelectionKey.OP_READ);
			}
			return bytesWritten;
		}
	}

	private void x_accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		log.debug("x_accept: " + channel.getLocalAddress() + " from " + channel.getRemoteAddress());
		channel.setOption(StandardSocketOptions.SO_LINGER, 0);
		channel.configureBlocking(false);
		channel.register(socketSelector, SelectionKey.OP_READ);
		server.handleConnectionChannelConnectedEvent(channel);
	}

	private void x_finishConnection(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
//		log.info("finish Connect ChangeRequest(channel: " + channel + ")");
		try {
			channel.finishConnect();
			channel.setOption(StandardSocketOptions.SO_LINGER, 0);
		} catch (IOException e) {
			e.printStackTrace();
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_READ);
		server.handleClientChannelConnectedEvent(channel);
	}

	private void x_select() {
		if(selectorRunning) {
			synchronized (pendingChanges) {
				Iterator<NIOEventfulChangeRequest> selectedKeys = pendingChanges.iterator();
				while (selectedKeys.hasNext()) {
					NIOEventfulChangeRequest change = selectedKeys.next();
					switch (change.type) {
						case NIOEventfulChangeRequest.CHANGEOPS: {
							SelectionKey key = change.socket.keyFor(socketSelector);
							if (key == null) {
								continue;
							}
							key.interestOps(change.ops);
							continue;
						}
						// Client only:
						case NIOEventfulChangeRequest.REGISTER: {
							try {
								change.socket.register(socketSelector, change.ops);
							} catch (ClosedChannelException e) {
								e.printStackTrace();
							}
							continue;
						}

					}
				}
				pendingChanges.clear();
			}
			try {
				socketSelector.select(100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Iterator<SelectionKey> selectedKeys = socketSelector.selectedKeys().iterator();
			while (selectedKeys.hasNext() && selectorRunning) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				if (!key.isValid())
					continue;
				try {
					if (key.isAcceptable()) {
//						log.info("accept with a ListenConnection: " + key);
						x_accept(key);
					} else if (key.isConnectable()) {
//						log.info("finishConnection: " + key);
						x_finishConnection(key);
					} else if (key.isReadable()) {
						x_read(key);
					} else if (key.isWritable()) {
						x_write(key);
					}
				} catch (IOException e) {
//					log.debug("connection blows up", e);
					try {
						disconnect((SocketChannel) key.channel());
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public static class ClientChannelConnectedEvent {
		public SocketChannel channel;
		public ClientChannelConnectedEvent(SocketChannel channel) { this.channel = channel; }
	}
	public static class ConnectionChannelConnectedEvent {
		public SocketChannel channel;
		public ConnectionChannelConnectedEvent(SocketChannel channel) { this.channel = channel; }
	}
	public static class ConnectionChannelDisconnectedEvent {
		public SocketChannel channel;
		public Throwable throwable;
		public ConnectionChannelDisconnectedEvent(SocketChannel channel) { this.channel = channel; }
		public ConnectionChannelDisconnectedEvent(SocketChannel channel, Throwable throwable) { this.channel = channel; this.throwable = throwable; }
	}
	public static class DataReceivedEvent {
		public byte[] data;
		public SocketChannel channel;
		public DataReceivedEvent(SocketChannel channel, byte[] data) { this.channel = channel; this.data = data; }
	}

	public static class NIOEventfulChangeRequest {

	    public static final int REGISTER = 1;
	    public static final int CHANGEOPS = 2;

	    public SocketChannel socket;
	    public int type;
	    public int ops;

		public NIOEventfulChangeRequest(SocketChannel socket, int type, int ops) {
			this.socket = socket;
			this.type = type;
			this.ops = ops;
		}
	}
}

