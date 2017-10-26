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
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 * ******************************************************************************
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the erights.org community, which is also this software's 
 * foundation.  In particular, I would like to thank the following individuals:
 *         Mark Miller
 *         Marc Stiegler
 *         Bill Franz
 *         Tyler Close 
 *         Kevin Reid
 *******************************************************************************/
package club.callistohouse.utils;

import java.math.BigInteger;

public class Base64Encoder {

	private static byte[] base64Table = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz=_".getBytes();

	public static BigInteger toBase64(BigInteger base) {
		return new BigInteger(toBase64(base.toByteArray()));
	}

	public static byte[] toBase64(byte[] bytes) {
		int firstIndex, secondIndex, thirdIndex, fourthIndex;
		byte[] encoded = new byte[bytes.length * 4 / 3];
		for(int i = 0; i < bytes.length - 2; i += 3) {
			firstIndex = (bytes[i] & 0xFC) >> 2;
			secondIndex = ((bytes[i] & 0x03) << 4) | ((bytes[i+1] & 0xF0) >> 4);
			thirdIndex = ((bytes[i+1] & 0x0F) << 2) | ((bytes[i+2] & 0xC0) >> 6);
			fourthIndex = (bytes[i+2] & 0x3F);
			encoded[i] = base64Table[firstIndex];
			encoded[i+1] = base64Table[secondIndex];
			encoded[i+2] = base64Table[thirdIndex];
			encoded[i+3] = base64Table[fourthIndex];
		}
		int extra = bytes.length - (bytes.length % 3 * 3);
		if(extra == 0) {
		} else if(extra == 1) {
			firstIndex = ((bytes[bytes.length - 1]) & 0xFC) >> 2;
			secondIndex = ((bytes[bytes.length - 1]) & 0x03) << 4;
			encoded[bytes.length - 2] = base64Table[firstIndex];
			encoded[bytes.length - 1] = base64Table[secondIndex];
		} else if(extra == 2) {
			firstIndex = ((bytes[bytes.length - 2]) & 0xFC) >> 2;
			secondIndex = (((bytes[bytes.length - 2]) & 0x03) << 4) | (((bytes[bytes.length - 1]) & 0xF0) >> 4);
			thirdIndex = ((bytes[bytes.length - 1]) & 0x0F) << 2;
			encoded[bytes.length - 3] = base64Table[firstIndex];
			encoded[bytes.length - 2] = base64Table[secondIndex];
			encoded[bytes.length - 1] = base64Table[thirdIndex];
		}
		return encoded;
	}
}
