package com.android.hadstore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.commons.codec.binary.Hex;

/**
 * 
 * @author
 */
public class Encoder {

	public static final String SHA1 = "sha1";

	public static final String encrypt(String plain, String algorithm) throws NoSuchAlgorithmException {
		if (plain != null && plain.length()>0) {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(plain.getBytes());
			plain = new String(Hex.encodeHex(md.digest()));
		}
		return plain;
	}

	public static final String encryptToSha(String plain) throws NoSuchAlgorithmException {
		return encrypt(plain, SHA1);
	}

	public static String timeStamp() {
		Calendar cal = Calendar.getInstance();
		String s = String.format("%04d%02d%02d%02d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		return s;
	}

}
