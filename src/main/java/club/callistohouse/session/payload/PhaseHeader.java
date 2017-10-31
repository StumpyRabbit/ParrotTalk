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

package club.callistohouse.session.payload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1Module;
import club.callistohouse.asn1.ASN1OutputStream;
import club.callistohouse.asn1.types.ASN1Type;

public abstract class PhaseHeader {

	private static Map<Integer, Class<?>> headerClasses = new HashMap<Integer, Class<?>>();

	static {
		headerClasses.put(new ProtocolOffered().getId(), ProtocolOffered.class);
		headerClasses.put(new ProtocolAccepted().getId(), ProtocolAccepted.class);
		headerClasses.put(new Encoded().getId(), Encoded.class);
		headerClasses.put(new Encrypted().getId(), Encrypted.class);
		headerClasses.put(new MAC().getId(), MAC.class);
		headerClasses.put(new IWant().getId(), IWant.class);
		headerClasses.put(new IAm().getId(), IAm.class);
		headerClasses.put(new GiveInfo().getId(), GiveInfo.class);
		headerClasses.put(new ReplyInfo().getId(), ReplyInfo.class);
		headerClasses.put(new Go().getId(), Go.class);
		headerClasses.put(new GoToo().getId(), GoToo.class);
		headerClasses.put(new DuplicateConnection().getId(), DuplicateConnection.class);
		headerClasses.put(new NotMe().getId(), NotMe.class);
		headerClasses.put(new RawData().getId(), RawData.class);
		headerClasses.put(new InternalChangeEncryption().getId(), InternalChangeEncryption.class);
	}

	private Frame frame;

	public PhaseHeader() {}
	public PhaseHeader(Frame frame) { this.frame = frame; }

	public Integer getId() { int i = getType().getId(); return i; }
	public abstract MessageEnum getType();

	public int getHeaderSize() { return toByteArray().length; }

	public String description() { return getType().description(); }

	public Frame getFrame() { return frame; }
	public void setFrame(Frame frame) { this.frame = frame; }

	public static Class<?> headerClassForType(int headerType) { return headerClasses.get(headerType); }

	public Frame toFrame() { return (frame != null) ? frame : new Frame(this); }

	public byte[] toByteArray() {
		ASN1Type type = (ASN1Type) ASN1Module.name("Session").find("PhaseHeader");
		try (ASN1OutputStream outStream = new ASN1OutputStream()) {
			return outStream.encode(this, type);
		} catch (IOException e1) {
			throw new RuntimeException(e1.getMessage());
		}
	}

	public static PhaseHeader readFrom(ByteArrayInputStream inStream)
			throws InstantiationException, IllegalAccessException, IOException {
		ASN1Type type = (ASN1Type) ASN1Module.name("Session").find("PhaseHeader");
		try (ASN1InputStream derStream = new ASN1InputStream(inStream)) {
			return (PhaseHeader) derStream.decode(type);
		}
	}
}