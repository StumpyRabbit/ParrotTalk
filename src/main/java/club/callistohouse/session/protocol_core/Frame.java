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

package club.callistohouse.session.protocol_core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.utils.ArrayUtil;
import club.callistohouse.utils.IntUtil;
import club.callistohouse.utils.events.Listener;

public class Frame {
	public static int specificationSize() { return 8; }
	public static Frame onFrameSpecification(byte[] bytes) { return new Frame().initOnSpecification(bytes); }
	public static Frame readFromStream(ByteArrayInputStream stream) throws IOException {
		Frame frame = new Frame();
		byte[] spec = new byte[8];
		stream.read(spec);
		frame.initOnSpecification(spec);
		frame.readRemainderFrom(stream);
		return frame;
	}

	private Frame initOnSpecification(byte[] bytes) {
		if(bytes.length != specificationSize()) {
			throw new RuntimeException("bad spec size");
		}
		this.frameSpecification = bytes;
		return this;
	}

	protected byte[] frameSpecification = new byte[8];
	protected PhaseHeader header;
	protected Object payload;

	public Frame() { setFrameVersion(1); }
	public Frame(PhaseHeader header) {
		this(header, new byte[0]);
	}
	public Frame(PhaseHeader header, Object pload) {
		this();
		setHeader(header);
		setPayload(pload);
	}

	public int getTags() { return frameSpecification[0] & 0x0F; }
	public void setTags(int tags) { frameSpecification[0] = (byte) ((frameSpecification[0] & 0xF0) | (tags & 0x0F)); }
	public int getMulticast() { return ((frameSpecification[0] & 0xF0) >>> 4) | ((frameSpecification[1] & 0x3F) << 4); }
	public void setMulticast(int multicast) {
		frameSpecification[0] = (byte) ((frameSpecification[0] & 0x0F) | ((multicast & 0x0F) << 4));
		frameSpecification[1] = (byte) ((frameSpecification[1] & 0xC0) | ((multicast & 0x3F0) >>> 4));
	}
	public int getHash() { return ((frameSpecification[1] & 0xC0) >>> 6) | ((frameSpecification[2] & 0xFF) << 6); }
	public void setHash(int hash) {
		frameSpecification[1] = (byte) ((frameSpecification[0] & 0x3F) | ((hash & 0x03) << 6));
		frameSpecification[2] = (byte) ((hash & 0x3FC) >>> 2);
	}
	public int getFrameVersion() { return frameSpecification[3] & 0x01; }
	public void setFrameVersion(int version) { frameSpecification[3] = (byte) ((frameSpecification[3] & 0xFE) | (version & 0x01)); }
	public int getPriority() { return (frameSpecification[3] & 0x06) >>> 2; }
	public void setPriority(int priority) { frameSpecification[3] = (byte) ((frameSpecification[3] & 0xF9) | (priority & 0x03) << 2); }
	public int getHeaderType() { return (frameSpecification[3] & 0xF8) >>> 3; }
	public void setHeaderType(int type) { frameSpecification[3] = (byte) ((frameSpecification[3] & 0x07) | ((type & 0x1F) << 3)); 	}
	public int getMessageSize() { return IntUtil.byteArrayToInt(Arrays.copyOfRange(frameSpecification, 4, 8)); }
	public void setMessageSize(int size) { System.arraycopy(IntUtil.intToByteArray(size), 0, frameSpecification, 4, 4); }

	public PhaseHeader getHeader() { return header; }
	public void setHeader(PhaseHeader header) {
		this.header = header;
		header.setFrame(this);
		header.addListener(new Listener<String>(String.class) {
			protected void handle(String event) {
				recomputeSpec();
			}});
		recomputeSpec();
	}
	public Object getPayload() { return payload; }
	public void setPayload(Object payload) {
		this.payload = payload;
		recomputeSpec();
	}

	public byte[] toByteArray() {
		byte[] hdrBytes = getHeader().toByteArray();
		byte[] payloadBytes = new byte[0];
		if(payload instanceof byte[]) {
			payloadBytes = (byte[]) payload;
		}
		return ArrayUtil.concatAll(frameSpecification, hdrBytes, payloadBytes);
	}

	public int getSpecSize() { return specificationSize(); }
	public int getHeaderSize() { return header.toByteArray().length; }
	public int getPayloadSize() { return getMessageSize() - getSpecSize() - getHeaderSize(); }

	public String toString() { return "Frame<" + header.toString() + " payloadSize: " + getPayloadSize() + ">"; }

	private void recomputeSpec() {
		setMessageSize(getSpecSize() + getHeaderSize() + computePayloadSize());
		setHeaderType(header.getType().getId());
	}
	private int computePayloadSize() { return (payload instanceof byte[]) ? ((byte[]) payload).length : 0; }

	public void readRemainderFrom(ByteArrayInputStream baiStream) throws IOException {
		try {
			byte[] bytes = new byte[baiStream.available()];
			baiStream.read(bytes);
			ByteArrayInputStream shortStream = new ByteArrayInputStream(bytes);
			this.header = PhaseHeader.readFrom(shortStream);
			header.setFrame(this);
			bytes = new byte[getPayloadSize()];
			shortStream.read(bytes);
			setPayload(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readFrom(ByteArrayInputStream stream) throws IOException {
		frameSpecification = new byte[Frame.specificationSize()];
		stream.read(frameSpecification);
		readRemainderFrom(stream);
	}
}
