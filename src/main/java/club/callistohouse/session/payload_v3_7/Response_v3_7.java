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

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import club.callistohouse.session.payload.MessageEnum;
import club.callistohouse.session.payload.PhaseHeader;
import club.callistohouse.session.payload.RSAPublicKey;
import club.callistohouse.session.payload.Version37;

public class Response_v3_7 extends Version37 {

	private String vatId;
	private String domain;
	private RSAPublicKey publicKey;
	private String cryptoProtocol;
	private String dataEncoder;
    private byte[] diffieHellmanParam;
    private byte[] signature;

	public Response_v3_7() {
	}

	public Response_v3_7(String vatId, String domain, PublicKey publicKey, String defaultAlgorithm, String defaultEncoder, byte[] dhParam, byte[] signature) {
		this.vatId = vatId;
		this.domain = domain;
		setPublicKeyImpl(publicKey);
		this.cryptoProtocol = defaultAlgorithm;
		this.dataEncoder = defaultEncoder;
		this.diffieHellmanParam = dhParam;
		this.signature = signature;
	}

	public String getVatId() { return vatId; }
    public String getDomain() { return domain; }
    public RSAPublicKey getPublicKey() { return publicKey; }
    public PublicKey getPublicKeyImpl() {
    	try {
			return publicKey.asImpl();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
    	return null;
    }
    public String getCryptoProtocol() { return cryptoProtocol; }
    public String getDataEncoder() { return dataEncoder; }
    public byte[] getDiffieHellmanParam() { return diffieHellmanParam; }
    public byte[] getSignature() { return signature; }
    public void setVatId(String id) { this.vatId = id; }
	public void setDomain(String domain) { this.domain = domain; }
    public void setPublicKey(RSAPublicKey publicKey) { this.publicKey = publicKey; }
    public void setPublicKeyImpl(PublicKey publicKey) { this.publicKey = new RSAPublicKey((java.security.interfaces.RSAPublicKey) publicKey); }
    public void setCryptoProtocol(String s) { this.cryptoProtocol = s; }
    public void setDataEncoder(String s) { this.dataEncoder = s; }
    public void setDiffieHellmanParam(byte[] bytes) { this.diffieHellmanParam = bytes; }
    public void setSignature(byte[] bytes) { this.signature = bytes; recomputeSpec(); }

	public MessageEnum getType() { return MessageEnum.RESPONSE_V3_7; }

	public String toString() { return getClass().getSimpleName() + "(" 
			+ getVatId() + ", " 
			+ getDomain() + ", " 
			+ getPublicKey() + ", " 
			+ getCryptoProtocol() + ", " 
			+ getDataEncoder() + ", " 
			+ getDiffieHellmanParam() + ", " 
			+ getSignature() + ")"; 
	}
}
