package com.jmhy.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;
import android.util.Base64;

public class SecurityUtils {

	private static String LOGTAG = "SecurityUtils";

	private static final byte[] SECRET_KEY;

	static {
		SECRET_KEY = getMD516(getMD516("DSJFIVJDG845234").substring(3, 12))
				.getBytes();

	}

	/*
	 * MD5加密,返回32位小写
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// // 16位加密，从第9位到25位
		// return md5StrBuff.substring(8, 24).toString().toUpperCase();
		return md5StrBuff.toString().toLowerCase();
	}

	/*
	 * MD5加密,返回16位大写
	 */
	public static String getMD516(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// // 16位加密，从第9位到25位
		return md5StrBuff.substring(8, 24).toString();

	}

	/**
	 * 解密
	 * 
	 * @param Value
	 * @return
	 */
	public static String decrypt(String value) {
		if (TextUtils.isEmpty(value))
			return "";

		byte[] bytes = Base64.decode(Utils.getUTF8Bytes(value), Base64.DEFAULT);

		if (bytes == null)
			return "";

		bytes = new Crypter().decrypt(bytes, SECRET_KEY);
		if (bytes == null)
			return "";

		return Utils.getUTF8String(bytes);
	}

	/**
	 * 加密
	 * 
	 * @param value
	 * @return
	 */
	public static String encrypt(String value) {

		if (TextUtils.isEmpty(value))
			return "";

		byte[] bytes = Utils.getUTF8Bytes(value);
		bytes = new Crypter().encrypt(bytes, SECRET_KEY);
		bytes = Base64.encode(bytes, Base64.DEFAULT);
		return Utils.getUTF8String(bytes);
	}
}
