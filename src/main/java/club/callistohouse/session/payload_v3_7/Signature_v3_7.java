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

package club.callistohouse.session.payload_v3_7;

import club.callistohouse.session.payload.MessageEnum;
import club.callistohouse.session.payload.PhaseHeader;

public class Signature_v3_7 extends PhaseHeader {

    private byte[] signature;

	public Signature_v3_7() {
	}

	public Signature_v3_7(byte[] signature) {
		this.signature = signature;
	}

    public byte[] getSignature() { return signature; }
    public void setSignature(byte[] bytes) { this.signature = bytes; recomputeSpec(); }

	public MessageEnum getType() { return MessageEnum.SIGNATURE_V3_7; }

	public String toString() { return getClass().getSimpleName() + "(" 
			+ getSignature() + ")"; 
	}
}
