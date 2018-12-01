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

package club.callistohouse.session.payload_core;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class RSAPublicKey {

	private BigInteger exponent;
	private BigInteger modulo;

	public RSAPublicKey() {}
	public RSAPublicKey(BigInteger modulo, BigInteger exponent) { this.modulo = modulo; this.exponent = exponent; }
	public RSAPublicKey(java.security.interfaces.RSAPublicKey key) { this.modulo = key.getModulus(); this.exponent = key.getPublicExponent(); }

	public BigInteger getExponent() { return exponent; }
	public void setExponent(BigInteger exponent) { this.exponent = exponent; }
	public BigInteger getModulo() { return modulo; }
	public void setModulo(BigInteger modulo) { this.modulo = modulo; }

	public java.security.interfaces.RSAPublicKey asImpl() throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulo, exponent);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (java.security.interfaces.RSAPublicKey) kf.generatePublic(keySpec);
	}
}
