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

package club.callistohouse.session.marshmuck;

import java.util.ArrayList;
import java.util.List;

import club.callistohouse.utils.ArrayUtil;

public class MessageLogger {

	private List<byte[]> localMessagesToSign = new ArrayList<byte[]>();
	private List<byte[]> remoteMessagesToSign = new ArrayList<byte[]>();

	public void addLocalMessage(byte[] msg) {
		this.localMessagesToSign.add(msg);
	}
	public void addRemoteMessage(byte[] msg) {
		this.remoteMessagesToSign.add(msg);
	}

	public byte[] getLocalMessagesBytes() {
		byte[] result = new byte[0];
		for(byte[] msg : localMessagesToSign) {
			result = ArrayUtil.concatAll(result, msg);
		}
		return result;
	} 
	public byte[] getRemoteMessagesBytes() {
		byte[] result = new byte[0];
		for(byte[] msg : remoteMessagesToSign) {
			result = ArrayUtil.concatAll(result, msg);
		}
		return result;
	} 

	public static class NullMessageLogger extends MessageLogger {

		public void addLocalMessage(byte[] msg) {} 
		public void addRemoteMessage(byte[] msg) {}
		public byte[] getLocalMessagesBytes() {
			return new byte[0];
		} 
		public byte[] getRemoteMessagesBytes() {
			return new byte[0];
		} 
	}
}

