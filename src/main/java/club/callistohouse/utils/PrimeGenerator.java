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
import java.security.SecureRandom;

public class PrimeGenerator {

	private static BigInteger two() { return BigInteger.ONE.add(BigInteger.ONE); }
	int minBitLength;
	int certainty;
	SecureRandom sr;

	public PrimeGenerator(int minBitLength, int certianty, SecureRandom sr) {
		if(minBitLength < 512) throw new IllegalArgumentException("Strong/Safe primes must be at least 64 bytes long");
		this.minBitLength = minBitLength;
		this.certainty = certianty;
		this.sr = sr;
	}

	public BigInteger getStrongPrime() {
		BigInteger s = new BigInteger(minBitLength/2-8, certainty, sr);
		BigInteger t = new BigInteger(minBitLength/2-8, certainty, sr);
		BigInteger i = BigInteger.ONE;
		BigInteger r;
		do {
			r = two().multiply(i).multiply(t).add(BigInteger.ONE);
			i = i.add(BigInteger.ONE);
		} while (!r.isProbablePrime(certainty));
		BigInteger z = s.modPow(r.subtract(two()), r);
		BigInteger pstar = two().multiply(z).multiply(s).subtract(BigInteger.ONE);
		BigInteger k = BigInteger.ONE;
		BigInteger p = two().multiply(r).multiply(s).add(pstar);
		while(!p.isProbablePrime(certainty)) {
			k = k.add(BigInteger.ONE);
			p = two().multiply(k).multiply(r).multiply(s).add(pstar);
		}
		return p;
	}
	public BigInteger getSafePrime() {
		BigInteger p;
		BigInteger r = BigInteger.valueOf(0x7fffffff);
		BigInteger t = new BigInteger(minBitLength-30, certainty, sr);
		do {
			r = r.add(BigInteger.ONE);
			p = two().multiply(r).multiply(t).add(BigInteger.ONE);
		} while (!p.isProbablePrime(certainty));
		return p;
	}
}
