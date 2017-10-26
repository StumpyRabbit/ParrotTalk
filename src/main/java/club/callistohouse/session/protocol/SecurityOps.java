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

package club.callistohouse.session.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import club.callistohouse.session.Session;
import club.callistohouse.session.SessionAgentMap;
import club.callistohouse.session.SessionIdentity;
import club.callistohouse.session.marshmuck.DiffieHellman;
import club.callistohouse.session.marshmuck.HmacSHA1;
import club.callistohouse.session.marshmuck.MessageLogger;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.MAC;
import club.callistohouse.session.payload.PhaseHeader;
import club.callistohouse.utils.ArrayUtil;

public class SecurityOps implements Cloneable {

	public static void writePublicKey(PublicKey publicKey, DataOutputStream outStream) throws IOException {
		byte[] bytes = publicKey.getEncoded();
		outStream.writeShort(bytes.length);
		outStream.write(bytes);
	}

	public static PublicKey readPublicKey(DataInputStream inStream) throws IOException {
		int length = inStream.readShort();
		byte[] bytes = new byte[length];
		inStream.read(bytes);
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytes);
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			return keyFactory.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	private CipherThunkMaker cryptoProtocol;
	private boolean isIncoming = false;
	private DiffieHellman diffieHellman = DiffieHellman.defaultDiffieHellman();
	private MessageLogger messageLogger = new MessageLogger();
	private byte[] macBytes;
	IvParameterSpec sendIv = null;
	IvParameterSpec receiveIv = null;
	// byte[] pbePassword;
	SecretKeySpec secretKeySpec;
	SessionAgentMap map;

	public SecurityOps(String proto) {
		this.cryptoProtocol = lookupCryptoProtocol(proto);
	}

	public SecurityOps(SessionAgentMap map) {
		this.map = map;
	}

	public SecurityOps clone() throws CloneNotSupportedException {
		return (SecurityOps) super.clone();
	}

	public SessionAgentMap getSessionAgentMap() { return map; }

	public void makeNullLogging() {
		messageLogger = new MessageLogger.NullMessageLogger();
	}

	public void addLocalMessage(byte[] msg) {
		messageLogger.addLocalMessage(msg);
	}

	public void addRemoteMessage(byte[] msg) {
		messageLogger.addRemoteMessage(msg);
	}

	public String getDefaultAlgorithm() {
		return "AESede";
	}

	public String getDefaultEncoder() {
		return "asn1der";
	}

	public String getCryptoProtocolString() {
		return cryptoProtocol.shortCryptoProtocol;
	}

	public void setCryptoProtocol(String proto) {
		this.cryptoProtocol = lookupCryptoProtocol(proto);
	}

	public String getFullCryptoProtocol() {
		return cryptoProtocol.fullCryptoProtocol;
	}

	public boolean isIncoming() {
		return isIncoming;
	}

	public void setIsIncoming(boolean isIncoming) {
		this.isIncoming = isIncoming;
	}

	private DiffieHellman getDiffieHellman() {
		return diffieHellman;
	}

	public MessageLogger getMessageLogger() {
		return messageLogger;
	}

	public HmacSHA1 getHmac() {
		return new HmacSHA1(macBytes);
	}

	public byte[] getLocalMessagesBytes() {
		return messageLogger.getLocalMessagesBytes();
	}

	public byte[] getRemoteMessagesBytes() {
		return messageLogger.getRemoteMessagesBytes();
	}

	public byte[] getDhParam() throws NoSuchAlgorithmException {
		return getDiffieHellman().sendMessage();
	}

	public void processOtherSideDhParam(byte[] otherSideDhParam, boolean isIncoming) throws NoSuchAlgorithmException {
		byte[] sharedKey = getDiffieHellman().receiveMessage(otherSideDhParam);
		try {
			generateMacKey(sharedKey);
			// generateIvSequences(cryptoProtocol.blockSize, sharedKey);
			// generatePBEPassword(sharedKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	private void generateMacKey(byte[] sharedKey) throws NoSuchAlgorithmException {
		macBytes = md5Hash(ArrayUtil.concatAll(padAndHash(new byte[] { 0x11 }, sharedKey),
				padAndHash(new byte[] { 0x22 }, sharedKey), padAndHash(new byte[] { 0x33 }, sharedKey),
				padAndHash(new byte[] { 0x44 }, sharedKey)));
		macBytes = ArrayUtil.concatAll(macBytes,
				md5Hash(ArrayUtil.concatAll(padAndHash(new byte[] { 0x55 }, sharedKey),
						padAndHash(new byte[] { 0x66 }, sharedKey), padAndHash(new byte[] { 0x77 }, sharedKey),
						padAndHash(new byte[] { (byte) 0x88 }, sharedKey))));
		macBytes = ArrayUtil.concatAll(macBytes,
				md5Hash(ArrayUtil.concatAll(padAndHash(new byte[] { (byte) 0x99 }, sharedKey),
						padAndHash(new byte[] { (byte) 0xAA }, sharedKey),
						padAndHash(new byte[] { (byte) 0xBB }, sharedKey),
						padAndHash(new byte[] { (byte) 0xCC }, sharedKey))));
	}
	/*
	 * private void generateIvSequences(int vectorSize, byte[] sharedKey) throws
	 * NoSuchAlgorithmException { byte[] bytes = null; byte[] hash = new byte[0];
	 * for(int count = 0; count < ((vectorSize * 2) / 16); count++) { byte bump =
	 * (byte) ((count * 0x33) & 0xFF); byte padByte = (byte) ((0x33 + bump) & 0xFF);
	 * hash = ArrayUtil.concatAll(hash, padAndHash(new byte[] { padByte },
	 * sharedKey)); }
	 * 
	 * log.debug("composite IV sequence: " + Hex.encodeHexString(hash)); if
	 * (isIncoming) { bytes = Arrays.copyOfRange(hash, 0, vectorSize); receiveIv =
	 * new IvParameterSpec(bytes); bytes = Arrays.copyOfRange(hash, vectorSize,
	 * vectorSize * 2); sendIv = new IvParameterSpec(bytes); } else { bytes =
	 * Arrays.copyOfRange(hash, 0, vectorSize); sendIv = new IvParameterSpec(bytes);
	 * bytes = Arrays.copyOfRange(hash, vectorSize, vectorSize * 2); receiveIv = new
	 * IvParameterSpec(bytes); } }
	 * 
	 * private Cipher getCipher(int mode) throws InvalidKeyException,
	 * NoSuchAlgorithmException, NoSuchPaddingException,
	 * InvalidAlgorithmParameterException { SecretKeySpec keySpec = buildKeySpec();
	 * log.debug("Cipher key: " + Hex.encodeHexString(keySpec.getEncoded())); Cipher
	 * cipher = Cipher.getInstance(getFullCryptoProtocol());
	 * if(cryptoProtocol.hasIvParameter) { IvParameterSpec ivSpec; if (mode ==
	 * Cipher.ENCRYPT_MODE) { ivSpec = sendIv; } else { ivSpec = receiveIv; }
	 * cipher.init(mode, keySpec, ivSpec); } else { cipher.init(mode, keySpec); }
	 * return cipher; }
	 * 
	 * private SecretKeySpec buildKeySpec() { if(secretKeySpec != null) { return
	 * secretKeySpec; }
	 * 
	 * byte[] sharedKey = getDiffieHellman().getSharedKey(); if (sharedKey == null)
	 * return null; byte[] keyBytes; if(sharedKey.length == cryptoProtocol.keySize)
	 * { keyBytes = sharedKey; } else { keyBytes = Arrays.copyOf(sharedKey,
	 * cryptoProtocol.keySize); if(sharedKey.length < cryptoProtocol.keySize) {
	 * Arrays.fill(keyBytes, sharedKey.length, cryptoProtocol.keySize, (byte) 0x66);
	 * } } secretKeySpec = new SecretKeySpec(keyBytes,
	 * cryptoProtocol.fullCryptoProtocol.split("/")[0]); return secretKeySpec; }
	 */

	public static byte[] md5Hash(byte[] sourceBytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return md.digest(sourceBytes);
	}

	public static byte[] padAndHash(byte[] padBytes, byte[] secret) throws NoSuchAlgorithmException {
		byte[] paddedBytes = new byte[16];
		Arrays.fill(paddedBytes, padBytes[0]);
		return MessageDigest.getInstance("MD5").digest(ArrayUtil.concatAll(paddedBytes, secret));
	}

	public static SecurityOps nullSecrets() {
		return new SecurityOps("null");
	}

	private static List<CipherThunkMaker> cryptoProtocols = new ArrayList<CipherThunkMaker>();

	public static CipherThunkMaker lookupCryptoProtocol(String proto) {
		for (CipherThunkMaker crypto : cryptoProtocols) {
			if (crypto.shortCryptoProtocol.equals(proto))
				return crypto;
		}
		return null;
	}

	public static List<String> getCryptoProtocols() {
		List<String> list = new ArrayList<String>();
		for (CipherThunkMaker crypto : cryptoProtocols) {
			list.add(crypto.shortCryptoProtocol);
		}
		return list;
	}

	private static List<EncoderThunkMaker> dataEncoders = new ArrayList<EncoderThunkMaker>();

	public static EncoderThunkMaker lookupDataEncoder(String proto) {
		for (EncoderThunkMaker encoder : dataEncoders) {
			if (encoder.encoderName.equals(proto))
				return encoder;
		}
		return null;
	}

	public static List<String> getDataEncoders() {
		List<String> list = new ArrayList<String>();
		for (EncoderThunkMaker encoder : dataEncoders) {
			list.add(encoder.encoderName);
		}
		return list;
	}

	static {
		cryptoProtocols.add(new CipherThunkMaker("AESede", "AES/CBC/PKCS5Padding", 32, 16, true));
		cryptoProtocols.add(new CipherThunkMaker("DESede", "DESede/CBC/PKCS5Padding", 24, 8, true));
		dataEncoders.add(new EncoderThunkMaker("asn1der") {

			@Override
			public Object preSerializeThunk(Frame frame) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object postSerializeThunk(Object frame) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object preMaterializeThunk(Frame frame) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object postMaterializeThunk(Object frame) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	public void installOn(Session session, ThunkStack stack, boolean incoming) {
		stack.pop(); // session
		stack.push(makeImmigrationThunk(stack));
		stack.push(makeCipherThunk(stack, incoming));
		stack.push(makeCustomsThunk(stack));
		stack.push(makeEncoderThunk(stack, session.getFarKey()));
		stack.push(session);
	}

	private Thunk makeCustomsThunk(ThunkStack stack) {
		HmacSHA1 hmac = getHmac();
		return new Thunk(false, false) {
			public Frame downThunk(Frame frame) {
				try {
					stack.propertyAtPut("WriteMAC", hmac.computeMAC((byte[]) frame.getPayload()));
				} catch (IOException e) {
					throw new RuntimeException("customs failed MAC verification");
				}
				return frame;
			}

			public Frame upThunk(Frame frame) {
				try {
					if (!(hmac.computeMAC((byte[]) frame.getPayload()).equals(stack.propertyAt("ReadMAC")))) {
						throw new RuntimeException("customs failed MAC verification");
					}
				} catch (IOException e) {
					throw new RuntimeException("customs failed MAC verification");
				}
				return frame;
			}
		};
	}

	private Thunk makeImmigrationThunk(ThunkStack stack) {
		return new Thunk() {
			public Frame downThunk(Frame frame) {
				return frame;
			}

			public Frame upThunk(Frame frame) {
				stack.propertyAtPut("ReadMAC", ((MAC) frame.getHeader()).getMAC());
				return frame;
			}

			public PhaseHeader getHeader(Frame frame) {
				return new MAC((byte[]) stack.propertyAt("WriteMAC"));
			}
		};
	}

	private Thunk makeEncoderThunk(ThunkStack stack, SessionIdentity farKey) {
		return map.buildEncoder(farKey).makeThunk();
	}

	private Thunk makeCipherThunk(ThunkStack stack, boolean incoming) {
		List<byte[]> secretKeyHolder = new ArrayList<byte[]>();
		secretKeyHolder.add(diffieHellman.getSharedKey());
		return map.buildProtocol().makeThunk(secretKeyHolder, incoming);
	}

	public void clearSensitiveInfo() {
		// TODO Auto-generated method stub

	}
}
