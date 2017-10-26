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

import java.util.ArrayList;
import java.util.List;

public class ReplyInfo extends PhaseHeader {

	private List<String> cryptoProtocols;
	private List<String> dataEncoders;

	public ReplyInfo() {}
    public ReplyInfo(List<String> cryptoProtocols, List<String> dataEncoders) { this.cryptoProtocols = cryptoProtocols; this.dataEncoders = dataEncoders; }

    public List<String> getCryptoProtocols() { return cryptoProtocols; }
    public void setCryptoProtocols(ArrayList<String> cryptoProtocols) { this.cryptoProtocols = cryptoProtocols; }
    public List<String> getDataEncoders() { return dataEncoders; }
    public void setDataEncoders(ArrayList<String> dataEncoders) { this.dataEncoders = dataEncoders; }

	public MessageEnum getType() { return MessageEnum.REPLY_INFO; }

	public String toString() {
		return getClass().getSimpleName() + "("
			+ getCryptoProtocols() + ", "
			+ getDataEncoders() + ")";
	}
}
