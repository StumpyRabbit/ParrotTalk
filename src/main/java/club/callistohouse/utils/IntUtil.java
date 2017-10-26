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

package club.callistohouse.utils;

public class IntUtil {

	public static final byte[] intToByteArray(int value) {
		return new byte[] { 
				(byte) (value >>> 24), 
				(byte) (value >>> 16),
				(byte) (value >>> 8), 
				(byte) value };
	}

	public static final byte[] longToByteArray(long value) {
		return new byte[] { 
				(byte) (value >>> 56), 
				(byte) (value >>> 48), 
				(byte) (value >>> 40), 
				(byte) (value >>> 32), 
				(byte) (value >>> 24), 
				(byte) (value >>> 16),
				(byte) (value >>> 8), 
				(byte) value };
	}

	public static final int byteArrayToInt(byte[] b) {
		return 
				(b[0] << 24) 
				+ ((b[1] & 0xFF) << 16) 
				+ ((b[2] & 0xFF) << 8) 
				+ (b[3] & 0xFF);
	}

	public static final int byteArrayToLong(byte[] b) {
		return 
				(b[0] << 56) 
				+ ((b[1] & 0xFF) << 48) 
				+ ((b[2] & 0xFF) << 40) 
				+ ((b[3] & 0xFF) << 32) 
				+ ((b[4] & 0xFF) << 24) 
				+ ((b[5] & 0xFF) << 16) 
				+ ((b[6] & 0xFF) << 8) 
				+ (b[7] & 0xFF);
	}

	public static int byteArrayToInt(int[] lengthBytes) {
		return 
				(lengthBytes[0] << 24) 
				+ ((lengthBytes[1] & 0xFF) << 16) 
				+ ((lengthBytes[2] & 0xFF) << 8) 
				+ (lengthBytes[3] & 0xFF);
	}

	public static long byteArrayToLong(int[] lengthBytes) {
		return 
				(lengthBytes[0] << 56) 
				+ ((lengthBytes[1] & 0xFF) << 48) 
				+ ((lengthBytes[2] & 0xFF) << 40) 
				+ ((lengthBytes[3] & 0xFF) << 32) 
				+ ((lengthBytes[4] & 0xFF) << 24) 
				+ ((lengthBytes[5] & 0xFF) << 16) 
				+ ((lengthBytes[6] & 0xFF) << 8) 
				+ (lengthBytes[7] & 0xFF);
	}
}
