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

import java.io.Serializable;

public enum MessageEnum implements Serializable {
	PROTOCOL_OFFERED(1) {
		public String description() { return "protocol-offered"; } },
	PROTOCOL_ACCEPTED(3) { 
		public String description() { return "protocol-accepted"; } },
    ENCODED_DATA(59) {
		public String description() { return "encoded-data"; }},
    ENCRYPTED_DATA(62) {
		public String description() { return "encrypted-data"; }},
	MAC_DATA(7) {
		public String description() { return "mac-data"; } },
    I_WANT(8) {
		public String description() { return "i-want"; }},
    I_AM(9) {
		public String description() { return "i-am"; }},
    GIVE_INFO(10) {
		public String description() { return "give-info"; }},
    REPLY_INFO(11) {
		public String description() { return "reply-info"; }},
    GO(12) {
		public String description() { return "go"; }},
    GO_TOO(13) {
		public String description() { return "go-too"; }},
    DUPLICATE_CONNECTION(14) {
		public String description() { return "duplicate-connection"; }},
    NOT_ME(15) {
			public String description() { return "not-me"; }},
    RAW_DATA(30) {
		public String description() { return "raw-data"; }},
	INTERNAL_CHANGE_ENCRYPTION(31) {
		public boolean isInternalChange() { return true; }
		public String description() { return "internal change encryption"; } };

	private int code;

	private MessageEnum(int code) {
		this.code = code;
	}

	public MessageEnum valueOf(int code) {
		for (MessageEnum p : values()) {
			if (p.getCode() == code)
				return p;
		}
		return null;
	}

	public Byte getId() { return (byte) getCode(); }
	public int getCode() { return code; }
	public boolean isInternalChange() { return false; }

	public abstract String description();

	public String toString() {
		return this.name() + "(" + Integer.toBinaryString(getCode()) + ")";
	}
}
