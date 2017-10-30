package club.callistohouse.session;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import club.callistohouse.session.payload.Encrypted;
import club.callistohouse.session.payload.Frame;
import club.callistohouse.session.payload.PhaseHeader;
import club.callistohouse.session.protocol.SecurityOps;
import club.callistohouse.session.protocol.Thunk;
import club.callistohouse.utils.ArrayUtil;

public class CipherThunkMaker implements Cloneable {
	public String shortCryptoProtocol;
	public String fullCryptoProtocol;
	public int keySize = 24;
	public int blockSize = 8;
	public boolean hasIvParameter = false;
	private SecretKeySpec secretKeySpec;
	List<byte[]> ivHolder = new ArrayList<byte[]>(1);

	public CipherThunkMaker(String shortName, String fullName, int keySize, int blockSize, boolean hasIv) {
		this.shortCryptoProtocol = shortName;
		this.fullCryptoProtocol = fullName;
		this.keySize = keySize;
		this.blockSize = blockSize;
		this.hasIvParameter = hasIv;
		ivHolder.add(new byte[0]);
	}
	public CipherThunkMaker newMaker() throws CloneNotSupportedException { return (CipherThunkMaker) clone(); }

	public Thunk makeThunk(List<byte[]> secretBytesHolder, boolean incoming) {
		Cipher downCipher = buildCipher(secretBytesHolder.get(0), incoming, Cipher.ENCRYPT_MODE);
		Cipher upCipher = buildCipher(secretBytesHolder.get(0), incoming, Cipher.DECRYPT_MODE);

		return new Thunk() {
			public Object downThunk(Frame frame) {
				ivHolder.set(0, downCipher.getIV());
				byte[] encryptedBytes = new byte[0];
				try {
					encryptedBytes = downCipher.doFinal(frame.toByteArray());
				} catch (IllegalBlockSizeException e1) {
					e1.printStackTrace();
				} catch (BadPaddingException e1) {
					e1.printStackTrace();
				}
				if(hasIvParameter) {
					byte[] newIv = Arrays.copyOfRange(encryptedBytes, (encryptedBytes.length - blockSize), encryptedBytes.length);
					try {
						downCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(newIv));
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					}
				} else {
					try {
						downCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					}
				}
				//log.debug("encrypt results: " + Hex.encodeHexString(encryptedBytes));
				return encryptedBytes;
			}
			public Object upThunk(Frame frame) {
//				upCipher hasVector ifTrue: [upCipher initialVector: frame header ivSequence].
//				upCipher decrypt: frame payload ];
				byte[] encryptedBytes = (byte[]) frame.getPayload();
				if(hasIvParameter) {
					IvParameterSpec ivSpec = new IvParameterSpec(((Encrypted)frame.getHeader()).getIvSequence());
					try {
						upCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					}
				} else {
					try {
						upCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					}
				}
				//log.debug("vector: " + Hex.encodeHexString(((Encrypted)frame.getHeader()).getIVSequence()));
				//log.debug("decrypting encrypted: " + Hex.encodeHexString(encryptedBytes));
				byte[] unencryptedBytes = new byte[0];
				try {
					unencryptedBytes = upCipher.doFinal(encryptedBytes);
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
				//log.debug("decrypt results: " + Hex.encodeHexString(unencryptedBytes));
				return unencryptedBytes;
			}
			public PhaseHeader getHeader(Frame frame) { return new Encrypted(ivHolder.get(0)); }
		};
	}

	private Cipher buildCipher(byte[] secretBytes, boolean incoming, int cryptMode) {
		byte[] keyBytes;
		if(secretBytes.length >= keySize) {
			keyBytes = Arrays.copyOf(secretBytes, keySize);
		} else {
			keyBytes = Arrays.copyOf(secretBytes, keySize);
			Arrays.fill(keyBytes, secretBytes.length, keySize, (byte) 0x98); 
		}
		secretKeySpec = new SecretKeySpec(keyBytes, fullCryptoProtocol.split("/")[0]);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(fullCryptoProtocol);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		if(hasIvParameter) {
			try {
					cipher.init(cryptMode, secretKeySpec, computeIVSpec(secretBytes, incoming, cryptMode));
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		} else {
			try {
				cipher.init(cryptMode, secretKeySpec);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		}
		return cipher;
	}
	private IvParameterSpec computeIVSpec(byte[] secretBytes, boolean incoming, int cryptMode) {
		IvParameterSpec ivSpec = null;
		byte[] hash = computeIVHash(secretBytes);
		if (incoming) {
			if (cryptMode == Cipher.ENCRYPT_MODE) {
				ivSpec = new IvParameterSpec(Arrays.copyOfRange(hash, blockSize, blockSize * 2));
			} else {
				ivSpec = new IvParameterSpec(Arrays.copyOfRange(hash, 0, blockSize));
			}
		} else {
			if (cryptMode == Cipher.ENCRYPT_MODE) {
				ivSpec = new IvParameterSpec(Arrays.copyOfRange(hash, 0, blockSize));
			} else {
				ivSpec = new IvParameterSpec(Arrays.copyOfRange(hash, blockSize, blockSize * 2));
			}
		}
		return ivSpec;
	}
	private byte[] computeIVHash(byte[] secretBytes) {
		byte[] hash = new byte[blockSize * 2]; 
		for(int count = 0; count < (blockSize * 2) / 16; count++) {
			byte bump = (byte) ((count * 0x33) & 0xFF);
			byte padByte = (byte) ((0x33 + bump) & 0xFF);
			try {
				hash = ArrayUtil.concatAll(hash, SecurityOps.padAndHash(new byte[] { padByte }, secretBytes));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return hash;
	}
}
