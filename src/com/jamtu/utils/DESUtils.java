package com.jamtu.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;
import android.util.Base64;

/**
 * DES加密工具类
 * 
 */
public class DESUtils {

	/**
	 * 默认KEY
	 */
	private static final String CURRENT_KEY = "cst_2015";

	private static final byte[] IV = { 1, 2, 3, 4, 5, 6, 7, 8 };

	// 密钥集合 包含 当前密钥 以及 以前用过的所有密钥，缺一不可
	private static final String[] KEYS = { CURRENT_KEY };

	// 向量
	private IvParameterSpec spec;
	// 密匙
	private Key key;

	public DESUtils() {
		this(CURRENT_KEY);
	}

	/**
	 * 实例化时并初始化密匙
	 * 
	 * @param str
	 */
	public DESUtils(String str) {
		spec = new IvParameterSpec(IV);
		setKey(str);
	}

	/**
	 * 加密 String 明文输入 ,String 密文输出
	 * 
	 * @param strMing
	 * @return
	 */
	public String encryptStr(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			byteMing = strMing.getBytes("UTF8");
			byteMi = this.encryptByte(byteMing);
			strMi = Base64.encodeToString(byteMi, Base64.DEFAULT);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			byteMing = null;
			byteMi = null;
		}
		try {
			strMi = strMi.replace("\r\n", "");
			strMi = URLEncoder.encode(strMi, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strMi;
	}

	/**
	 * 解密 以 String 密文输入 ,String 明文输出
	 * 
	 * @param strMi
	 * @return
	 */
	public String decryptStr(String strMi) {
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			strMi = URLDecoder.decode(strMi, "UTF-8");
			byteMi = Base64.decode(strMi, Base64.DEFAULT);
			byteMing = this.decryptByte(byteMi);
			strMing = new String(byteMing, "UTF8");
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * 查找并重置密钥
	 * 
	 * @param strMi
	 * @return
	 * 
	 *         -1 找不到对应密钥，未加密 需要将明文加密为密文
	 * 
	 *         1 密钥不一致 需要将密文数据替换新的加密数据
	 * 
	 *         2 密钥一致 不需要将密文数据替换新的加密数据
	 */
	public int findAndSetKey(String strMi) {
		int isFindAndSet = -1;
		for (String k : KEYS) {
			try {
				setKey(k);
				String strMing = decryptStr(strMi);
				if (!TextUtils.isEmpty(strMing)) {
					isFindAndSet = 1;
					isFindAndSet = k.equals(CURRENT_KEY) ? 2 : isFindAndSet;
					break;
				}
			} catch (Exception e) {
			}
		}
		return isFindAndSet;
	}

	/**
	 * 加密以 byte[] 明文输入 ,byte[] 密文输出
	 * 
	 * @param byteE
	 * @return
	 */
	private byte[] encryptByte(byte[] byteE) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, spec);
			byteFina = cipher.doFinal(byteE);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 解密以 byte[] 密文输入 , 以 byte[] 明文输出
	 * 
	 * @param byteD
	 * @return
	 */
	private byte[] decryptByte(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, spec);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 生成加密 KEY
	 */
	public void setKey(String strKey) {
		this.key = new SecretKeySpec(strKey.getBytes(), "DES");
	}
}
