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
******************************************************************************
	murmur/whisper would not be possible without the ideas, implementation, 
	brilliance and passion of the erights.org community, which is also this software's 
	foundation.  In particular, I would like to thank the following individuals:
	        Mark Miller
	        Marc Stiegler
	        Bill Franz
	        Tyler Close 
	        Kevin Reid
********************************************************************************/

package club.callistohouse.session;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import club.callistohouse.utils.ArrayUtil;

public class SessionIdentity implements Serializable {
	private static final long serialVersionUID = -7482553455550775031L;

	private String vatId;
	private String domain;
	private InetSocketAddress isa;
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public SessionIdentity(int port) throws UnknownHostException {
		this("", port);
	}
	public SessionIdentity(String domain, int port) throws UnknownHostException {
		this(domain, new InetSocketAddress(port));
	}
	public SessionIdentity(InetSocketAddress addr) {
		this("", addr);
	}
	public SessionIdentity(String domain, InetSocketAddress addr) {
		this.domain = domain;
		this.isa = addr;
		generateKeyPair();
		generateVatId();
	}
	public SessionIdentity(String domain, InetSocketAddress addr, String vatId) {
		this.domain = domain;
		this.isa = addr;
		this.vatId = vatId;
		generateKeyPair();
	}

	public String getDomain() { return domain; }
	public String getVatId() { return vatId; }
	public InetSocketAddress getSocketAddress() { return isa; }
	public int getPort() { return isa.getPort(); }
	public InetAddress getAddress() { return isa.getAddress(); }
	public PublicKey getPublicKey() { return publicKey; }

	public void setDomain(String domain) { this.domain = domain; }
	public void setVatId(String vatId) { this.vatId = vatId; }
	public void setSocketAddress(InetSocketAddress addr) { this.isa = addr; } 
	public void setPublicKey(PublicKey publicKey) { this.publicKey = publicKey; }

	public SessionIdentity asPublicCopy() {
		SessionIdentity id = new SessionIdentity(domain, getSocketAddress(), getVatId());
		id.publicKey = getPublicKey();
		return id;
	}

	public String toString() {
		return getSocketAddress() + "/" + getVatId(); 
	} 

	public byte[] getSignatureBytes(byte[] bytes) throws SignatureException {
        Signature dsa = null;
		try {
			dsa = Signature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

        try {
			dsa.initSign(privateKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        dsa.update(bytes);
		return dsa.sign();
	}
	public void verifySignature(byte[] bytes, byte[] signatureBytes) throws SignatureException {
        Signature dsa = null;
		try {
			dsa = Signature.getInstance("SHA1withRSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

        try {
			dsa.initVerify(publicKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        dsa.update(bytes);
		if(!dsa.verify(signatureBytes)) {
			throw new SignatureException();
		} 
	}

	private void generateKeyPair() {
		try {
			/*KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");*/
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			kpg.initialize(2048, random);
			KeyPair kp = kpg.generateKeyPair();
			publicKey = kp.getPublic();
			privateKey = kp.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			return;
		}
	}
	private String generateVatId() {
		if (getVatId() != null)
			return vatId;
		if(getPublicKey() == null)
			throw new RuntimeException("no public key");
		byte[] vatIdBytes = null;
		byte[] forwardBytes = getPublicKey().getEncoded(); 
		if((forwardBytes[0] & 0x80) > 0) {
			forwardBytes = ArrayUtil.concatAll(new byte[] { 0x00 }, vatIdBytes);
		}
		try {
			vatIdBytes = MessageDigest.getInstance("SHA1").digest(forwardBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if((vatIdBytes[0] & 0x80) > 0) {
			vatIdBytes = ArrayUtil.concatAll(new byte[] { 0x00 }, vatIdBytes);
		}
		vatId = Base64.getEncoder().encodeToString(vatIdBytes);
		return vatId;
	}
}
